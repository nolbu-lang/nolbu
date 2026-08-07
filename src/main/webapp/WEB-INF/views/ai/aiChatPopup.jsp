<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    String contextPath = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<title>AI 예산편성 도우미</title>
<script src="<%=contextPath%>/js/jquery/jquery-1.9.1.js"></script>
<script src="<%=contextPath%>/js/jquery/ajaxfileupload.js"></script>
<script src="<%=contextPath%>/js/comm/cs.jquery.js"></script>
<script src="<%=contextPath%>/js/comm/bcjisComm.js"></script>
<script>var ctx = "<%=contextPath%>";</script>
<link type="text/css" rel="stylesheet" href="<%=contextPath%>/css/ai/aiChat.css?v=20260806d" />
<style type="text/css">
html, body { height:100%; margin:0; padding:0; background:#f4f6f9;
  font-family:"맑은 고딕","Malgun Gothic",dotum,sans-serif; font-size:13px; }
#aiChatPopupRoot { display:flex; flex-direction:column; height:100%; }
#aiChatPopupHd {
  flex:0 0 auto; background:#1f4e79; color:#fff; padding:12px 16px;
  font-size:16px; font-weight:bold;
}
#aiChatPopupHd .ai-badge {
  display:inline-block; background:#f26c4f; color:#fff; border-radius:8px;
  font-size:10px; padding:1px 6px; margin-left:6px; vertical-align:middle; font-weight:normal;
}
#aiChatToolbar {
  flex:0 0 auto; background:#eef2f7; border-bottom:1px solid #d5dbe3;
  padding:8px 12px 6px 12px; box-sizing:border-box;
}
.ai-year-row, .ai-search-row, .ai-general-row {
  display:block; margin-bottom:4px; line-height:28px;
}
.ai-general-row { white-space:nowrap; }
.ai-manual-right {
  display:inline-block; float:right; vertical-align:middle;
  max-width:52%; text-align:right; white-space:nowrap;
}
.ai-toolbar-label {
  display:inline-block; font-size:12px; font-weight:bold; color:#1f4e79;
  margin-right:6px; vertical-align:middle; min-width:78px;
}
.ai-year-input {
  width:56px; height:26px; border:1px solid #c9cdd4; border-radius:4px;
  padding:2px 6px; font-size:13px; text-align:center; vertical-align:middle; background:#fff;
}
.ai-year-sep { color:#666; margin:0 4px; vertical-align:middle; }
.ai-chk {
  display:inline-block; font-size:12px; color:#333; cursor:pointer;
  white-space:nowrap; margin-right:10px; vertical-align:middle;
}
.ai-chk input { margin:0 3px 0 0; vertical-align:middle; }
.ai-tool-btn {
  display:inline-block; vertical-align:middle; margin-left:4px;
  height:26px; padding:0 10px; border:1px solid #8a9bb0; border-radius:4px;
  background:#fff; color:#1f4e79; font-size:12px; font-weight:bold; cursor:pointer;
}
.ai-tool-btn:hover { background:#e7eef7; }
.ai-tool-btn:disabled { color:#999; border-color:#ccc; cursor:default; }
.ai-tool-btn.ai-close { background:#2b4f81; color:#fff; border-color:#1f3a5c; margin-left:6px; }
.ai-tool-btn.ai-close:hover { background:#1f3a5c; }
.ai-manual-box {
  display:inline-block; vertical-align:middle;
  padding:2px 6px; background:#fff; border:1px dashed #b0bccb; border-radius:4px;
}
.ai-manual-box input[type=file] { font-size:11px; max-width:220px; vertical-align:middle; }
.ai-manual-list { display:inline-block; font-size:11px; color:#555; margin-left:4px; vertical-align:middle; max-width:160px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.ai-manual-hint { font-size:11px; color:#888; margin-left:4px; vertical-align:middle; }
#aiChatBody {
  flex:1 1 auto; display:flex; flex-direction:column; min-height:0; background:#f4f6f9;
}
#aiChatMessages { flex:1 1 auto; overflow-y:auto; padding:8px 10px 10px 10px; }
#aiChatInputArea {
  flex:0 0 auto; display:flex; border-top:1px solid #dcdfe5; background:#fff;
  padding:8px; gap:8px;
}
#aiChatInput {
  flex:1 1 auto; resize:none; border:1px solid #c9cdd4; border-radius:6px;
  padding:8px; font-size:13px; height:56px; font-family:inherit;
}
#aiChatSendBtn {
  flex:0 0 auto; background:#2b4f81; color:#fff; border:none; border-radius:6px;
  padding:0 18px; font-size:14px; cursor:pointer;
}
#aiChatSendBtn:disabled { background:#9aa7b8; cursor:default; }
</style>
</head>
<body>
<div id="aiChatPopupRoot">
  <div id="aiChatPopupHd">
    AI 예산편성 도우미 <span class="ai-badge">내부 AI</span>
  </div>
  <div id="aiChatToolbar">
    <div class="ai-year-row">
      <span class="ai-toolbar-label">회계년도</span>
      <input type="text" id="aiFisYearFrom" class="ai-year-input" maxlength="4" inputmode="numeric" title="시작 회계년도" value="" />
      <span class="ai-year-sep">~</span>
      <input type="text" id="aiFisYearTo" class="ai-year-input" maxlength="4" inputmode="numeric" title="종료 회계년도" value="" />
      <button type="button" id="aiChatClearBtn" class="ai-tool-btn" title="채팅 입력·결과 모두 지우기">지우기</button>
      <button type="button" id="aiChatExportBtn" class="ai-tool-btn" title="상단 회계년도(1년 단위) 내부자료를 JSON으로 내보내기">JSON내보내기</button>
      <button type="button" id="aiChatCloseBtn" class="ai-tool-btn ai-close" title="창 닫기">닫기</button>
    </div>
    <div class="ai-search-row">
      <span class="ai-toolbar-label">내부자료 검색</span>
      <label class="ai-chk"><input type="checkbox" id="aiSearchBizNm" class="ai-internal-chk" /> 사업명</label>
      <label class="ai-chk"><input type="checkbox" id="aiSearchGubun" class="ai-internal-chk" /> 구분</label>
      <label class="ai-chk"><input type="checkbox" id="aiSearchExam" class="ai-internal-chk" /> 검토내용</label>
      <label class="ai-chk"><input type="checkbox" id="aiSearchSrchVal" class="ai-internal-chk" /> 조건검색어</label>
    </div>
    <div class="ai-general-row">
      <span class="ai-toolbar-label">일반자료 검색</span>
      <label class="ai-chk"><input type="checkbox" id="aiSearchLaw" class="ai-general-chk" name="aiGeneralSearch" /> 법령·조례</label>
      <label class="ai-chk"><input type="checkbox" id="aiSearchCity" class="ai-general-chk" name="aiGeneralSearch" /> 보도자료,고시공고</label>
      <label class="ai-chk"><input type="checkbox" id="aiSearchManual" class="ai-general-chk" name="aiGeneralSearch" /> 예산운용지침</label>
      <span class="ai-manual-right" id="aiManualUploadRow">
        <span class="ai-manual-box" id="aiManualManageBox">
          <input type="file" id="aiManualFile" name="file" accept=".pdf,application/pdf" multiple="multiple" title="PDF 여러 개 선택 가능" />
          <button type="button" id="aiManualUploadBtn" class="ai-tool-btn" title="PDF 업로드">올리기</button>
          <button type="button" id="aiManualRefreshBtn" class="ai-tool-btn" title="목록 새로고침">목록</button>
        </span>
        <span class="ai-manual-list" id="aiManualFileList" title=""></span>
        <span class="ai-manual-hint" id="aiManualHint"></span>
      </span>
    </div>
  </div>
  <div id="aiChatBody">
    <div id="aiChatMessages">
      <div class="ai-msg ai-bot ai-guide-msg">
        <div class="ai-bubble ai-guide">
          <div class="ai-guide-line">안녕하세요. AI 예산편성 도우미입니다.</div>
          <div class="ai-guide-line"><span class="ai-guide-mark">▸</span><span class="ai-guide-label">내부검색</span> 사업명·구분·검토내용·조건검색어 체크 후 검색 (복수 선택 시 OR)</div>
          <div class="ai-guide-line"><span class="ai-guide-mark">▸</span><span class="ai-guide-label">일반검색</span> 법령·조례 / 보도자료,고시공고 / 예산운용지침 중 <b>하나만</b> 선택</div>
          <div class="ai-guide-line"><span class="ai-guide-mark">▸</span>내부검색과 일반검색은 <b>동시에 선택불가</b></div>
          <div class="ai-guide-line ai-guide-sub">키워드 조건: AND는 &amp; / OR는 , &nbsp;·&nbsp; 띄어쓰기·영문 대소문자 무시</div>
          <div class="ai-guide-line ai-guide-sub">예) 투자심사&amp;40억원,예비타당성 / 세출예산 절차별 이행사항</div>
        </div>
      </div>
    </div>
    <div id="aiChatInputArea">
      <textarea id="aiChatInput" placeholder="키워드 입력 (AND: &amp; / OR: , / Enter 전송)"></textarea>
      <button type="button" id="aiChatSendBtn">전송</button>
    </div>
  </div>
</div>
<script src="<%=contextPath%>/js/ai/aiChat.js?v=20260807b"></script>
</body>
</html>
