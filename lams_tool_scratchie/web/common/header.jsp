<%@ include file="/common/taglibs.jsp"%>
<c:set var="lams"><lams:LAMSURL/></c:set>
<c:set var="tool"><lams:WebAppURL/></c:set>
<c:set var="ctxPath" value="${pageContext.request.contextPath}" scope="request"/>

<!-- ********************  CSS ********************** -->
<lams:css />
<link href="<lams:WebAppURL/>includes/css/scratchie.css" rel="stylesheet" type="text/css">

<!-- ********************  javascript ********************** -->
<script type="text/javascript" src="${lams}includes/javascript/common.js"></script>
<script type="text/javascript" src="${lams}includes/javascript/jquery.js"></script>
<script type="text/javascript" src="${lams}includes/javascript/tabcontroller.js"></script>    
<script type="text/javascript" src="${lams}includes/javascript/bootstrap-material-design.min.js"></script>
<script type="text/javascript" src="${lams}includes/javascript/jquery.timeago.js"></script>
	
