<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page session="true" %>
<%@include file="/jsp/comm/commHeader.jsp"%>

<script type="text/javaScript" language="javascript" defer="defer">
$(document).ready(function (){
    $("#loginId").focus();
    
    $("#loginId").keypress(function(event){
        if(event.which == 13){
            $("#pswd").focus();
        }
    });
    
    $("#pswd").keypress(function(event){
        if(event.which == 13){
            doLogin();
        }
    });
    
    doLogin = function(){
        if(!isRequired($("#loginId").val())){
            $.csAlert({
                msg : "아이디를 입력하여 주십시오.",
                callBack : function() {
                    $("#loginId").focus();
                }
            });
            
            return false;
        }
        
        if(!isRequired($("#pswd").val())){
            $.csAlert({
                msg : "암호를 입력하여 주십시오.",
                callBack : function() {
                    $("#pswd").focus();
                }
            });
            
            return false;
        }
        
        var data = $.csAjaxCall({
            url : "/login/ajaxLogin.do",
            data : {loginId: $("#loginId").val(), pswd: $("#pswd").val()}
        });
        
        if(isEmpty(data) == true || data[BCJIS_RETURN_CODE] != "SUCC"){
            $.csAlert({
                msg : data.bcjisMessage
            });
            
            return;
        }
        
        bcjisMovePage('/main/main.do', {});
    };
});
</script>
<!--container s-->


<!-- 190814 기존로그인틀
<div class="nondiv" style="text-align:center;">
<form name="loginForm" action ="" method="post">
  <div class="contents_login">
    <div class="nondiv" style="width:565px;height:60px;margin-top:220px;text-align:center;vertical-align: middle;">
      <span>아이디 <input type="text" id="loginId" name="loginId" style="width:80px;" value="csit"> 패스워드 <input type="password" id="pswd" name="pswd" style="width:80px;" value="csit"></span>
      <span><a href="#" onclick="javascirpt:doLogin();" ><img src="<c:url value='/images/btn/btn_login.png'/>" alt="로그인"/></a></span>
    </div>
  </div>
</form>
</div>
-->


<!--container e-->
<div class="space10">&nbsp;</div>

<div class="login_wrap">
	<div class="login_box">
		<div class="visual">
			<img src="/images/design/img_login_visual.jpg" alt="부산시예산편성심사정보시스템 로그인화면">
		</div><!-- .visual -->

		<form name="loginForm" action ="" method="post">
			<div class="login_form">	
				<div class="left">
					<p class="p_id"><input type="text" id="loginId" name="loginId" placeholder="아이디를 입력하세요" title="아이디를 입력하세요"></p>
					<p class="p_pw"><input type="password" id="pswd" name="pswd" placeholder="비밀번호를 입력하세요" title="비밀번호를 입력하세요"></p>
				</div>
				<div class="right">
					<a href="#" onclick="javascirpt:doLogin();">로그인</a>
				</div>
			</div><!-- .login_form -->
		</form>
	</div><!-- .login_box -->
</div><!-- .login_wrap -->
<%@include file="/jsp/comm/commFooter.jsp"%>