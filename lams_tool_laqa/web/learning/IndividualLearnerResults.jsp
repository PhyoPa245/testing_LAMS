<!DOCTYPE html>
<%@ include file="/common/taglibs.jsp"%>

<c:set var="sessionMap" value="${sessionScope[generalLearnerFlowDTO.httpSessionID]}" />

<lams:html>
<lams:head>
	<lams:css />
	<title><fmt:message key="activity.title" /></title>

	<script type="text/javascript" src="<lams:LAMSURL />includes/javascript/jquery.js"></script>

	<script language="JavaScript" type="text/JavaScript">
		function submitMethod(actionMethod) {
			$('.btn').prop('disabled', true);
			document.forms.qaLearningForm.action = actionMethod+".do";
			document.forms.qaLearningForm.submit();
		}
	</script>
</lams:head>

<body class="stripes">

	<!-- form needs to be outside page so that the form bean can be picked up by Page tag. -->
	<form:form action="/lams/tool/laqa11/learning/learning.do" method="POST" modelAttribute="qaLearningForm" target="_self">

		<lams:Page type="learner" title="${generalLearnerFlowDTO.activityTitle}">

		<!--  Announcements and advanced settings -->

		<c:if test="${generalLearnerFlowDTO.noReeditAllowed}">
			<lams:Alert type="danger" id="noRedosAllowed" close="false">
				<fmt:message key="label.noredo.enabled" />
			</lams:Alert>
		</c:if>

		<c:if test="${generalLearnerFlowDTO.lockWhenFinished && not generalLearnerFlowDTO.noReeditAllowed}">
			<lams:Alert type="danger" id="lockWhenFinished" close="false">
				<fmt:message key="label.responses.locked" />
			</lams:Alert>
		</c:if>

		<c:if test="${not empty sessionMap.submissionDeadline}">
			<lams:Alert type="danger" id="submissionDeadline" close="false">
				<fmt:message key="authoring.info.teacher.set.restriction">
					<fmt:param>
						<lams:Date value="${sessionMap.submissionDeadline}" />
					</fmt:param>
				</fmt:message>
			</lams:Alert>
		</c:if>
		<!-- End announcements -->

			<form:hidden path="toolSessionID" />
			<form:hidden path="userID" />
			<form:hidden path="httpSessionID" />
			<form:hidden path="totalQuestionCount" />

			<c:forEach var="questionEntry" items="${generalLearnerFlowDTO.mapQuestionContentLearner}">

				<div class="row no-gutter">
					<div class="col-xs-12">
						<div class="card card-plain">
							<div class="card-header card-title">
								<strong> <fmt:message key="label.question" /> <c:out value="${questionEntry.key}" escapeXml="false" />:
								</strong> <br>
								<c:out value="${questionEntry.value.question}" escapeXml="false" />
							</div>
							<div class="card-body">

								<c:forEach var="answerEntry" items="${generalLearnerFlowDTO.mapAnswersPresentable}">
									<c:if test="${answerEntry.key == questionEntry.key}">

										<h5>
											<fmt:message key="label.learning.yourAnswer" />
										</h5>

										<div class="card card-plain" id="answer${questionEntry.key}">
											<c:out value="${answerEntry.value}" escapeXml="false" />
										</div>


									</c:if>
								</c:forEach>

								<c:if test="${(questionEntry.value.feedback != '') && (questionEntry.value.feedback != null) }">
									<!-- Feedback -->

									<div class="row no-gutter">
										<div class="col-xs-12">
											<div class="card card-plain voffset5" id="feedback${questionEntry.key}">
												<div class="card-header card-header-sm card-title">
													<fmt:message key="label.feedback" />
												</div>
												<div class="card-body card-body-sm">
													<c:out value="${questionEntry.value.feedback}" escapeXml="false" />
												</div>
											</div>
										</div>
									</div>

									<!-- End feedback -->
								</c:if>



							</div>
							<!-- End card body -->
						</div>
					</div>
				</div>
				<div class="shading-bg">
					<p></p>



				</div>

			</c:forEach>

			<hr class="msg-hr" />

			<div class="voffset10">
				<c:if test="${!generalLearnerFlowDTO.noReeditAllowed}">
					<button type="button" name="redoQuestions" class="btn btn-default pull-left"
						onclick="submitMethod('redoQuestions');">
						<fmt:message key="label.redo" />
					</button>
				</c:if>

				<c:if test="${generalLearnerFlowDTO.showOtherAnswers}">
					<button name="viewAllResults" type="button" onclick="submitMethod('storeAllResults');"
						class="btn btn-default pull-right">
						<fmt:message key="label.allResponses" />
					</button>
				</c:if>

				<c:if test="${!generalLearnerFlowDTO.showOtherAnswers}">
					<c:if test="${generalLearnerFlowDTO.reflection != 'true'}">
						<div class="space-bottom-top align-right">
							<button type="button" id="finishButton" 
								onclick="javascript:submitMethod('storeAllResults');return false" class="btn btn-primary pull-right na">
								<c:choose>
									<c:when test="${sessionMap.isLastActivity}">
										<fmt:message key="button.submit" />
									</c:when>
									<c:otherwise>
										<fmt:message key="button.endLearning" />
									</c:otherwise>
								</c:choose>

							</button>
						</div>
					</c:if>

					<c:if test="${generalLearnerFlowDTO.reflection == 'true'}">
						<button name="forwardtoReflection" type="button" onclick="javascript:submitMethod('storeAllResults');"
							class="btn btn-primary pull-right">
							<fmt:message key="label.continue" />
						</button>
					</c:if>
				</c:if>

			</div>


		<div id="footer"></div>

		</lams:Page>

	</form:form>
	<!-- end form -->

</body>
</lams:html>
