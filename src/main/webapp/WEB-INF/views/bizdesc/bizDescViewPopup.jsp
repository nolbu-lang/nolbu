<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String contextPath = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<title>사업설명서</title>
<link type="text/css" rel="stylesheet" href="<%=contextPath%>/theme/css/base/jquery-ui.css"/>
<link type="text/css" rel="stylesheet" href="<%=contextPath%>/css/my.jquery-ui.css"/>
<script src="<%=contextPath%>/js/jquery/jquery-1.9.1.js"></script>
<script src="<%=contextPath%>/js/jquery/jquery-impromptu.js"></script>
<script src="<%=contextPath%>/js/jquery/jquery-ui-1.10.1.custom.js"></script>
<script src="<%=contextPath%>/js/comm/cs.jquery.js"></script>
<script src="<%=contextPath%>/js/comm/bcjisComm.js"></script>
<script>var ctx = "<%=contextPath%>";</script>
<style type="text/css">
html, body { height:100%; margin:0; background:#fff; }
body { font-family:"맑은 고딕","Malgun Gothic",sans-serif; }
#dialogBizDescViewDiv {
  margin:0; padding:4px 8px 6px; width:100%; box-sizing:border-box;
}
#dialogBizDescViewDiv .bizdesc-head {
  background:#1a3a5c; color:#fff; padding:8px 10px; margin:-4px -8px 6px;
  font-size:16px; font-weight:bold;
}
#dialogBizDescViewDiv .bizdesc-view-meta { font-size:12px; margin:0 0 4px; line-height:1.4; color:#555; }
#dialogBizDescViewDiv .bizdesc-suggest-table { width:100%; border-collapse:collapse; font-size:13px; }
#dialogBizDescViewDiv .bizdesc-suggest-table th,
#dialogBizDescViewDiv .bizdesc-suggest-table td { border:1px solid #ddd; padding:5px 7px; vertical-align:top; }
#dialogBizDescViewDiv .bizdesc-suggest-table th { background:#f5f5f5; }
#dialogBizDescViewDiv .bizdesc-toolbar {
  position:sticky; top:0; z-index:5; background:#fff; padding:2px 0 4px; margin:0 0 4px;
  border-bottom:1px solid #ddd;
}
#dialogBizDescViewDiv .bizdesc-toolbar a.btnClass,
#dialogBizDescViewDiv .bizdesc-toolbar a.bizdesc-btn-sm {
  display:inline-block; font-size:12px; padding:3px 10px; margin-right:4px; line-height:1.4;
  height:auto; min-height:0; border:1px solid #888; background:#f5f5f5; color:#222; text-decoration:none;
}
#dialogBizDescViewDiv .bizdesc-body-wrap { overflow:auto; max-height:calc(100vh - 120px); }
#dialogBizDescViewDiv .bizdesc-body { font-size:13px; line-height:1.5; user-select:text; -webkit-user-select:text; padding-bottom:4px; }
#dialogBizDescViewDiv .bizdesc-body .bd-heading { font-weight:bold; margin:9px 0 3px; color:#1a3a5c; font-size:14px; }
#dialogBizDescViewDiv .bizdesc-body .bd-para { margin:2px 0; white-space:pre-wrap; }
#dialogBizDescViewDiv .bizdesc-body .bd-para.bd-line { white-space:pre-wrap; word-break:keep-all; line-height:1.5; margin:3px 0; }
/* 지정 표: 폭 약 90% 고정, 칸 폭 고정 */
#dialogBizDescViewDiv .bizdesc-body table.bd-table {
  width:90%; max-width:90%; table-layout:fixed; border-collapse:collapse;
  margin:5px auto; font-size:12px;
}
#dialogBizDescViewDiv .bizdesc-body table.bd-table th,
#dialogBizDescViewDiv .bizdesc-body table.bd-table td {
  border:1px solid #888; padding:3px 5px; vertical-align:middle; text-align:center;
  overflow:hidden; word-break:break-word;
}
#dialogBizDescViewDiv .bizdesc-body table.bd-table th,
#dialogBizDescViewDiv .bizdesc-body table.bd-table td.bd-label { background:#f0f4f8; font-weight:bold; white-space:normal; }
#dialogBizDescViewDiv .bizdesc-body table.bd-meta { width:90%; max-width:90%; margin:2px auto 5px; font-size:13px; table-layout:fixed; }
#dialogBizDescViewDiv .bizdesc-body table.bd-meta th { width:14%; }
#dialogBizDescViewDiv .bizdesc-body table.bd-meta td { width:36%; text-align:left; }
#dialogBizDescViewDiv .bizdesc-body table.bd-plan th,
#dialogBizDescViewDiv .bizdesc-body table.bd-plan td { font-size:11px; padding:2px 3px; white-space:pre-wrap; word-break:keep-all; }
#dialogBizDescViewDiv .bizdesc-body table.bd-procedure,
#dialogBizDescViewDiv .bizdesc-body table.bd-yearly { width:90%; max-width:90%; margin:5px auto; table-layout:fixed; }
#dialogBizDescViewDiv .bizdesc-body table.bd-procedure th,
#dialogBizDescViewDiv .bizdesc-body table.bd-procedure td,
#dialogBizDescViewDiv .bizdesc-body table.bd-yearly th,
#dialogBizDescViewDiv .bizdesc-body table.bd-yearly td { font-size:12px; padding:3px 4px; }
.bcjis-loading { display:none; }
/* 안내/확인 다이얼로그: 본문과 겹치지 않도록 모달·최상위 표시 */
.ui-widget-overlay {
  background:#000 !important;
  opacity:0.35 !important;
  filter:Alpha(Opacity=35);
  z-index:10000 !important;
}
.ui-dialog {
  z-index:10001 !important;
  box-shadow:0 6px 20px rgba(0,0,0,0.28);
}
.ui-dialog .ui-dialog-titlebar { padding:0.4em 1em; }
.ui-dialog .ui-dialog-content { background:#fff; color:#222; }
.ui-dialog .ui-dialog-buttonpane { background:#f7f7f7; }
#bcjisDialogMsgDiv { font-size:13px; line-height:1.5; white-space:pre-wrap; word-break:keep-all; }
</style>
</head>
<body>

<input type="hidden" id="bizDescViewPopupFlag" value="Y"/>

<div id="dialogBizDescViewDiv" title="사업설명서">
  <input type="hidden" id="dialogBizDescViewFisYear" value="<c:out value='${param.fisYear}'/>"/>
  <input type="hidden" id="dialogBizDescViewBgtDgr" value="<c:out value='${param.bgtDgr}'/>"/>
  <input type="hidden" id="dialogBizDescViewReportCd" value="<c:out value='${param.reportCd}'/>"/>
  <input type="hidden" id="dialogBizDescViewTeBgtCompoId" value="<c:out value='${param.teBgtCompoId}'/>"/>
  <input type="hidden" id="dialogBizDescViewDgrcompoId" value="<c:out value='${param.dgrcompoId}'/>"/>
  <input type="hidden" id="dialogBizDescViewReportBizNm" value="<c:out value='${param.reportBizNm}'/>"/>
  <input type="hidden" id="dialogBizDescViewDbizNm" value="<c:out value='${param.dbizNm}'/>"/>
  <input type="hidden" id="dialogBizDescViewOfficeCd" value="<c:out value='${param.officeCd}'/>"/>
  <input type="hidden" id="dialogBizDescViewTabId" value="<c:out value='${param.tabId}'/>"/>
  <input type="hidden" id="dialogBizDescViewGridId" value="<c:out value='${param.gridId}'/>"/>

  <div id="dialogBizDescViewHead" class="bizdesc-head">
    <span id="dialogBizDescViewHeadTitle">사업설명서</span>
  </div>
  <div class="bizdesc-view-meta" id="dialogBizDescViewMeta"></div>

  <div id="dialogBizDescViewSuggestPanel" class="bizdesc-panel" style="display:none;">
    <div style="margin-bottom:6px;font-size:14px;">유사도 60% 이상 사업설명서 후보 (사업명 중심, 공백·대소문자 무시)</div>
    <table class="bizdesc-suggest-table">
      <thead>
        <tr>
          <th style="width:10%;">유사도</th>
          <th style="width:18%;">부서</th>
          <th style="width:32%;">사업명</th>
          <th style="width:25%;">파일</th>
          <th style="width:15%;">매칭</th>
        </tr>
      </thead>
      <tbody id="dialogBizDescViewSuggestBody"></tbody>
    </table>
  </div>

  <div id="dialogBizDescViewSummaryPanel" class="bizdesc-panel" style="display:none;">
    <div class="bizdesc-toolbar">
      <a id="dialogBizDescViewCopyBtn" class="btnClass bizdesc-btn-sm" href="#">선택영역 복사</a>
      <a id="dialogBizDescViewClearBtn" class="btnClass bizdesc-btn-sm" href="#">매칭해제</a>
      <a id="dialogBizDescViewCloseBtn" class="btnClass bizdesc-btn-sm" href="#">창닫기</a>
    </div>
    <div class="bizdesc-body-wrap">
      <div class="bizdesc-body" id="dialogBizDescViewBody"></div>
    </div>
  </div>
</div>

<div id="bcjisLoading" class="bcjis-loading"></div>
<div id="bcjisDialogMsg" style="overflow:hidden;display:none;"><div id="bcjisDialogMsgDiv"></div></div>

<script>
window.BIZDESC_VIEW_MODE = "popup";
</script>
<script src="<%=contextPath%>/js/dialog/dialogBizDescView.js?v=20260830a"></script>
</body>
</html>
