<%@ include file="/common/taglibs.jsp"%>
<%-- user for  surveysurveyitem.js --%>
<script type="text/javascript">
  		var removeInstructionUrl = "<c:url value='/authoring/removeInstruction.do'/>";
     		var addInstructionUrl = "<c:url value='/authoring/newInstruction.do'/>";
</script>
<script type="text/javascript" src="<lams:WebAppURL/>/includes/javascript/surveyitem.js"></script>

	<div class="card card-plain add-file">
		<div class="card-header card-title">
			<fmt:message key="label.authoring.basic.add.survey.question" />
		</div>
		<div class="card-body">

		

		<form:form action="saveOrUpdateItem.do" method="post" modelAttribute="surveyItemForm" id="surveyItemForm">
			<form:hidden path="sessionMapID" />
			<form:hidden path="contentFolderID" />
			<form:hidden path="itemIndex" />
			<form:hidden path="itemType" value="3" />
			<lams:errors/>

			<div class="form-group">
				<label for="question.description"><fmt:message key="label.question" /></label>
				<lams:CKEditor id="question.description" value="${surveyItemForm.question.description}"
					contentFolderID="${surveyItemForm.contentFolderID}">
				</lams:CKEditor>
			</div>

			<div class="checkbox">
				<label for="questionOptional">
				<form:checkbox path="question.optional" id="questionOptional"/>
				<fmt:message key="label.authoring.basic.question.optional" />
				</label>
			</div>

		</form:form>
        <a href="#" onclick="submitSurveyItem()" class="btn btn-default btn-sm pull-right">
			<fmt:message key="label.authoring.basic.add.question" /> </a>
        <a href="javascript:;" onclick="cancelSurveyItem()"
			class="btn btn-default btn-sm pull-right roffset5"> <fmt:message key="label.cancel" /> </a>
		
		
		
		</div>
	</div>
