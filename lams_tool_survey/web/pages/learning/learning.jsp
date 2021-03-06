<!DOCTYPE html>

<%@ include file="/common/taglibs.jsp"%>
<lams:html>
<lams:head>
	<title><fmt:message key="label.learning.title" /></title>
	<%@ include file="/common/header.jsp"%>

	<script type="text/javascript">
		function previousQuestion(sessionMapID) {
			$("#surveyForm").attr('action', '<c:url value="/learning/previousQuestion.do"/>');
			$("#surveyForm").submit();
		}

		function nextQuestion(sessionMapID) {
			$("#surveyForm").attr('action', '<c:url value="/learning/nextQuestion.do"/>');
			$("#surveyForm").submit();
		}

		function singleChoice(choiceName) {
			var rs = document.getElementsByName(choiceName);
			for (idx = 0; idx < rs.length; idx++)
				rs[idx].checked = false;
		}
		
		function disableButtons() {
			$('.btn').prop('disabled', true);
		}

	</script>
</lams:head>
<body class="stripes">
	<form:form action="doSurvey.do" method="post" modelAttribute="surveyForm" id="surveyForm" onsubmit="disableButtons();">
		<form:hidden path="questionSeqID" />
		<form:hidden path="sessionMapID" />
		<form:hidden path="position" />
		<form:hidden path="currentIdx" />
		<c:set var="sessionMapID" value="${surveyForm.sessionMapID}" />
		<c:set var="sessionMap" value="${sessionScope[sessionMapID]}" />
		<c:set var="position" value="${surveyForm.position}" />
		<c:set var="questionSeqID" value="${surveyForm.questionSeqID}" />
		<c:set var="currentIdx" value="${surveyForm.currentIdx}" />

		<lams:Page type="learner" title="${sessionMap.title}">

			<div class="card card-plain">
				<c:out value="${sessionMap.instructions}" escapeXml="false" />
			</div>
			<c:if test="${not empty sessionMap.submissionDeadline}">
				<lams:Alert id="submissionDeadline" type="info" close="true">
					<fmt:message>
						<fmt:param>
							<lams:Date value="${sessionMap.submissionDeadline}" />
						</fmt:param>
					</fmt:message>
				</lams:Alert>
			</c:if>
			<c:choose>
				<%-- Show on one page or when learner does not choose edit one question --%>
				<c:when test="${sessionMap.showOnOnePage && (empty questionSeqID or questionSeqID == 0)}">
					<c:forEach var="element" items="${sessionMap.questionList}">
						<c:set var="question" value="${element.value}" />
						<%@ include file="/pages/learning/question.jsp"%>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:set var="question" value="${sessionMap.questionList[questionSeqID]}" />
					<fmt:message key="label.question" />&nbsp;${currentIdx}&nbsp;<fmt:message key="label.of" />&nbsp;${sessionMap.totalQuestions}
						<%@ include file="/pages/learning/question.jsp"%>
				</c:otherwise>
			</c:choose>

			<%-- Display button according to different situation --%>
			<%--POSITION: 0=middle of survey, 1=first question, 2=last question, 3=Only one question available--%>
			<c:choose>
				<c:when test="${(sessionMap.showOnOnePage && (empty questionSeqID or questionSeqID == 0)) or position == 3}">
					<div class="right-buttons voffset10">
						<form:button onclick="submit" path="doSurvey" value="Done" disabled="${sessionMap.finishedLock}"
							class="btn btn-sm btn-primary pull-right">
							<fmt:message key="label.submit.survey" />
						</form:button>
						
					</div>
				</c:when>
				<c:otherwise>
					<c:set var="preChecked" value="true" />
					<c:if test="${position == 2 || position == 0}">
						<c:set var="preChecked" value="false" />
					</c:if>
					<c:set var="nextChecked" value="true" />
					<c:if test="${position == 1 || position == 0}">
						<c:set var="nextChecked" value="false" />
					</c:if>
					<div class="pull-left voffset10"> 
						<form:button path="PreviousButton" onclick="previousQuestion()"
							class="btn btn-sm btn-default" disabled="${preChecked}">
							<fmt:message key="label.previous" />
						</form:button>
					</div>
					<div class="pull-right voffset10"> 
					<c:if test="${position != 2}">
						<form:button path="NextButton" onclick="nextQuestion()" class="btn btn-sm btn-default" disabled="${nextChecked}">
							<fmt:message key="label.next" />
						</form:button>
					</c:if>
					 <c:if test="${position == 2}">
						<form:button onclick="submit" path="doSurvey" disabled="${sessionMap.finishedLock}" class="btn btn-sm btn-primary">
							<fmt:message key="label.submit.survey" />
						</form:button>
					</c:if>
					</div>
				</c:otherwise>
			</c:choose>
			<div id="footer"></div>
			<!--closes footer-->
		</lams:Page>
	</form:form>
</body>
</lams:html>
