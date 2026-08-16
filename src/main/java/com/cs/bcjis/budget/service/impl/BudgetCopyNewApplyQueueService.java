package com.cs.bcjis.budget.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.cs.bcjis.budget.service.BudgetCopyNewApplyJob;
import com.cs.bcjis.budget.service.BudgetCopyNewService;

/**
 * 전년도예산조서적용[신규] 일괄적용 대기열.
 * - 동시 적용 요청을 FIFO로 직렬화해 DB 락/커넥션 경합을 완화한다.
 * - 워커 1개만 실행(동시 DB 적용 1건). Tomcat 재시작 시 메모리 큐는 초기화된다.
 */
@Service("budgetCopyNewApplyQueueService")
public class BudgetCopyNewApplyQueueService {

    private static final Logger logger = Logger.getLogger(BudgetCopyNewApplyQueueService.class);

    /** 동시에 DB에 적용하는 작업 수(고정 1: 20명 동시 클릭 경합 완화) */
    private static final int WORKER_COUNT = 1;
    /** 대기열 최대 적재 수 */
    private static final int MAX_QUEUE_SIZE = 100;
    /** 완료/오류 Job 보관 시간(ms) */
    private static final long JOB_TTL_MS = 60L * 60L * 1000L;
    /** 내부 적용 청크(진행률 갱신 단위) */
    private static final int APPLY_CHUNK = 40;

    @Resource(name = "budgetCopyNewService")
    private BudgetCopyNewService budgetCopyNewService;

    private final BlockingQueue<String> waitQueue = new LinkedBlockingQueue<String>();
    private final ConcurrentHashMap<String, BudgetCopyNewApplyJob> jobs =
            new ConcurrentHashMap<String, BudgetCopyNewApplyJob>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final List<Thread> workers = new ArrayList<Thread>();

    @PostConstruct
    public void startWorkers() {
        running.set(true);
        for (int i = 0; i < WORKER_COUNT; i++) {
            final int workerNo = i + 1;
            Thread t = new Thread(new Runnable() {
                public void run() {
                    workerLoop(workerNo);
                }
            }, "budget-copy-new-apply-worker-" + workerNo);
            t.setDaemon(true);
            t.start();
            workers.add(t);
        }
        logger.info("BudgetCopyNewApplyQueueService started. workers=" + WORKER_COUNT);
    }

    @PreDestroy
    public void stopWorkers() {
        running.set(false);
        for (int i = 0; i < workers.size(); i++) {
            try {
                workers.get(i).interrupt();
            } catch (Exception e) {
                // ignore
            }
        }
        logger.info("BudgetCopyNewApplyQueueService stopped.");
    }

    /**
     * 일괄적용 Job 등록. 사용자당 QUEUED/RUNNING 1건만 허용.
     */
    @SuppressWarnings("rawtypes")
    public BudgetCopyNewApplyJob enqueue(String userId, String userNm, List<Map> mappings) throws Exception {
        if (mappings == null || mappings.isEmpty()) {
            throw new IllegalArgumentException("mappings is empty");
        }
        if (userId == null || userId.trim().length() < 1) {
            throw new IllegalArgumentException("userId is empty");
        }

        purgeExpiredJobs();

        // 동일 사용자 중복 제출 방지
        BudgetCopyNewApplyJob existing = findActiveJobByUser(userId);
        if (existing != null) {
            existing.setQueuePos(calcQueuePos(existing.getJobId()));
            existing.setMessage("이미 대기/적용 중인 요청이 있습니다. 완료 후 다시 시도하십시오.");
            return existing;
        }

        if (waitQueue.size() >= MAX_QUEUE_SIZE) {
            throw new IllegalStateException("적용 대기열이 가득 찼습니다. 잠시 후 다시 시도하십시오.");
        }

        BudgetCopyNewApplyJob job = new BudgetCopyNewApplyJob();
        job.setJobId(UUID.randomUUID().toString().replace("-", ""));
        job.setUserId(userId);
        job.setUserNm(userNm == null ? "" : userNm);
        job.setStatus(BudgetCopyNewApplyJob.STATUS_QUEUED);
        job.setTotalCnt(mappings.size());
        job.setAppliedCnt(0);
        job.setMappings(new ArrayList<Map>(mappings));
        job.setMessage("대기열에 등록되었습니다.");

        jobs.put(job.getJobId(), job);
        if (!waitQueue.offer(job.getJobId())) {
            jobs.remove(job.getJobId());
            throw new IllegalStateException("적용 대기열 등록에 실패했습니다.");
        }

        job.setQueuePos(calcQueuePos(job.getJobId()));
        return snapshot(job);
    }

    public BudgetCopyNewApplyJob getJob(String jobId) {
        if (jobId == null || jobId.trim().length() < 1) {
            return null;
        }
        BudgetCopyNewApplyJob job = jobs.get(jobId);
        if (job == null) {
            return null;
        }
        if (BudgetCopyNewApplyJob.STATUS_QUEUED.equals(job.getStatus())) {
            job.setQueuePos(calcQueuePos(jobId));
        } else if (BudgetCopyNewApplyJob.STATUS_RUNNING.equals(job.getStatus())) {
            job.setQueuePos(0);
        }
        return snapshot(job);
    }

    /** 현재 대기열 요약(모니터링용) */
    public Map<String, Object> getQueueSummary() {
        Map<String, Object> m = new java.util.HashMap<String, Object>();
        m.put("queuedCnt", Integer.valueOf(waitQueue.size()));
        m.put("jobCnt", Integer.valueOf(jobs.size()));
        m.put("workerCnt", Integer.valueOf(WORKER_COUNT));
        return m;
    }

    private void workerLoop(int workerNo) {
        while (running.get()) {
            String jobId = null;
            try {
                jobId = waitQueue.take();
            } catch (InterruptedException ie) {
                if (!running.get()) {
                    break;
                }
                continue;
            }

            BudgetCopyNewApplyJob job = jobs.get(jobId);
            if (job == null) {
                continue;
            }

            processJob(job);
            purgeExpiredJobs();
        }
        logger.info("worker-" + workerNo + " exit");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void processJob(BudgetCopyNewApplyJob job) {
        job.setStatus(BudgetCopyNewApplyJob.STATUS_RUNNING);
        job.setStartedAt(System.currentTimeMillis());
        job.setQueuePos(0);
        job.setMessage("적용 처리 중...");

        try {
            List<Map> mappings = job.getMappings();
            int total = mappings == null ? 0 : mappings.size();
            int applied = 0;

            for (int from = 0; from < total; from += APPLY_CHUNK) {
                int to = Math.min(from + APPLY_CHUNK, total);
                List<Map> chunk = new ArrayList<Map>(mappings.subList(from, to));
                budgetCopyNewService.copyReportBatch(chunk);
                applied = to;
                job.setAppliedCnt(applied);
                job.setMessage("적용 처리 중... (" + applied + "/" + total + ")");
            }

            job.setAppliedCnt(total);
            job.setStatus(BudgetCopyNewApplyJob.STATUS_DONE);
            job.setMessage(total + "건 적용이 완료되었습니다.");
            job.setFinishedAt(System.currentTimeMillis());
            // 완료 후 매핑 대량 데이터 해제
            job.setMappings(new ArrayList<Map>());
        } catch (Exception e) {
            logger.error("processJob failed. jobId=" + job.getJobId() + ", userId=" + job.getUserId(), e);
            job.setStatus(BudgetCopyNewApplyJob.STATUS_ERROR);
            job.setMessage("적용 중 오류가 발생했습니다.");
            job.setFinishedAt(System.currentTimeMillis());
            job.setMappings(new ArrayList<Map>());
        }
    }

    private BudgetCopyNewApplyJob findActiveJobByUser(String userId) {
        Iterator<BudgetCopyNewApplyJob> it = jobs.values().iterator();
        while (it.hasNext()) {
            BudgetCopyNewApplyJob j = it.next();
            if (j == null || !userId.equals(j.getUserId())) {
                continue;
            }
            String st = j.getStatus();
            if (BudgetCopyNewApplyJob.STATUS_QUEUED.equals(st)
                    || BudgetCopyNewApplyJob.STATUS_RUNNING.equals(st)) {
                return j;
            }
        }
        return null;
    }

    private int calcQueuePos(String jobId) {
        // LinkedBlockingQueue iterator 순서는 FIFO
        int pos = 1;
        Iterator<String> it = waitQueue.iterator();
        while (it.hasNext()) {
            String id = it.next();
            if (jobId.equals(id)) {
                return pos;
            }
            pos++;
        }
        // 큐에 없으면 실행 중/완료
        return 0;
    }

    private void purgeExpiredJobs() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, BudgetCopyNewApplyJob>> it = jobs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, BudgetCopyNewApplyJob> e = it.next();
            BudgetCopyNewApplyJob j = e.getValue();
            if (j == null) {
                it.remove();
                continue;
            }
            String st = j.getStatus();
            if (!BudgetCopyNewApplyJob.STATUS_DONE.equals(st)
                    && !BudgetCopyNewApplyJob.STATUS_ERROR.equals(st)) {
                continue;
            }
            long fin = j.getFinishedAt() > 0 ? j.getFinishedAt() : j.getCreatedAt();
            if (now - fin > JOB_TTL_MS) {
                it.remove();
            }
        }
    }

    /** 폴링 응답용 스냅샷(매핑 본문 제외) */
    private BudgetCopyNewApplyJob snapshot(BudgetCopyNewApplyJob src) {
        BudgetCopyNewApplyJob s = new BudgetCopyNewApplyJob();
        s.setJobId(src.getJobId());
        s.setUserId(src.getUserId());
        s.setUserNm(src.getUserNm());
        s.setStatus(src.getStatus());
        s.setMessage(src.getMessage());
        s.setTotalCnt(src.getTotalCnt());
        s.setAppliedCnt(src.getAppliedCnt());
        s.setQueuePos(src.getQueuePos());
        s.setCreatedAt(src.getCreatedAt());
        s.setStartedAt(src.getStartedAt());
        s.setFinishedAt(src.getFinishedAt());
        return s;
    }
}
