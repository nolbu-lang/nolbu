<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import = "com.cs.bcjis.comm.BcjisUserDetailsHelper" %>
<%@ page import = "com.cs.bcjis.comm.web.BcjisUserVO" %>
<%
    BcjisUserVO mainNorthUser = (BcjisUserVO) BcjisUserDetailsHelper.getAuthenticatedUser();

    String mainNorthUserId = "";
    String mainNorthUserNm = "";
    String mainNorthPowGrCd = "";
    
    if(mainNorthUser != null){
        mainNorthUserId = mainNorthUser.getUserId();
        mainNorthUserNm = mainNorthUser.getUserNm();
        mainNorthPowGrCd = mainNorthUser.getPowGrCd();
    }

%>
<script type="text/javaScript" language="javascript" defer="defer">
var _mainNorthUserId = "<%=mainNorthUserId%>";
var _mainNorthUserNm = "<%=mainNorthUserNm%>";
var _mainNorthPowGrCd = "<%=mainNorthPowGrCd%>";
</script>
<script src="${pageContext.request.contextPath}/js/comm/mainNorth.js?v=20260804g"></script>
<div id="mainNorth">
<!--
  <div class="nondiv" style="height:100%;">
    <table>
      <colgroup>
        <col width="240px"/>
        <col width="*"/>
        <col width="100px"/>
        <col width="100px"/>
      </colgroup>
      <tbody>
        <tr>
          <td>
            <a id="mainNorthLogo" href="#"><img src="${pageContext.request.contextPath}/images/<spring:eval expression="@config['Globals.LocalCd']"/>/logo.png" width="239" height="44" /></a>
          </td>
          <td>&nbsp;</td>
          <td id="mainNorthInfoTd" style="text-align:right;">&nbsp;</td>
          <td style="text-align:left;">
            <a id="mainNorthLogoutBtn" href="#"><img src="${pageContext.request.contextPath}/images/btn/btn_logout.gif" alt="로그아웃"/>
          </a>
        </tr>
      </tbody>
    </table>  
  </div>
-->


<div class="always_top">
	<p class="left"><img src="/images/design/img_header_logo_240325.png" alt="예산담당관실 예산편성심사정보시스템"></p>
	<p class="right">
		<span id="mainNorthInfoTd">&nbsp;</span>
		<a href="javascript:void(0);" id="globalAbortBtn" class="btnClass" title="진행 중인 조회/저장 작업을 중단합니다">실행중단</a>
		<a href="javascript:void(0);" id="aiChatOpenBtn" title="AI 예산편성 도우미">AI 예산도우미</a>
		<a id="mainNorthLogoutBtn" href="#">로그아웃</a>
	</p>
</div><!-- .always_top -->


</div>
<%@include file="/WEB-INF/views/ai/aiChatLauncher.jsp"%>
