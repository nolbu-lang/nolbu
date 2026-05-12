<%@page import="com.cs.bcjis.comm.util.BcjisCommUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    String tabId = BcjisCommUtil.getStringParameter(request, "tabId", "");
%>
<script>
_reportWrite0F0PageTabId = "<%=tabId%>";
</script>
<script src="${pageContext.request.contextPath}/js/report/reportWrite0F0.js"></script>

<div class="contents" style="height:100%;">
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
            <col width="200px"/>
            <col width="100px"/>
            <col width="200px"/>
          </colgroup>
          <tbody>
            <tr>
              <th>보고항목</th>
              <td colspan="2">
                <select id="condIndiAttr" name="condIndiAttr" title="조서상세구분" style="width:93%;">
                </select>
              </td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
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
            </tr>
          </tbody>
        </table>
      </div>
      <!--condition e-->
      <div class="btn">
        <div class="btnR">
          <a id="searchBtn" class="btnClass" href="#">조회</a>
          <a id="saveFileBtn" class="btnClass" href="#">파일저장</a>
          <!-- <a id="saveFileBtn2" class="btnClass" href="#">파일저장(보고)</a> -->
          <a id="condInitBtn" class="btnClass" href="#">조건초기화</a>
        </div>
      </div>
      <div class="unitDiv">
        (단위:천원)
        <input type="hidden" id="condAmtUnit" value="1000"/>
      </div>
    </div>
    <div id="mainCenter" class="pane ui-layout-center" style="border:0px;">
      <div class="btn">
        <div class="gridHeader">
          <span id="REPORT_WRITE0F0PAGE_TOT">세부사업 총건수 : 0건</span>,&nbsp;세부사업건수
          <select id="condRowNum" name="condRowNum" title="세부사업건수">
          </select>
        </div>
        <div class="btnR">
          <a id="saveBtn" class="btnDisabledClass" href="#" enabledYn="N">저장</a>
        </div>
      </div>
      <div id="REPORT_WRITE0F0PAGE_DIV" class="csGrid" style="border:0px;">
        <table id="REPORT_WRITE0F0PAGE_GRD" style="border:0px;height:100%;"></table>
      </div>
      <div id="REPORT_WRITE0F0PAGE_PGR" class="paging">
      </div>
    </div>
  </div>
  <!--list e-->
</div>
<%@include file="/WEB-INF/views/dialog/dialogDgroffice020Sort.jsp"%>
<!--ui-layout-center e-->