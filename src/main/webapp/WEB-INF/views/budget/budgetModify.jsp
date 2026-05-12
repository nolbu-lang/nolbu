<%@page import="com.cs.bcjis.comm.util.BcjisCommUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    String tabId = BcjisCommUtil.getStringParameter(request, "tabId", "");
%>
<script>
_budgetModifyTabId = "<%=tabId%>";
</script>
<script src="${pageContext.request.contextPath}/js/budget/budgetModify.js"></script>

<div class="contents" style="height:100%;">
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
              <td>&nbsp;</td>
              <td>&nbsp;</td>
            </tr>
            <tr>
              <th>재원구분</th>
              <td colspan="3">
                <select id="condFrscFgCdFr" name="condFrscFgCdFr" title="재원구분" style="width:40%;">
                </select>&nbsp;~&nbsp;
                <select id="condFrscFgCdTo" name="condFrscFgCdTo" title="재원구분" style="width:40%;">
                </select>
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
          <a id="searchBtn" class="btnClass" href="#">조회</a>
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
        <div class="btnR">
          <a id="regiBtn" class="btnDisabledClass" href="#" enabledYn="N">등록</a>
          <a id="modifyBtn" class="btnDisabledClass" href="#" enabledYn="N">수정</a>
          <a id="deleteBtn" class="btnDisabledClass" href="#" enabledYn="N">삭제</a>
          <a id="sortBtn" class="btnDisabledClass" href="#" enabledYn="N">정렬순서변경</a>
        </div>
      </div>
      <div id="BUDGET_MODIFY_DIV" class="csGrid" style="border:0px;">
        <table id="BUDGET_MODIFY_GRD" style="border:0px;height:100%;"></table>
      </div>
    </div>
  </div>
</div>
<%@include file="/WEB-INF/views/dialog/dialogDgrcompoRegi.jsp"%>
<%@include file="/WEB-INF/views/dialog/dialogDgrcompoTeMngRegi.jsp"%>
<%@include file="/WEB-INF/views/dialog/dialogDgrcompoSort.jsp"%>
<!--ui-layout-center e-->