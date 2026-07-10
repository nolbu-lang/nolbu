package com.cs.bcjis.batch;

import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;

import com.cs.bcjis.batch.util.BatchApi;
import com.cs.bcjis.budget.util.BudgetSelectNewSaveFile;
import com.cs.bcjis.report.util.ReportSaveUtil;

public class BatchScheduleController {

	@Resource(name = "batchApi")
    private BatchApi batchApi;
	
	
	//@Scheduled(cron="0 55 * * * *")
	//@Scheduled(cron = "0 0/1 * * * *") //5분마다 실행 
	@Scheduled(cron="0 0 03 * * ?")
	public void getLinkDataSchedule() throws Exception {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ 스케쥴 실행 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		
		Properties prop = new Properties();
        prop.load(this.getClass().getClassLoader().getResourceAsStream("csframework/bcjisProps/globals.properties"));
        String storePath = prop.getProperty("Globals.fileStorePath");
		//String storePathString = ReportSaveUtil.getStorePathString(config, "", "123");
          
		//batchApi.getData();
		
		//BatchApi.sendLog(storePath, "log");
	}
}
