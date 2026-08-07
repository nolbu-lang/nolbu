<%@page import="com.cs.bcjis.comm.util.BcjisCommUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    String tabId = BcjisCommUtil.getStringParameter(request, "tabId", "");
    String viewMode = (String) request.getAttribute("viewMode");
    if (viewMode == null || "".equals(viewMode)) {
        viewMode = BcjisCommUtil.getStringParameter(request, "viewMode", "class");
    }
%>
<script>
_budgetSelectTabId = "<%=tabId%>";
_budgetSelectViewMode = "<%=viewMode%>";
</script>
<script src="${pageContext.request.contextPath}/js/budget/budgetSelectNew.js?v=20260804i"></script>

<div class="contents" style="height:100%;" data-view-mode="<%=viewMode%>" data-tab-id="<%=tabId%>">
  <!--list s-->
  <div id="mainBody" class="nondiv" style="height:95%;margin: 0 auto;width: 100%;min-width: 700px;_width: 700px;overflow: auto;">
    <div id="mainNorth" class="pane ui-layout-north" style="border:0px;overflow-x: hidden;">
      <!--condition s-->
      <div class="condition">
        <table>
          <colgroup>
            <col width="100px"/>
            <col width="200px"/>
            <col width="100px"/>
            <col width="100px"/>
            <col width="200px"/>
            <col width="100px"/>
            <col width="200px"/>
          </colgroup>
          <tbody>
            <!-- <tr>
              <th>조서구분</th>
              <td>
                <select id="condReportCd" name="condReportCd" title="조서구분" style="width:90%;">
                </select>
              </td>
              <td colspan="2">
                <select id="condReportDetlCd" name="condReportDetlCd" title="조서상세구분" style="width:93%;">
                </select>
              </td>
              <th>
                <span id="budgetSelectCondOrderYmdSeqSpan" style="display: none;">지시일자</span>
              </th>
              <td>
                <div id="budgetSelectCondOrderYmdSeqDiv" style="display: none;">
                  <select id="condOrderYmdSeq" name="condOrderYmdSeq" title="지시일자" style="width:110px;">
                  </select>
                  <a id="modifyOrderYmdSeqBtn" class="btnClass" href="#" enabledYn="Y">변경</a>
                </div>
              </td>
            </tr> -->
            <tr>
              <th>회계년도</th>
              <td>
                <select id="condFisYear" name="condFisYear" title="회계년도" style="width:90%;">
                </select>
              </td>
              <th>예산차수</th>
              <td colspan="2">
                <select id="condBgtDgr" name="condBgtDgr" title="예산차수" style="width:90%;">
                </select>
              </td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
            </tr>
            <tr>
              <th>회계구분</th>
              <td>
                <select id="condFisFgMstCd" name="condFisFgMstCd" title="회계마스터구분" style="width:90%;">
                </select>
              </td>
              <td colspan="3">
                <select id="condFisFgCd" name="condFisFgCd" title="회계구분" style="width:93%;">
                </select>
              </td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
            </tr>
            <tr>
              <th>실국</th>
              <td>
                <select id="condOfficeCd" name="condOfficeCd" title="실국" style="width:90%;">
                </select>
              </td>
              <th>부서</th>
              <td colspan="4">
                <input type="hidden" id="condDeptCdFr"/>
                <input type="hidden" id="condDeptRankFr"/>
                <input type="hidden" id="condDeptCdTo"/>
                <input type="hidden" id="condDeptRankTo"/>
                <input type="text" id="condDeptNmFr" class="readonly" style="width:35%;" readonly/>
                <a id="openDialogBgtDeptBtnFr" href="#"><img src="<c:url value='/images/btn/btn_search02.gif'/>" alt="조회"/></a>&nbsp;~&nbsp;
                <input type="text" id="condDeptNmTo" class="readonly" style="width:35%;" readonly/>
                <a id="openDialogBgtDeptBtnTo" href="#"><img src="<c:url value='/images/btn/btn_search02.gif'/>" alt="조회"/></a>
              </td>
            </tr>
            <tr>
              <th>통계목</th>
              <td colspan="4">
                <select id="condTeMngMokCdFr" name="condTeMngMokCdFr" title="통계목" style="width:40%;">
                </select>&nbsp;~&nbsp;
                <select id="condTeMngMokCdTo" name="condTeMngMokCdTo" title="통계목" style="width:40%;">
                </select>
              </td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
            </tr>
            <tr>
              <th>재원구분</th>
              <td colspan="2">
                <select id="condFrscFgCdFr" name="condFrscFgCdFr" title="재원구분" style="width:45%;">
                </select>&nbsp;~&nbsp;
                <select id="condFrscFgCdTo" name="condFrscFgCdTo" title="재원구분" style="width:45%;">
                </select>
              </td>
              <th>사업명</th>
              <td colspan="2">
                <input type="text" id="condBizNm" name="condBizNm" title="사업명"
                       placeholder="세부사업·개별사업 키워드"
                       style="width:92%; height:22px; line-height:22px; padding:0 4px; box-sizing:border-box; vertical-align:middle;" />
              </td>
            </tr>
            <tr>
              	<th>조정 증감액</th>
              	<td colspan="2">
              		최소&nbsp;<input type="text" id="condAmtFr"  onkeydown="javascript:commifyKey(this);" onkeyup="javascript:commifyKey(this);" style="width:13%;text-align:right;" />
              		&nbsp;~&nbsp;
              		최대&nbsp;<input type="text" id="condAmtTo"  onkeydown="javascript:commifyKey(this);" onkeyup="javascript:commifyKey(this);" style="width:13%;text-align:right;" />
              		(천원)&nbsp;&nbsp;&nbsp;<a id="condAmtInitBtn" class="btnClass" href="#">초기화</a>
              	</td>
              	<td colspan="4" class="viewModeAttrOnly" style="white-space:nowrap;vertical-align:middle;">
              		<span style="display:inline-block;margin-right:2px;">분류항목</span>
              		<select id="condAdvncProc" name="condAdvncProc" title="분류항목" style="width:120px;margin-right:10px;vertical-align:middle;"></select>
              		<span style="display:inline-block;margin-right:2px;">투자사업유형</span>
              		<select id="condIndiAttr" name="condIndiAttr" title="투자사업유형" style="width:120px;margin-right:8px;vertical-align:middle;"></select>
              		<a id="attrCodeBtn" class="btnClass btn_system" href="#" title="투자사업유형·분류항목 설정"
              		   style="background-color:#f26c4f;border:#f26c4f;vertical-align:middle;padding:2px 8px;">
              			&#9881; 설정
              		</a>
              	</td>
            </tr>
            <tr>
            	<td></td>
            	<td colspan="5">※ "이상"은 최소값만 입력하고 "미만"은 최대값만 입력 후 조회</td>
            	<td></td>
            </tr>
            <!-- <tr>
            	<th>분류</th>
            	<td>
            		<select id="condReportMstr" name="condReportMstr" title="대분류" style="width:90%;">
                	</select>
            	</td>
            	<td colspan="2">
            		<select id="condReportCd" name="condReportCd" title="중분류" style="width:90%;">
                	</select>
            	</td>
            	<td>
		            <select id="condReportDetlCd" name="condReportDetlCd" title="소분류" style="width:93%;">
                	</select>
            	</td>
            	<th class="condGovSubTarget">국고보조</th>
            	<td class="condGovSubTarget">
            		<select id="condGovSub" name="condGovSub" title="국고보조" style="width:93%;">
                	</select>
            	</td>
            </tr>
            <tr>
            	<th>보고항목</th>
            	<td>
            		<select id="condIndiAttr1" name="condIndiAttr1" title="보고항목1" style="width:90%;">
                	</select>
            	</td>
            	<td colspan="2">
            		<select id="condIndiAttr2" name="condIndiAttr2" title="보고항목2" style="width:90%;">
                	</select>
            	</td>
            	<td>
            		<select id="condIndiAttr3" name="condIndiAttr3" title="보고항목3" style="width:90%;">
                	</select>
            	</td>
            	<td>
            		<a id="indiAttrBtn" class="btnClass" href="#">관리</a>
            	</td>
            	<td>&nbsp;</td>
            </tr>
            <tr>
            	<th>사전절차</th>
            	<td>
            		<select id="condAdvncProc1" name="condAdvncProc1" title="보고항목1" style="width:90%;">
                	</select>
            	</td>
            	<td colspan="2">
            		<select id="condAdvncProc2" name="condAdvncProc2" title="보고항목2" style="width:90%;">
                	</select>
            	</td>
            	<td>
            		<select id="condAdvncProc3" name="condAdvncProc3" title="보고항목3" style="width:90%;">
                	</select>
            	</td>
            	<td>
            		<a id="advncProcBtn" class="btnClass" href="#">관리</a>
            	</td>
            	<td>&nbsp;</td>
            </tr> -->
          </tbody>
        </table>
      </div>
      <!--condition e-->
      <div class="btn">
        <div class="btnR">
          <a id="searchBtn" class="btnClass" href="#">조회</a>
          <a id="condInitBtn" class="btnClass" href="#">조건초기화</a>
          <a id="saveFileBtn" class="btnClass" href="#">파일저장</a>
        </div>
      </div>
      <div class="unitDiv" width="500px;">
        (단위:천원)&nbsp;**<span style="color:#0000FF">1.경상사업심사조서</span>,&nbsp;
        <span style="color:#FF0000">2.투자사업심사조서,&nbsp;
        <span style="color:#00B8B8">3.기본경비,&nbsp;</span>
        <span style="color:#FF9900">4.공통경비,&nbsp;</span>
        <span style="color:#FF99FF">5.그외 조서</span>
        <input type="hidden" id="condAmtUnit" value="1000"/>
      </div>
    </div>
    <div id="mainCenter" class="pane ui-layout-center" style="border:0px;">
      <div class="btn">
        <div class="btnL" style="width:100%;white-space:nowrap;">
          	<a id="selectAllBtn" class="btnDisabledClass viewModeClassOnly" href="#" enabledYn="N" style="background:#a6a6a5;border:#a6a6a5;">전체선택</a>
          	<a id="unSelectAllBtn" class="btnDisabledClass viewModeClassOnly" href="#" enabledYn="N" style="display:none;background:#a6a6a5;border:#a6a6a5;">선택해제</a>
      	  	<select id="reportMstrSel" name="reportMstrSel" title="대분류" style="width:160px;min-width:140px;">
          	</select>
      	  	<select id="reportCdSel" name="reportCdSel" title="중분류" style="width:160px;min-width:140px;">
          	</select>
        	<select id="reportDetlCdSel" name="reportDetlCdSel" title="소분류" style="width:180px;min-width:160px;">
          	</select>
      		<select id="govSubSel" name="govSubSel" title="국고보조" style="width:140px;min-width:120px;">
          	</select>
          	<span class="viewModeAttrOnly" id="attrToolbarPanel" style="display:inline-block;vertical-align:middle;margin-left:6px;">
          		<span style="margin-right:2px;">분류항목</span>
          		<select id="toolbarAdvncProc" name="toolbarAdvncProc" title="분류항목"
          		        style="width:140px;min-width:120px;margin-right:8px;vertical-align:middle;height:24px;"></select>
          		<span style="margin-right:2px;">투자사업유형</span>
          		<select id="toolbarIndiAttr" name="toolbarIndiAttr" title="투자사업유형" disabled="disabled"
          		        style="width:140px;min-width:120px;margin-right:6px;vertical-align:middle;height:24px;"></select>
          		<a id="attrApplyBtn" class="btnClass" href="#">적용</a>
          	</span>
          	<a id="searchDetlBtn" class="btnClass" href="#">조회</a>
          	<a id="attrSearchClearBtn" class="btnClass" href="#">조회지우기</a>
          	<a id="saveAllBtn" class="btnDisabledClass viewModeClassOnly" href="#" enabledYn="N">적용</a>
          	<a id="cancelClassBtn" class="btnDisabledClass viewModeClassOnly" href="#" enabledYn="N">취소</a>
          	<a id="undoCancelClassBtn" class="btnDisabledClass viewModeClassOnly" href="#" enabledYn="N" style="display:none;">취소 되돌리기</a>
        </div>
      </div>
      <div id="BUDGET_SELECT_NEW_DIV" class="csGrid" style="border:0px;">
        <table id="BUDGET_SELECT_NEW_GRD_<%=tabId%>" style="border:0px;height:100%;"></table>
      </div>
    </div>
  </div>
  <!--list e-->
</div>
<%@include file="/WEB-INF/views/dialog/dialogReport070OrderYmdModify.jsp"%>
<%@include file="/WEB-INF/views/dialog/dialogDgrcompoIndiAttr.jsp"%>
<%@include file="/WEB-INF/views/dialog/dialogDgrcompoAdvncProc.jsp"%>
<div id="dialogDgrcompoAttrCodeDiv" class="dialog" style="display:none;">
  <div id="attrCodeToolbar" class="btn" style="margin-bottom:6px;">
    <div class="btnR">
      <a id="attrCodeAddBtn" class="btnClass" href="#">추가</a>
      <a id="attrCodeSaveBtn" class="btnClass" href="#">저장</a>
      <a id="attrCodeDelBtn" class="btnClass" href="#">삭제</a>
    </div>
  </div>
  <div id="attrCodeTabs">
    <ul>
      <li><a href="#attrCodeTabIndi">투자사업유형</a></li>
      <li><a href="#attrCodeTabClass">분류항목</a></li>
    </ul>
    <div id="attrCodeTabIndi"></div>
    <div id="attrCodeTabClass"></div>
  </div>
</div>
<!--ui-layout-center e-->