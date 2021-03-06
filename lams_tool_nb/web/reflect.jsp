<!DOCTYPE html>
<%@ include file="/includes/taglibs.jsp"%>
<%@ page import="org.lamsfoundation.lams.tool.noticeboard.NoticeboardConstants"%>
<c:set var="lams">
	<lams:LAMSURL />
</c:set>

<lams:html>
<lams:head>
	<lams:css />
	<title><fmt:message key="activity.title"/></title>
	<script src="${lams}includes/javascript/jquery.js"></script>
	<script src="${lams}includes/javascript/bootstrap-material-design.min.js" type="text/javascript"></script>
</lams:head>

<body class="stripes">

<script type="text/javascript">
	function disableFinishButton() {
		document.getElementById("finishButton").disabled = true;
	}
	function submitForm(methodName) {
		var f = document.getElementById('nbLearnerForm');
		f.action = methodName + ".do";
		f.submit();
	}
</script>

<lams:Page type="learner" title="${title}" formID="nbLearnerForm">

	<form:form method="post" onsubmit="disableFinishButton();" modelAttribute="nbLearnerForm" id="nbLearnerForm">
		<div class="form-group">
			<div class="card card-plain">
				<lams:out value="${reflectInstructions}" escapeHtml="true" />
			</div>

			<textarea rows="4" name="reflectionText" value="${reflectEntry}" class="form-control"
				id="focusedInput"></textarea>

			<form:hidden path="toolSessionID" />
			<form:hidden path="mode" />

			<a href="#nogo" class="btn btn-primary pull-right voffset10" onclick="submitForm('finish')">
				<c:choose>
					<c:when test="${isLastActivity}">
						<fmt:message key="button.submit" />
					</c:when>
					<c:otherwise>
						<fmt:message key="button.finish" />
					</c:otherwise>
				</c:choose>
			</a>
		</div>

	</form:form>

	<!-- Comments: the extra div counteracts the float -->
	<c:if test="${allowComments}">
		<div class="row no-gutter"><div class="col-xs-12"></div></div>
		<lams:Comments toolSessionId="${nbLearnerForm.toolSessionID}"
			toolSignature="<%=NoticeboardConstants.TOOL_SIGNATURE%>" likeAndDislike="${likeAndDislike}" readOnly="true"
			pageSize="10" sortBy="1" />
	</c:if>
	<!-- End comments -->

</lams:Page>

<script type="text/javascript">
	window.onload = function() {
		document.getElementById("focusedInput").focus();
	}
</script>
	
</lams:html>
