<!DOCTYPE html>
<%@ include file="/common/taglibs.jsp"%>
<lams:html>
<lams:head>
	<lams:css />
	<title><fmt:message key="activity.title" /></title>
	<script type="text/javascript" src="<lams:LAMSURL/>includes/javascript/jquery.js"></script>
</lams:head>

<body class="stripes">

	<lams:Page type="learner" title="${mcGeneralLearnerFlowDTO.activityTitle}" formID="reflectionForm">

		<div class="card card-plain">
			<lams:out value="${mcGeneralLearnerFlowDTO.reflectionSubject}" escapeHtml="true" />
		</div>

		<div class="form-group">
			<form:form action="displayMc.do" modelAttribute="mcLearningForm" id="mcLearningForm" method="POST">
				<form:hidden path="toolContentID" />
				<form:hidden path="toolSessionID" />
				<form:hidden path="httpSessionID" />
				<form:hidden path="userID" />
				<form:hidden path="submitReflection" />
				<textarea rows="4" name="entryText" class="form-control" id="focusedInput">
					<c:if test="${not empty mcGeneralLearnerFlowDTO.notebookEntry}">
						<lams:out value="${mcGeneralLearnerFlowDTO.notebookEntry}" escapeHtml="true" />
					</c:if>
				</textarea>


				<a href="#" name="submitReflection" class="btn btn-primary pull-right voffset10 na"
					onclick="javascript:document.forms.mcLearningForm.submit();return false">
					<fmt:message key="button.endLearning" />
				</a>

			</form:form>
	</lams:Page>
	
	<script type="text/javascript">
		window.onload = function() {
			document.getElementById("focusedInput").focus();
		}
	</script>

</body>
</lams:html>
