<%@page import="com.cs.bcjis.comm.util.BcjisCommUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    String tabId = BcjisCommUtil.getStringParameter(request, "tabId", "");
%>
<script>
_manageCommcdTabId = "<%=tabId%>";
</script>
<script src="${pageContext.request.contextPath}/js/manage/manageCommcd.js"></script>

<div class="contents" style="height: 100%;">
  <!--list s-->
  <div id="mainBody" class="nondiv" style="height: 95%; margin: 0 auto; width: 100%; min-width: 700px; _width: 700px; overflow: auto;">
    <div id="mainNorth" class="pane ui-layout-north" style="border: 0px; overflow-x: hidden;">
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
              <th>코드명</th>
              <td><input id="condDetlCdNm" name="condDetlCdNm" style="width: 90%; ime-mode: active;" /></td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="btn">
        <div class="btnR">
          <a id="searchBtn" class="btnClass" href="#">조회</a>
        </div>
      </div>
    </div>
    <div id="mainCenter" class="pane ui-layout-center" style="border: 3px;">
      <div class="btn">
        <div class="btnR">
          <a id="modifyBtn" class="btnClass" href="#">수정</a>
        </div>
      </div>
      <div class="gridHeader" style="float:none;">
        <span id="MANAGE_COMMCD_LIST_TOT">총건수 : 0건</span>
      </div>
      <div id="MANAGE_COMMCD_LIST_DIV" class="csGrid" style="border: 0px;">
        <table id="MANAGE_COMMCD_LIST_GRD" style="border: 0px; height: 100%;"></table>
      </div>
      <div id="MANAGE_COMMCD_LIST_PGR" class="paging" style="width: 788px;"></div>
    </div>
  </div>
</div>
<%@include file="/WEB-INF/views/dialog/dialogManageCommcdModify.jsp"%>