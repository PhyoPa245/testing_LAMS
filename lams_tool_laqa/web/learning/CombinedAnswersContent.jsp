<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt"%>
<%@ taglib uri="tags-lams" prefix="lams"%>

<c:forEach var="questionEntry"
	items="${generalLearnerFlowDTO.mapQuestionContentLearner}">
	<c:forEach var="answerEntry"
		items="${generalLearnerFlowDTO.mapAnswers}">

		<c:if test="${questionEntry.key == answerEntry.key}">
			<div class="row no-gutter">
				<div class="col-xs-12">
					<div class="card card-plain">
						<div class="card-header">
							<div class="card-title">
								<strong> <fmt:message key="label.question" /> <c:out
										value="${questionEntry.key}" /> 
								</strong>
							</div>
						</div>
						<div class="card-body">
							<div class="card card-plain"><c:out value="${questionEntry.value.question}" escapeXml="false" /></div>
						
							<div class="answer-req">
								<fmt:message key="label.learning.yourAnswer" /> 
								<c:if test="${questionEntry.value.required}">
									<small>
										<mark>
											<fmt:message key="label.required" />
										</mark>
									</small>
								</c:if> 
								
								<c:if test="${questionEntry.value.minWordsLimit != 0}">
									<br/>
									<fmt:message key="label.words.required" />: <span id="words-required-${questionEntry.key}"></span>
								</c:if>
							</div>

							<c:choose>
								<c:when test="${hasEditRight}">

									<div data-sequence-id="${questionEntry.key}"
										data-is-ckeditor="${generalLearnerFlowDTO.allowRichEditor}"
										data-min-words-limit="${questionEntry.value.minWordsLimit}"
										<c:if test="${questionEntry.value.minWordsLimit != 0}">class="min-words-limit-enabled"</c:if>>
										<c:choose>
											<c:when test="${generalLearnerFlowDTO.allowRichEditor}">
												<lams:CKEditor id="answer${questionEntry.key}"
													value="${answerEntry.value}"
													contentFolderID="${sessionMap.learnerContentFolder}"
													toolbarSet="DefaultLearner">
												</lams:CKEditor>
											</c:when>

											<c:otherwise>
												<lams:textarea name="answer${questionEntry.key}"
													id="answer${questionEntry.key}" rows="5"
													class="form-control"><c:out value='${answerEntry.value}' escapeXml='false' /></lams:textarea>
											</c:otherwise>
										</c:choose>
									</div>

								</c:when>

								<c:otherwise>
									<lams:textarea name="answer${questionEntry.key}" rows="5"
										class="form-control" disabled="disabled">
										<c:out value='${answerEntry.value}' escapeXml='false' />
									</lams:textarea>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
			</div>
		</c:if>

	</c:forEach>
</c:forEach>

<c:if test="${hasEditRight}">
	<div class="right-buttons">
		<button name="btnCombined"
			type="button"
			onclick="javascript:submitMethod('submitAnswersContent');"
			class="btn btn-default pull-right voffset5">
			<fmt:message key="button.submitAllContent" />
		</button>
	</div>
</c:if>


<script type="text/javascript">
	window.onload = function() {
		document.getElementById("answer1").focus();
	}
</script>

