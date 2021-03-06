<%@ include file="/common/taglibs.jsp"%>

<script lang="javascript">
	$(document).ready(function(){
		$("#attemptsAllowedRadio").change(function() {
			$("#passingMark").val("0");
			$("#passingMark").prop("disabled", true);
			$("#attemptsAllowed").prop("disabled", false);
		});
		
		$("#passingMarkRadio").change(function() {
			$("#attemptsAllowed").val("0");
			$("#attemptsAllowed").prop("disabled", true);
			$("#passingMark").prop("disabled", false);
		});
		
		$("#display-summary").change(function(){
			$('#display-summary-area').toggle('slow');
			$('#allowQuestionFeedback').prop("checked", false);
			$('#allowDiscloseAnswers').prop("checked", false);
			$('#allowRightAnswersAfterQuestion').prop("checked", false).prop('disabled', false);
			$('#allowWrongAnswersAfterQuestion').prop("checked", false).prop('disabled', false);
			$('#allowHistoryResponsesAfterAttempt').prop("checked", false);
		});

		$('#allowDiscloseAnswers').change(function(){
			if ($(this).prop('checked')) {
				$('#allowRightAnswersAfterQuestion, #allowWrongAnswersAfterQuestion').prop('checked', false).prop('disabled', true);
			} else {
				$('#allowRightAnswersAfterQuestion, #allowWrongAnswersAfterQuestion').prop('disabled', false);
			}
		});
		
		$("#useSelectLeaderToolOuput").change(function() {
			if ($(this).prop('checked')) {
				$("#display-summary").prop("checked", true).prop("disabled", true);
				$('#display-summary-area').show('slow');
				$('#allowDiscloseAnswers').prop('disabled', false);
			} else {
				$("#display-summary").prop("disabled", false);
				$('#allowDiscloseAnswers').prop("checked", false).prop('disabled', true).change();
			}		
		});
		
		<c:if test="${assessmentForm.assessment.passingMark == 0}">$("#passingMark").prop("disabled", true);</c:if>
		<c:if test="${assessmentForm.assessment.passingMark > 0}">$("#attemptsAllowed").prop("disabled", true);</c:if>
		<c:if test="${assessmentForm.assessment.useSelectLeaderToolOuput}">$("#display-summary").prop("disabled", true);</c:if>
		<c:if test="${assessmentForm.assessment.allowDiscloseAnswers}">
			$('#allowRightAnswersAfterQuestion, #allowWrongAnswersAfterQuestion').prop('disabled', true)
		;</c:if>
	});
</script>

<!-- Advance Tab Content -->

<lams:SimplePanel titleKey="label.select.leader">
<div class="togglebutton">
	<label for="useSelectLeaderToolOuput">
		<form:checkbox path="assessment.useSelectLeaderToolOuput" value="1" id="useSelectLeaderToolOuput"/>
		 <span class="toggle"></span>
		<fmt:message key="label.use.select.leader.tool.output" />
	</label>
</div>
</lams:SimplePanel>

<lams:SimplePanel titleKey="label.question.options">

<lams:SimplePanel cardBodyClass="card-body-sm">

<div class="form-inline">
	<label for="assessment.questionsPerPage">
		<fmt:message key="label.authoring.advance.questions.per.page" />&nbsp;
	<form:select path="assessment.questionsPerPage" cssClass="form-control input-sm">
		<form:option value="0"><fmt:message key="label.authoring.advance.all.in.one.page" /></form:option>
		<form:option value="10">10</form:option>
		<form:option value="9">9</form:option>
		<form:option value="8">8</form:option>
		<form:option value="7">7</form:option>
		<form:option value="6">6</form:option>
		<form:option value="5">5</form:option>
		<form:option value="4">4</form:option>
		<form:option value="3">3</form:option>
		<form:option value="2">2</form:option>
		<form:option value="1">1</form:option>
	</form:select>
	</label>
</div>

<div class="togglebutton">
	<label for="shuffled">
		<form:checkbox path="assessment.shuffled" id="shuffled"/>
		 <span class="toggle"></span>
		<fmt:message key="label.authoring.advance.shuffle.questions" />
	</label>
</div>

<div class="togglebutton">
	<label for="questions-numbering">
		<form:checkbox path="assessment.numbered" id="questions-numbering"/>
		<span class="toggle"></span>
		<fmt:message key="label.authoring.advance.numbered.questions" />
	</label>
</div>

</lams:SimplePanel>

<lams:SimplePanel cardBodyClass="card-body-sm">

<div class="form-inline">
	<label for="timeLimit">
		<fmt:message key="label.authoring.advance.time.limit" />&nbsp;
		<form:input path="assessment.timeLimit" size="3" id="timeLimit" cssClass="form-control input-sm"/>
	</label>
</div>

<fmt:message key="label.authoring.advance.choose.restriction" />
	
<div class="loffset20">
	<div class="radio form-inline">
		<label for="attemptsAllowedRadio">
			<input type="radio" name="isAttemptsChosen" value="${true}" id="attemptsAllowedRadio"
					<c:if test="${assessmentForm.assessment.passingMark == 0}">checked="checked"</c:if> />
					
			<fmt:message key="label.authoring.advance.attempts.allowed" />&nbsp;
			
			<form:select path="assessment.attemptsAllowed" id="attemptsAllowed" cssClass="form-control input-sm">
				<form:option value="0"><fmt:message key="label.authoring.advance.unlimited" /></form:option>
				<form:option value="6">6</form:option>
				<form:option value="5">5</form:option>
				<form:option value="4">4</form:option>
				<form:option value="3">3</form:option>
				<form:option value="2">2</form:option>
				<form:option value="1">1</form:option>
			</form:select>
		</label>
	</div>

	<div class="radio form-inline">
		<label for="passingMarkRadio">
			<input type="radio" name="isAttemptsChosen" value="${false}" id="passingMarkRadio"	
					<c:if test="${assessmentForm.assessment.passingMark > 0}">checked="checked"</c:if> />
					
			<fmt:message key="label.authoring.advance.passing.mark" />&nbsp;
			
			<form:select path="assessment.passingMark" id="passingMark" cssClass="form-control input-sm"/>
		</label>
	</div>
	
</div>

</lams:SimplePanel>

<lams:SimplePanel cardBodyClass="card-body-sm">

<div class="togglebutton">
	<label for="display-summary">
		<form:checkbox path="assessment.displaySummary" id="display-summary"/>
		<span class="toggle"></span>
		<fmt:message key="label.authoring.advance.display.summary" />
	</label>
</div>

<div id="display-summary-area" class="loffset20" 
		<c:if test="${!assessmentForm.assessment.displaySummary}">style="display:none;"</c:if>>

	<div class="togglebutton">
		<label for="allowQuestionFeedback">
			<form:checkbox path="assessment.allowQuestionFeedback" id="allowQuestionFeedback"/>
			<span class="toggle"></span>
			<fmt:message key="label.authoring.advance.allow.students.question.feedback" />
		</label>
	</div>
	
	<div class="togglebutton">
		<label for="allowDiscloseAnswers">
			<form:checkbox path="assessment.allowDiscloseAnswers" id="allowDiscloseAnswers"/>
			<span class="toggle"></span>
			<fmt:message key="label.authoring.advance.disclose.answers" />
		</label>
	</div>

	<div class="togglebutton">
		<label for="allowRightAnswersAfterQuestion">
			<form:checkbox path="assessment.allowRightAnswersAfterQuestion" id="allowRightAnswersAfterQuestion"/>
			<span class="toggle"></span>
			<fmt:message key="label.authoring.advance.allow.students.right.answers" />
		</label>
	</div>

	<div class="togglebutton">
		<label for="allowWrongAnswersAfterQuestion">
			<form:checkbox path="assessment.allowWrongAnswersAfterQuestion" id="allowWrongAnswersAfterQuestion"/>
			<span class="toggle"></span>
			<fmt:message key="label.authoring.advance.allow.students.wrong.answers" />
		</label>
	</div>

	<div class="togglebutton">
		<label for="allowHistoryResponsesAfterAttempt">
			<form:checkbox path="assessment.allowHistoryResponses" id="allowHistoryResponsesAfterAttempt"/>
			<span class="toggle"></span>
			<fmt:message key="label.authoring.advance.allow.students.history.responses" />
		</label>
	</div>
</div>

<div class="togglebutton">
	<label for="allowOverallFeedbackAfterQuestion">
		<form:checkbox path="assessment.allowOverallFeedbackAfterQuestion" id="allowOverallFeedbackAfterQuestion"/>
		<span class="toggle"></span>
		<fmt:message key="label.authoring.advance.allow.students.overall.feedback" />
	</label>
</div>

<div class="togglebutton">
	<label for="allowGradesAfterAttempt">
		<form:checkbox path="assessment.allowGradesAfterAttempt" id="allowGradesAfterAttempt"/>
		<span class="toggle"></span>
		<fmt:message key="label.authoring.advance.allow.students.grades" />
	</label>
</div>

<div class="togglebutton">
	<label for="enable-confidence-levels">
		<form:checkbox path="assessment.enableConfidenceLevels" id="enable-confidence-levels"/>
		<span class="toggle"></span>
		<fmt:message key="label.enable.confidence.levels" />
	</label>
</div>
</lams:SimplePanel>

</lams:SimplePanel>

<lams:SimplePanel titleKey="label.notifications">
<div class="togglebutton">
	<label for="notifyTeachersOnAttemptCompletion">
		<form:checkbox path="assessment.notifyTeachersOnAttemptCompletion" id="notifyTeachersOnAttemptCompletion"/>
		<span class="toggle"></span>
		<fmt:message key="label.authoring.advanced.notify.on.attempt.completion" />
	</label>
</div>
</lams:SimplePanel>

<!-- Overall feedback -->
<lams:SimplePanel titleKey="label.authoring.advance.overall.feedback">

<input type="hidden" name="overallFeedbackList" id="overallFeedbackList" />
<iframe id="advancedInputArea" name="advancedInputArea" style="width:650px;height:100%;border:0px;display:block;" 
		frameborder="no" scrolling="no" src="<c:url value='/authoring/initOverallFeedback.do'/>?sessionMapID=${assessmentForm.sessionMapID}">
</iframe>
</lams:SimplePanel>

<lams:OutcomeAuthor toolContentId="${assessmentForm.assessment.contentId}" />

<lams:SimplePanel titleKey="label.activity.completion">

<div class="togglebutton">
	<label for="reflectOnActivity">
		<form:checkbox path="assessment.reflectOnActivity" id="reflectOnActivity"/>
		<span class="toggle"></span>
		<fmt:message key="advanced.reflectOnActivity" />
	</label>
</div>

<div class="form-group">
	<form:textarea path="assessment.reflectInstructions" rows="3" id="reflectInstructions" cssClass="form-control"/>
</div>
</lams:SimplePanel>

<script type="text/javascript">
	//automatically turn on refect option if there are text input in refect instruction area
	var ra = document.getElementById("reflectInstructions");
	var rao = document.getElementById("reflectOnActivity");
	function turnOnRefect(){
		if(isEmpty(ra.value)){
			//turn off	
			rao.checked = false;
		}else{
			//turn on
			rao.checked = true;		
		}
	}
	
	ra.onkeyup=turnOnRefect;
</script>

