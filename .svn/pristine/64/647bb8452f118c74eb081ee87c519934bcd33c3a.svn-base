<%@page import="com.cs.bcjis.comm.util.BcjisCommUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
    String tabId = BcjisCommUtil.getStringParameter(request, "tabId", "");
%>
<script>
_manageTeMngVeriTabId = "<%=tabId%>";
</script>
<script src="${pageContext.request.contextPath}/js/manage/manageTeMngVeri.js"></script>

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
              <th>통계목명</th>
              <td><input id="condTeMngMokNm" name="condTeMngMokNm" title="통계목명" style="width: 90%; ime-mode: active;" /></td>
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
      <div class="gridHeader" style="float:none;">
        <span id="MANAGE_TEMNG_VERI_LIST_TOT">총건수 : 0건</span>
      </div>
      <div id="MANAGE_TEMNG_VERI_LIST_DIV" class="csGrid" style="border: 0px;">
        <table id="MANAGE_TEMNG_VERI_LIST_GRD" style="border: 0px; height: 100%;"></table>
      </div>
    </div>
  </div>
</div>
<%@include file="/WEB-INF/views/dialog/dialogTeMngVeri.jsp"%>