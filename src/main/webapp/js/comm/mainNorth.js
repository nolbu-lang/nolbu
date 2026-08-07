$(document).ready(function() {

    var tHtml = '<span><font style="font-weight: bold;">' + _mainNorthUserNm + '</font>님&nbsp;&nbsp;</span>';

    $("#mainNorthInfoTd").html(tHtml);

    $("#globalAbortBtn").click(function(e) {
        e.preventDefault();
        if (typeof $.csAbortAllAjax === "function") {
            $.csAbortAllAjax();
        }
    });
    
    $("#mainNorthLogoutBtn").click(function() {
        bcjisMovePage("/login/logoutAction.do");
    });
    
    $("#mainNorthLogo").click(function() {
        bcjisMovePage("/main/main.do");
    });
});