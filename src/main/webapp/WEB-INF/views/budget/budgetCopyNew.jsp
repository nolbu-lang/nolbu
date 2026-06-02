<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import = "com.cs.bcjis.comm.util.BcjisCommUtil" %>
<%
    String tabId = BcjisCommUtil.getStringParameter(request, "tabId", "");
%>

<script>
  _budgetCopyTabId = "<%=tabId%>";
</script>
<script src="${pageContext.request.contextPath}/js/budget/budgetCopyNew.js"></script>
<div class="contents" style="height:100%;">
  <!--list s-->
  <div id="mainBody" class="nondiv" style="height:100%;margin: 0 auto;width: 100%;min-width: 700px;_width: 700px;overflow: hidden;">
    <div id="mainCenter" class="pane ui-layout-center" style="border:0px;overflow:hidden;">
      <div id="subMainBody" class="nondiv" style="height:100%;margin: 0 auto;width: 100%;overflow: auto;">
        <div id="subMainCenter" class="pane ui-layout-center" style="border:0px;overflow:hidden;">
          <div class="ui-widget-header">
            적용대상
          </div>
          <!--condition s-->
          <div id="subMainCenterCond" class="condition" style="width:100%;">
          <input type="hidden" id="condAmtUnit" value="1000"/>
            <table>
              <colgroup>
                <col width="100px"/>
                <col width="180px"/>
                <col width="100px"/>
                <col width="170px"/>
                <col width="100px"/>
                <col width="110px"/>
              </colgroup>
              <tbody>
              	<tr>
	            	<th>분류</th>
	            	<td>
	            		<select id="condReportMstr" name="condReportMstr" title="대분류" style="width:90%;">
	                	</select>
	            	</td>
	            	<td colspan="2">
	            		<select id="condReportCd" name="condReportCd" title="중분류" style="width:90%;">
	                	</select>
	            	</td>
	            	<td colspan="2">
			            <select id="condReportDetlCd" name="condReportDetlCd" title="소분류" style="width:93%;">
	                	</select>
	            	</td>
	            </tr>
                <tr>
                  <th>회계년도</th>
                  <td>
                    <select id="condFisYear" name="condFisYear" title="회계년도" style="width:90%;">
                    </select>
                  </td>
                  <th>예산차수</th>
                  <td>
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
                  <td colspan="2">
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
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                <tr>
                  <th>부서</th>
                  <td colspan="3">
                    <input type="hidden" id="condDeptCdFr"/>
                    <input type="hidden" id="condDeptRankFr"/>
                    <input type="hidden" id="condDeptCdTo"/>
                    <input type="hidden" id="condDeptRankTo"/>
                    <input type="text" id="condDeptNmFr" class="readonly" style="width:35%;" readonly/>
                    <a id="openDialogBgtDeptBtnFr" href="#"><img src="<c:url value='/images/btn/btn_search02.gif'/>" alt="조회"/></a>&nbsp;~&nbsp;
                    <input type="text" id="condDeptNmTo" class="readonly" style="width:35%;" readonly/>
                    <a id="openDialogBgtDeptBtnTo" href="#"><img src="<c:url value='/images/btn/btn_search02.gif'/>" alt="조회"/></a>
                  </td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                </tr>
              </tbody>
            </table>
          </div>
          <!--condition e-->
          <div class="btn">
            <div class="btnR">
              <a id="mapAddBtn" class="btnDisabledClass" href="#" enabledYn="N">매핑추가</a>
              <a id="searchBtn" class="btnClass" href="#">조회</a>
              <a id="condInitBtn" class="btnClass" href="#">조건초기화</a>
            </div>
          </div>
          <div id="BUDGET_COPY_DIV" class="csGrid">
            <table id="BUDGET_COPY_GRD"></table>
          </div>
        </div>
        <div id="subMainWest" class="pane ui-layout-west" style="border:0px;overflow:hidden;">
          <div class="ui-widget-header">
            전년도 예산
          </div>
          <!--condition s-->
          <div id="subMainWestCond" class="condition" style="width:97%;">
            <table>
              <colgroup>
                <col width="100px"/>
                <col width="180px"/>
                <col width="100px"/>
                <col width="170px"/>
                <col width="100px"/>
                <col width="100px"/>
              </colgroup>
              <tbody>
              	<tr>
	            	<th>분류</th>
	            	<td>
	            		<select id="condSrcReportMstr" name="condSrcReportMstr" title="대분류" style="width:90%;">
	                	</select>
	            	</td>
	            	<td colspan="2">
	            		<select id="condSrcReportCd" name="condSrcReportCd" title="중분류" style="width:90%;">
	                	</select>
	            	</td>
	            	<td colspan="2">
			            <select id="condSrcReportDetlCd" name="condSrcReportDetlCd" title="소분류" style="width:93%;">
	                	</select>
	            	</td>
	            </tr>
                <tr>
                  <th>회계년도</th>
                  <td>
                    <select id="condSrcFisYear" name="condSrcFisYear" title="회계년도" style="width:90%;">
                    </select>
                  </td>
                  <th>예산차수</th>
                  <td>
                    <select id="condSrcBgtDgr" name="condSrcBgtDgr" title="예산차수" style="width:90%;">
                    </select>
                  </td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                </tr>
                <tr>
                  <th>회계구분</th>
                  <td>
                    <select id="condSrcFisFgMstCd" name="condSrcFisFgMstCd" title="회계마스터구분" style="width:90%;">
                    </select>
                  </td>
                  <td colspan="2">
                    <select id="condSrcFisFgCd" name="condSrcFisFgCd" title="회계구분" style="width:93%;">
                    </select>
                  </td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                </tr>
                <tr>
                  <th>실국</th>
                  <td>
                    <select id="condSrcOfficeCd" name="condSrcOfficeCd" title="실국" style="width:90%;">
                    </select>
                  </td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                <tr>
                  <th>부서</th>
                  <td colspan="3">
                    <input type="hidden" id="condSrcDeptCdFr"/>
                    <input type="hidden" id="condSrcDeptRankFr"/>
                    <input type="hidden" id="condSrcDeptCdTo"/>
                    <input type="hidden" id="condSrcDeptRankTo"/>
                    <input type="text" id="condSrcDeptNmFr" class="readonly" style="width:35%;" readonly/>
                    <a id="openDialogBgtDeptSrcBtnFr" href="#"><img src="<c:url value='/images/btn/btn_search02.gif'/>" alt="조회"/></a>&nbsp;~&nbsp;
                    <input type="text" id="condSrcDeptNmTo" class="readonly" style="width:35%;" readonly/>
                    <a id="openDialogBgtDeptSrcBtnTo" href="#"><img src="<c:url value='/images/btn/btn_search02.gif'/>" alt="조회"/></a>
                  </td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                </tr>
              </tbody>
            </table>
          </div>
          <!--condition e-->
          <div class="btn">
            <div class="btnR">
              <a id="srcSearchBtn" class="btnClass" href="#">조회</a>
              <a id="condSrcInitBtn" class="btnClass" href="#">조건초기화</a>
            </div>
          </div>
          <div id="BUDGET_COPY_SRC_DIV" class="csGrid">
            <table id="BUDGET_COPY_SRC_GRD" ></table>
          </div>
        </div>
      </div>
    </div>
    <div id="mainSouth" class="pane ui-layout-south" style="border:0px;overflow:hidden;">
      <div class="ui-widget-header">
        매핑 목록 (전년도 → 올해)
      </div>
      <div class="btn">
        <div class="btnR">
          <a id="batchApplyBtn" class="btnDisabledClass" href="#" enabledYn="N">일괄적용</a>
          <a id="mapDelBtn" class="btnClass" href="#">매핑삭제</a>
          <a id="mapClearBtn" class="btnClass" href="#">전체삭제</a>
        </div>
      </div>
      <div id="BUDGET_MAP_DIV" class="csGrid">
        <table id="BUDGET_MAP_GRD"></table>
      </div>
    </div>
  </div>
  <!--list e-->
</div>
<!--ui-layout-center e-->