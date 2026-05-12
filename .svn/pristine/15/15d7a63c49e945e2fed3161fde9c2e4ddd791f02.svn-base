<%@page import="com.cs.bcjis.comm.util.BcjisCommUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    String tabId = BcjisCommUtil.getStringParameter(request, "tabId", "");
%>
<script>
_pledgeMatchTabId = "<%=tabId%>";
</script>
<script src="${pageContext.request.contextPath}/js/pledge/pledgeMatch.js"></script>

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
              <th>회계년도</th>
              <td>
                <select id="condFisYear" name="condFisYear" title="회계년도" style="width:90%;">
                </select>
              </td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
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
    </div>
    <div id="mainCenter" class="pane ui-layout-center" style="border:0px;">
      <div class="btn">
        <div class="btnR">
          <a id="saveBtn" class="btnDisabledClass" href="#" enabledYn="N">저장</a>
        </div>
      </div>
      <div id="PLEDGE_MATCH_DIV" class="csGrid" style="border:0px;">
        <table id="PLEDGE_MATCH_GRD" style="border:0px;height:100%;"></table>
      </div>
    </div>
  </div>
  <!--list e-->
</div>
<!--ui-layout-center e-->