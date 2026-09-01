<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import = "com.cs.bcjis.comm.util.BcjisCommUtil" %>
<%
    String tabId = BcjisCommUtil.getStringParameter(request, "tabId", "");
%>
<script>
  _rlkCheckTabId = "<%=tabId%>";
</script>
<script src="${pageContext.request.contextPath}/js/budget/rlkCheck.js"></script>
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
              <th>부서·사업명</th>
              <td colspan="3">
                <input type="text" id="condSrchNm" name="condSrchNm" title="부서·사업명 검색어" style="width:60%;" placeholder="검색어"/>
              </td>
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
    <div id="mainCenter" class="pane ui-layout-center" style="border:0px;overflow:hidden;">
      <div id="subMainBody" class="nondiv" style="height:100%;margin: 0 auto;width: 100%;overflow: auto;">
        <div id="subMainCenter" class="pane ui-layout-center" style="border:0px;overflow:hidden;">
          <div class="btn">
            <div class="btnL">
              <a id="selectAllBtn" class="btnDisabledClass" href="#" enabledYn="N">전체선택</a>
              <a id="unSelectAllBtn" class="btnDisabledClass" href="#" enabledYn="N" style="display:none;">선택해제</a>
            </div>
            <div class="btnR">
              <a id="applyDatasBtn" class="btnDisabledClass" href="#" enabledYn="N">적용</a>
            </div>
          </div>
          <div class="ui-widget-header">
            세부사업 목록 (기존 예산수정 화면과 동일한 트리 구조)
          </div>
          <div id="RLK_CHECK_DGRCOMPO_DIV" class="csGrid" >
            <table id="RLK_CHECK_DGRCOMPO_GRD" ></table>
          </div>
        </div>
        <div id="subMainWest" class="pane ui-layout-west" style="border:0px;overflow:hidden;">
          <div class="ui-widget-header">
            부서 목록
          </div>
          <div id="RLK_CHECK_DEPT_DIV" class="csGrid">
            <table id="RLK_CHECK_DEPT_GRD" ></table>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!--list e-->
</div>
