<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<style type="text/css">

#dialogBizDescMatchDiv .bizdesc-file-row { margin-bottom:8px; }

#dialogBizDescMatchDiv .bizdesc-file-table { width:100%; border-collapse:collapse; font-size:12px; }

#dialogBizDescMatchDiv .bizdesc-file-table th,

#dialogBizDescMatchDiv .bizdesc-file-table td { border:1px solid #ddd; padding:6px 8px; text-align:left; }

#dialogBizDescMatchDiv .bizdesc-file-table th { background:#f5f5f5; }

#dialogBizDescMatchDiv .bizdesc-help { font-size:12px; color:#555; margin-bottom:8px; line-height:1.5; }

#dialogBizDescMatchDiv .bizdesc-office { font-weight:bold; color:#0645AD; margin-bottom:6px; font-size:13px; }

</style>

<div id="dialogBizDescMatchDiv" class="dialog" style="display:none;" title="사업설명서불러오기">

  <input type="hidden" id="dialogBizDescMatchFisYear" name="fisYear"/>

  <input type="hidden" id="dialogBizDescMatchBgtDgr" name="bgtDgr"/>

  <input type="hidden" id="dialogBizDescMatchReportCd" name="reportCd"/>

  <input type="hidden" id="dialogBizDescMatchOfficeCd" name="officeCd"/>

  <input type="hidden" id="dialogBizDescMatchOfficeNm" name="officeNm"/>

  <div id="dialogBizDescMatchOfficeRow" class="bizdesc-office" style="display:none;">

    적용 조건: <span id="dialogBizDescMatchOfficeLabel"></span>

  </div>

  <div class="bizdesc-help">

    조서 화면 조회조건의 <b>회계년도</b>, <b>예산차수</b>, <b>실국</b> 기준으로 사업설명서를 업로드·목록 표시합니다.<br/>

    ※ <b>실국=전체</b>에서 업로드한 파일은 모든 실국에서 공유됩니다. (목록에 [전체공유] 표시)<br/>

    ※ DRM 문서는 시 보안솔루션으로 해제 후 업로드 (.hwpx / .hwp)<br/>

    ※ 보안 해제 후 확장자만 .hwpx이고 내용이 구형 HWP여도 업로드됩니다.<br/>

    ※ JSON보내기·삭제는 목록에서 파일을 선택한 뒤 하단 버튼을 사용합니다.

  </div>

  <div class="bizdesc-file-row">

    <input type="file" id="dialogBizDescMatchFile" name="hwpxFile"

           accept=".hwpx,.hwp,.HWPX,.HWP,application/haansofthwp,application/x-hwp,application/octet-stream"/>

    <a id="dialogBizDescMatchUploadBtn" class="btnClass" href="#">업로드</a>

    <a id="dialogBizDescMatchRefreshBtn" class="btnClass" href="#">새로고침</a>

    <span id="dialogBizDescMatchStatus" style="margin-left:8px;font-size:12px;"></span>

  </div>

  <div style="max-height:360px;overflow:auto;">

    <table class="bizdesc-file-table">

      <thead>

        <tr>

          <th style="width:4%;text-align:center;"><input type="checkbox" id="dialogBizDescMatchCheckAll" title="전체 선택"/></th>

          <th style="width:38%;">파일명</th>

          <th style="width:10%;">조서</th>

          <th style="width:10%;">사업수</th>

          <th style="width:18%;">등록일</th>

          <th style="width:10%;">등록자</th>

        </tr>

      </thead>

      <tbody id="dialogBizDescMatchFileBody"></tbody>

    </table>

  </div>

  <div style="margin-top:10px;text-align:right;">

    <a id="dialogBizDescMatchExportBtn" class="btnClass" href="#" title="선택한 파일을 웹앱용 JSON으로 PC에 저장">JSON보내기</a>

    <a id="dialogBizDescMatchDeleteBtn" class="btnClass" href="#">삭제</a>

    <a id="dialogBizDescMatchCloseBtn" class="btnClass" href="#">닫기</a>

  </div>

</div>

<script src="${pageContext.request.contextPath}/js/dialog/dialogBizDescMatch.js?v=20260811b"></script>


