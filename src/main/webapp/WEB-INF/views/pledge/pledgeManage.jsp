<%@page import="com.cs.bcjis.comm.util.BcjisCommUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    String tabId = BcjisCommUtil.getStringParameter(request, "tabId", "");
%>
<script>
_pledgeManageTabId = "<%=tabId%>";
</script>
<script src="${pageContext.request.contextPath}/js/pledge/pledgeManage.js"></script>

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
              <th>공약정보</th>
              <td colspan="3">
                <select id="condPledgeInfoId" name="condPledgeInfoId" title="공약사업정보" style="width:90%;">
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
        <div class="btnL">
          <a id="regiPledgeBtn" class="btnClass" href="#" enabledYn="Y">공약정보등록</a>
        </div>
        <div class="btnR">
          <a id="searchBtn" class="btnClass" href="#">조회</a>
          <a id="condInitBtn" class="btnClass" href="#">조건초기화</a>
        </div>
      </div>
    </div>
    <div id="mainCenter" class="pane ui-layout-center" style="border:0px;">
      <div class="btn">
        <div class="btnR">
          <a id="regiBizBtn" class="btnDisabledClass" href="#" enabledYn="N">공약사업등록</a>
          <a id="modifyBtn" class="btnDisabledClass" href="#" enabledYn="N">수정</a>
          <a id="deleteBtn" class="btnDisabledClass" href="#" enabledYn="N">삭제</a>
          <a id="sortBtn" class="btnDisabledClass" href="#" enabledYn="N">정렬순서변경</a>
        </div>
      </div>
      <div id="PLEDGE_MANAGE_DIV" class="csGrid" style="border:0px;">
        <table id="PLEDGE_MANAGE_GRD" style="border:0px;height:100%;"></table>
      </div>
    </div>
  </div>
</div>
<!--ui-layout-center e-->
<%@include file="/WEB-INF/views/dialog/dialogPledgeInfoRegi.jsp"%>
<%@include file="/WEB-INF/views/dialog/dialogPledgeInfoModify.jsp"%>
<%@include file="/WEB-INF/views/dialog/dialogPledgeBizRegi.jsp"%>
<%@include file="/WEB-INF/views/dialog/dialogPledgeBizModify.jsp"%>
<%@include file="/WEB-INF/views/dialog/dialogPledgeSort.jsp"%>