<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/userInfo.css" />
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles22.css" />
</head>
<body>
	  	<%@ include file="/WEB-INF/views/main/header1.jsp"%>
  	<%@ include file="/WEB-INF/views/main/header2.jsp"%>
	<!-- **MAIN START** -->
	<main>
	
	<nav class="navbar navbar-expand-lg navbar-dark bg-success fixed-top" id="sideNav">
            <a class="navbar-brand js-scroll-trigger" href="#page-top">
                <span class="d-block d-lg-none">Clarence Taylor</span>
                <span class="d-none d-lg-block">
<!--                 	<img class="img-fluid img-profile rounded-circle mx-auto mb-2" src="assets/img/profile.jpg" alt="..."> -->
                	<c:if test="${lhj_MemberVO.m_img == null}">
						<img src="${pageContext.request.contextPath}/img/흰둥이.jpg" class="img-fluid img-profile rounded-circle mx-auto mb-2">				
					</c:if>
					<c:if test="${lhj_MemberVO.m_img != null}">
						<img src="${pageContext.request.contextPath}/upload/${lhj_MemberVO.m_img}" class="img-fluid img-profile rounded-circle mx-auto mb-2">				
					</c:if>
                
                </span>
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation"><span class="navbar-toggler-icon"></span></button>
            <div class="collapse navbar-collapse" id="navbarResponsive">
                <ul class="navbar-nav">
                    <li class="nav-item"><a class="nav-link js-scroll-trigger" href="${pageContext.request.contextPath}/member/mypage">mypage_home</a></li>
                    <li class="nav-item"><a class="nav-link js-scroll-trigger" href="${pageContext.request.contextPath}/member/mypage_update">userinfo_update</a></li>
                    <li class="nav-item"><a class="nav-link js-scroll-trigger" href="${pageContext.request.contextPath}/member/mypage_mycertification">certification</a></li>
                    <li class="nav-item"><a class="nav-link js-scroll-trigger" href="${pageContext.request.contextPath}/member/mypage_myPostList">my_history</a></li>
                    <li class="nav-item"><a class="nav-link js-scroll-trigger" href="${pageContext.request.contextPath}/member/mypage_myreginfo">my_reginfo</a></li>
                    <li class="nav-item"><a class="nav-link js-scroll-trigger" href="${pageContext.request.contextPath}/member/mypage_mybookmark">my_bookmark</a></li>
                    <li class="nav-item"><a class="nav-link js-scroll-trigger" href="${pageContext.request.contextPath}/member/mypage_changePW">changePW</a></li>
                    <li class="nav-item"><a class="nav-link js-scroll-trigger" href="${pageContext.request.contextPath}/member/mypage_deleteMyself">delete_myself</a></li>
                </ul>
            </div>
        </nav>
		<!-- main -->
		<div class="userinfo_wrap">
			<div class="title" style="margin-left: 3%">${lhj_MemberVO.m_name }님의 마이페이지</div>
<!-- 			<div class="info_nav_bar"> -->
<%-- 				<div class="info_nav_menu"><a href="${pageContext.request.contextPath}/member/mypage">마이페이지 홈</a></div> --%>
<%-- 				<div class="info_nav_menu"><a href="${pageContext.request.contextPath}/member/mypage_update">회원정보 수정</a></div> --%>
<%-- 				<div class="info_nav_menu"><a href="${pageContext.request.contextPath}/member/mypage_mycertification">인증</a></div> --%>
<%-- 				<div class="info_nav_menu"><a href="${pageContext.request.contextPath}/member/mypage_myPostList">내가 쓴 글</a></div> --%>
<%-- 				<div class="info_nav_menu"><a href="${pageContext.request.contextPath}/member/mypage_myreginfo">신청 내역</a></div> --%>
<%-- 				<div class="info_nav_menu"><a href="${pageContext.request.contextPath}/member/mypage_mybookmark">관심 내역</a></div> --%>
<%-- 				<div class="info_nav_menu"><a href="${pageContext.request.contextPath}/member/mypage_changePW">비밀번호 변경</a></div> --%>
<%-- 				<div class="info_nav_menu"><a href="${pageContext.request.contextPath}/member/mypage_deleteMyself">회원 탈퇴</a></div> --%>
<!-- 			</div> -->
			<div class="info_section">
				<form action="${pageContext.request.contextPath}/member/mypage_changePW" method="post">
				<div class="info_items">
					<div class="info_item">
						<div>비밀번호</div>
						<div>
							<input type="hidden" value="${lhj_MemberVO.m_id }" name="m_id">
							<input type="hidden" value="${lhj_MemberVO.m_name }">
							&nbsp&nbsp&nbsp&nbsp
							<input type="text" value="${lhj_MemberVO.m_pw }" name="m_pw">
						</div>
					</div>
				</div>	
				<div class="info_items">
					<input type="submit" value="확인" style="float: right;">
					<input type="reset" value="취소" style="float: right;">
				</div>
				</form>
			</div>
		</div>
	</main>
	<%@ include file="/WEB-INF/views//main/footer.jsp"%>
</body>
</html>