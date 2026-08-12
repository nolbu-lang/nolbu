<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%--
  AI 예산편성 도우미 챗 위젯 (예산편성 화면 중앙 하단)
  - 내부 심사정보시스템(CUBRID) 데이터 조회 + 외부자료(관리자 URL) LLM 추론
  - 캐시 bust: v=20260729c (회계년도·내부검색 체크 UI)
--%>
<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/ai/aiChat.css?v=20260729c" />
<style type="text/css">
/* 구버전 CSS 캐시에도 툴바가 보이도록 핵심 스타일 인라인 보장 */
#aiChatToolbar{display:block;background:#eef2f7;border-bottom:1px solid #d5dbe3;padding:8px 12px 6px 12px;box-sizing:border-box;}
#aiChatDock.ai-collapsed #aiChatToolbar{display:none;}
.ai-year-row,.ai-search-row{display:block;margin-bottom:4px;line-height:26px;}
.ai-toolbar-label{display:inline-block;font-size:12px;font-weight:bold;color:#1f4e79;margin-right:6px;vertical-align:middle;}
.ai-year-input{width:56px;height:26px;border:1px solid #c9cdd4;border-radius:4px;padding:2px 6px;font-size:13px;text-align:center;font-family:inherit;background:#fff;vertical-align:middle;}
.ai-year-sep{color:#666;font-size:13px;margin:0 4px;vertical-align:middle;}
.ai-chk{display:inline-block;font-size:12px;color:#333;cursor:pointer;white-space:nowrap;margin-right:10px;vertical-align:middle;}
.ai-chk input{margin:0 3px 0 0;vertical-align:middle;}
</style>

<div id="aiChatDock" class="ai-collapsed">
  <div id="aiChatHeader">
    <span class="ai-title">AI 예산편성 도우미 <span class="ai-badge">내부 AI</span></span>
    <span class="ai-toggle" id="aiChatToggle">▲</span>
  </div>
  <%-- 툴바는 Body 밖(헤더 바로 아래) — 펼치면 회계년도·내부검색 체크가 바로 보임 --%>
  <div id="aiChatToolbar">
    <div class="ai-year-row">
      <span class="ai-toolbar-label">회계년도</span>
      <input type="text" id="aiFisYearFrom" class="ai-year-input" maxlength="4" inputmode="numeric" title="시작 회계년도" value="" />
      <span class="ai-year-sep">~</span>
      <input type="text" id="aiFisYearTo" class="ai-year-input" maxlength="4" inputmode="numeric" title="종료 회계년도" value="" />
    </div>
    <div class="ai-search-row">
      <span class="ai-toolbar-label">내부자료 검색</span>
      <label class="ai-chk"><input type="checkbox" id="aiSearchBizNm" /> 사업명</label>
      <label class="ai-chk"><input type="checkbox" id="aiSearchGubun" /> 구분</label>
      <label class="ai-chk"><input type="checkbox" id="aiSearchExam" /> 검토내용</label>
      <label class="ai-chk"><input type="checkbox" id="aiSearchSrchVal" /> 조건검색어</label>
    </div>
  </div>
  <div id="aiChatBody">
    <div id="aiChatMessages">
      <div class="ai-msg ai-bot ai-guide-msg">
        <div class="ai-bubble ai-guide"><div class="ai-guide-line">안녕하세요. AI 예산편성 도우미입니다.</div><div class="ai-guide-line"><span class="ai-guide-mark">▸</span><span class="ai-guide-label">내부검색</span> 위 체크박스를 선택한 뒤 검색어 입력</div><div class="ai-guide-line"><span class="ai-guide-mark">▸</span><span class="ai-guide-label">외부검색</span> 체크 없이 질문하면 등록된 외부자료를 바탕으로 답변</div><div class="ai-guide-line ai-guide-sub">키워드 조건: AND는 &amp; / OR는 , · 띄어쓰기·영문 대소문자 무시</div></div>
      </div>
    </div>
    <div id="aiChatInputArea">
      <textarea id="aiChatInput" placeholder="키워드 입력 (AND: &amp; / OR: , / Enter 전송)"></textarea>
      <button type="button" id="aiChatSendBtn">전송</button>
    </div>
  </div>
</div>

<script src="${pageContext.request.contextPath}/js/ai/aiChat.js?v=20260812b"></script>
<script type="text/javascript">
/* 구버전 JS 캐시 시에도 회계년도 기본값 보정 */
(function () {
  function fillYear() {
    var y = String(new Date().getFullYear());
    var $f = window.jQuery ? jQuery("#aiFisYearFrom") : null;
    var $t = window.jQuery ? jQuery("#aiFisYearTo") : null;
    if ($f && $f.length && !jQuery.trim($f.val())) { $f.val(y); }
    if ($t && $t.length && !jQuery.trim($t.val())) { $t.val(y); }
  }
  if (window.jQuery) { jQuery(fillYear); } else { document.addEventListener("DOMContentLoaded", fillYear); }
})();
</script>
