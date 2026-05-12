<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import = "com.cs.bcjis.comm.util.BcjisCommUtil" %>
<%
    String tabId = BcjisCommUtil.getStringParameter(request, "tabId", "");
%>
<script>
  _budgetPreCopyPageTabId = "<%=tabId%>";
</script>
<script src="${pageContext.request.contextPath}/js/budget/budgetPreCopyPage.js"></script>
<div class="contents" style="height:100%;">
  <!--list s-->
  <div id="mainBody" class="nondiv" style="height:95%;margin: 0 auto;width: 100%;min-width: 700px;_width: 700px;overflow: auto;">
    <input type="hidden" id="condAmtUnit" value="1000"/>
    <div id="mainCenter" class="pane ui-layout-center" style="border:0px;overflow:hidden;">
      <div id="subMainBody" class="nondiv" style="height:100%;margin: 0 auto;width: 100%;overflow: auto;">
        <div id="subMainCenter" class="pane ui-layout-center" style="border:0px;overflow:hidden;">
          <div class="ui-widget-header">
            대상
          </div>
          <!--condition s-->
          <div id="subMainCenterCond" class="condition" style="width:100%;">
            <table>
              <colgroup>
                <col width="100px"/>
                <col width="200px"/>
                <col width="100px"/>
                <col width="200px"/>
                <col width="100px"/>
                <col width="100px"/>
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
            <div class="gridHeader">
              <span id="BUDGET_PRE_COPYPAGE_TOT">통계목 총건수 : 0건</span>,&nbsp;통계목건수
              <select id="condRowNum" name="condRowNum" title="통계목건수">
              </select>
            </div>
            <div class="btnR">
              <a id="applyBtn" class="btnDisabledClass" href="#" enabledYn="N">적용</a>
              <a id="searchBtn" class="btnClass" href="#">조회</a>
              <a id="condInitBtn" class="btnClass" href="#">조건초기화</a>
            </div>
          </div>
          <div id="BUDGET_PRE_COPYPAGE_DIV" class="csGrid">
            <table id="BUDGET_PRE_COPYPAGE_GRD" style="border:0px;height:100%;"></table>
          </div>
          <div id="BUDGET_PRE_COPYPAGE_PGR" class="paging">
          </div>
        </div>
        <div id="subMainWest" class="pane ui-layout-west" style="border:0px;overflow:hidden;">
          <div class="ui-widget-header">
            자료
          </div>
          <!--condition s-->
          <div id="subMainWestCond" class="condition" style="width:97%;">
            <table>
              <colgroup>
                <col width="100px"/>
                <col width="200px"/>
                <col width="100px"/>
                <col width="200px"/>
                <col width="100px"/>
                <col width="100px"/>
              </colgroup>
              <tbody>
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
                <tr>
                  <th>통계목</th>
                  <td colspan="3">
                    <select id="condSrcTeMngMokCdFr" name="condSrcTeMngMokCdFr" title="통계목" style="width:40%;">
                    </select>&nbsp;~&nbsp;
                    <select id="condSrcTeMngMokCdTo" name="condSrcTeMngMokCdTo" title="통계목" style="width:40%;">
                    </select>
                  </td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                </tr>
                <tr>
                  <th>재원구분</th>
                  <td colspan="3">
                    <select id="condSrcFrscFgCdFr" name="condSrcFrscFgCdFr" title="재원구분" style="width:40%;">
                    </select>&nbsp;~&nbsp;
                    <select id="condSrcFrscFgCdTo" name="condSrcFrscFgCdTo" title="재원구분" style="width:40%;">
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
            <div class="gridHeader">
              <span id="BUDGET_PRE_COPYPAGE_SRC_TOT">통계목 총건수 : 0건</span>,&nbsp;통계목건수
              <select id="condSrcRowNum" name="condSrcRowNum" title="통계목건수">
              </select>
            </div>
            <div class="btnR">
              <a id="srcSearchBtn" class="btnClass" href="#">조회</a>
              <a id="condSrcInitBtn" class="btnClass" href="#">조건초기화</a>
            </div>
          </div>
          <div id="BUDGET_PRE_COPYPAGE_SRC_DIV" class="csGrid">
            <table id="BUDGET_PRE_COPYPAGE_SRC_GRD" ></table>
          </div>
          <div id="BUDGET_PRE_COPYPAGE_SRC_PGR" class="paging">
          </div>
        </div>
      </div>
    </div>
  </div>
  <!--list e-->
</div>
<!--ui-layout-center e-->