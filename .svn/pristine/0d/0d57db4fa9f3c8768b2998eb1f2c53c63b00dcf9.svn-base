<%@page import="com.cs.bcjis.comm.util.BcjisCommUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    String tabId = BcjisCommUtil.getStringParameter(request, "tabId", "");
%>
<script>
_pledgeReportTabId = "<%=tabId%>";
</script>
<script src="${pageContext.request.contextPath}/js/pledge/pledgeReport.js"></script>

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
              <th>공약사업구분</th>
              <td colspan="5">
                <select id="condPledgeInfoId" name="condPledgeBizId1" title="공약정보" style="width:100px;">
                </select>
                <select id="condPledgeBizId1" name="condPledgeBizId1" title="대분류" style="width:200px;">
                </select>
                <select id="condPledgeBizId2" name="condPledgeBizId2" title="중분류" style="width:200px;">
                </select>
                <select id="condPledgeBizId" name="condPledgeBizId" title="소분류"  style="width:200px;">
                </select>
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
              <th>조서/집계선택</th>
              <td>
                <select id="condSheetReportYn" name="condSheetReportYn" title="예산차수" style="width:90%;">
                  <option value="">전체</option>
                  <option value="O">O</option>
                  <option value="X">X</option>
                </select>
              </td>
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
            <tr>
              <th>통계목</th>
              <td colspan="3">
                <select id="condTeMngMokCdFr" name="condTeMngMokCdFr" title="통계목" style="width:40%;">
                </select>&nbsp;~&nbsp;
                <select id="condTeMngMokCdTo" name="condTeMngMokCdTo" title="통계목" style="width:40%;">
                </select>
              </td>
              <th>&nbsp;</th>
              <td>&nbsp;</td>
            </tr>
          </tbody>
        </table>
      </div>
      <!--condition e-->
      <div class="btn">
        <div class="btnR">
          <a id="searchBtn" class="btnClass" href="#">조회</a>
          <a id="savePledgeInfoBtn" class="btnClass" href="#">공약사업저장</a>
          <a id="saveSheetBtn2" class="btnClass" href="#">공약사업집계표</a>
          <a id="saveSheetBtn3" class="btnClass" href="#">공약사업현황</a>
          <a id="saveSheetBtn4" class="btnClass" href="#">공약사업현황(구군별)</a>
          <a id="condInitBtn" class="btnClass" href="#">조건초기화</a>
        </div>
      </div>
      <div class="unitDiv" style="top:-5px;">
        (단위:천원) <span style="color:#ff0000">※공약사업집계표는 집계표와 조서항목에 선택된 사업만 집계됩니다.</span>
        <input type="hidden" id="condAmtUnit" value="1000"/>
      </div>
    </div>
    <div id="mainCenter" class="pane ui-layout-center" style="border:0px;">
      <div class="btn">
        <div class="btnL" >
          조정액 <select id="yearFg" name="yearFg" title="년도구분"">
                 <option value="1">1차년도</option>
                 <option value="2">2차년도</option>
                 <option value="3">3차년도</option>
                 <option value="4">4차년도</option>
                 <option value="5">5차년도</option>
                 <option value="all">전체년도</option>
                </select>
          <a id="setBtn" class="btnDisabledClass" href="#" enabledYn="N">적용</a>&nbsp;&nbsp;
          <a id="resetBtn" class="btnDisabledClass" href="#" enabledYn="N">초기화</a>
        </div>
        <div class="btnR">
          <a id="saveBtn" class="btnDisabledClass" href="#" enabledYn="N">저장</a>
        </div>
      </div>
      <div id="PLEDGE_REPORT_DIV" class="csGrid" style="border:0px;">
        <table id="PLEDGE_REPORT_GRD" style="border:0px;height:100%;"></table>
      </div>
    </div>
  </div>
  <!--list e-->
</div>
<!--ui-layout-center e-->