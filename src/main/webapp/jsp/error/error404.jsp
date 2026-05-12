<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ko" lang="ko">
<head>
<title>404에러메세지</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bcjis.css" />
</head>
<body>

  <!--wrap s-->
  <div id="wrap">

    <!--container s-->
    <div id="container">

      <!--컨텐츠 s-->
      <div id="error_contents">
        <div class="mt10">
          <ul>
            <li class="error_bg">
              <ul class="error_msg">
                <li>HTTP 404 Error</li>
                <li>웹 페이지를 찾을 수 없습니다.</li>
              </ul>
            </li>
          </ul>
        </div>
      </div>
      <!--컨텐츠 e-->
    </div>
    <!--container e-->

  </div>
  <!--wrap e-->

</body>
</html>
