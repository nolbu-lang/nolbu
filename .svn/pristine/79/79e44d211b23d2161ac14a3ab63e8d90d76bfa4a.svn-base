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
<script src="${pageContext.request.contextPath}/js/comm/mainNorth.js"></script>
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
	<p class="left"><img src="/images/design/img_header_logo.png" alt="예산담당관실 예산편성심사정보시스템"></p>
	<p class="right">
		<span id="mainNorthInfoTd">&nbsp;</span>
		<a id="mainNorthLogoutBtn" href="#">로그아웃</a>
	</p>
</div><!-- .always_top -->


</div>
