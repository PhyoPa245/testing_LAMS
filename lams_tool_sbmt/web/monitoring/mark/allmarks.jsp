<!DOCTYPE html>
        
<%@include file="/common/taglibs.jsp"%>
<c:set var="tool">
	<lams:WebAppURL />
</c:set>
<c:set var="lams">
	<lams:LAMSURL />
</c:set>

<lams:html>
<lams:head>
	<title><fmt:message key="activity.title" /></title>
	<lams:css/>
	
	<script type="text/javascript" src="${lams}includes/javascript/common.js"></script>
	<script type="text/javascript" src="${lams}includes/javascript/jquery.js"></script>
	<script type="text/javascript" src="${lams}includes/javascript/bootstrap-material-design.min.js"></script>
	<script type="text/javascript" src="${lams}includes/javascript/readmore.min.js"></script>
	<script type="text/javascript" src="<c:url value="/"/>/includes/javascript/marks.js"></script>
	<script type="text/javascript">
		//constants for marks.js
		var LABEL_DELETE = '<fmt:message key="message.monitor.confirm.original.learner.file.delete"/>';
		var LABEL_RESTORE = '<fmt:message key="message.monitor.confirm.original.learner.file.restore"/>';
		var MONITOR_URL = "<c:url value="/monitoring"/>";

		function updateMark(detailId,reportId,sessionId,userId){
			location.href="<lams:WebAppURL/>mark/newMark.do?updateMode=listAllMarks&userID="+userId+"&toolSessionID="+sessionId+"&detailID="+detailId+"&reportID="+reportId;
		}
	</script>
</lams:head>

<body class="stripes">
<c:set var="title"><fmt:message key="label.monitoring.heading.marking" /></c:set>
<lams:Page title="${title}" type="monitor">

	<c:if test="${isMarksReleased}">
		<div class="alert alert-success">
            <i class="fa fa-check-circle"></i>
			<fmt:message key="label.marks.released" />
		</div>
	</c:if>

	<c:forEach var="user" items="${reports}">
		<c:forEach items="${user.value}" var="fileInfo" varStatus="status">
			<%@include file="fileinfo.jsp"%>
			<hr width="50%"/>
		</c:forEach>
		<hr width="100%" style="border-top: 2px solid #ddd;"/>
	</c:forEach>

	<a href="javascript:window.close();" name="submit" class="btn btn-primary pull-right">
		<fmt:message key="label.monitoring.done.button" />
	</a>
	
<div id="footer"></div>
	
</lams:Page>


</body>
</lams:html>
