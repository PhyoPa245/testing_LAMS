<c:set var="lams"><lams:LAMSURL/></c:set>
<c:set var="tool"><lams:WebAppURL/></c:set>
<c:set var="ctxPath" value="${pageContext.request.contextPath}" scope="request"/>

 	<!-- ********************  CSS ********************** -->
	<lams:css/>

 	<!-- ********************  javascript ********************** -->
	<script type="text/javascript" src="${lams}includes/javascript/common.js"></script>
	<script type="text/javascript" src="${lams}includes/javascript/jquery.js"></script>
	<script type="text/javascript" src="<c:url value='/includes/javascript/rsrccommon.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/includes/javascript/rsrcresourceitem.js'/>"></script>
	<script type="text/javascript" src="${lams}includes/javascript/popper.min.js"></script>
	<script type="text/javascript" src="${lams}includes/javascript/bootstrap-material-design.min.js"></script>
	<script type="text/javascript" src="${lams}includes/javascript/bootstrap.tabcontroller.js"></script>
	