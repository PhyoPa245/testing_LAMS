<!DOCTYPE html>
            
<%@ include file="/common/taglibs.jsp"%>

<lams:html>
		<c:set var="lams">
		<lams:LAMSURL />
	</c:set>
	<c:set var="tool">
		<lams:WebAppURL />
	</c:set>
	
	<lams:head>  
		<title>
			<fmt:message key="activity.title" />
		</title>
		<lams:css/>
	
		<script type="text/javascript" src="${lams}includes/javascript/common.js"></script>
		<script type="text/javascript" src="${tool}includes/javascript/wikiCommon.js"></script>
		<script type="text/javascript" src="${lams}includes/javascript/jquery.js"></script>
		<script type="text/javascript" src="${lams}includes/javascript/bootstrap-material-design.min.js"></script>
	</lams:head>

	<body class="stripes">
		
		<script type="text/javascript">
			function disableFinishButton() {
				document.getElementById("finishButton").disabled = true;
			}
			function submitForm(methodName) {
				var f = document.getElementById('learningForm');
				f.submit();
			}
		</script>
		
		<lams:Page type="learner" title="${wikiDTO.title}">
			<form:form action="submitReflection.do" method="post" onsubmit="disableFinishButton();" id="learningForm" modelAttribute="learningForm">
				<form:hidden path="toolSessionID" id="toolSessionID" />
				<form:hidden path="mode" value="${mode}" />
		
				<div class="card card-plain">
					<lams:out value="${wikiDTO.reflectInstructions}" escapeHtml="true" />
				</div>
				<div class="form-group">
					<textarea id="focused" rows="4" name="entryText" class="form-control">${learningForm.entryText}</textarea>
		
					<a href="#nogo" class="btn btn-primary voffset5 pull-right na" id="finishButton"
						onclick="submitForm('finish');return false">
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
		</lams:Page>
		
		<script type="text/javascript">
			window.onload = function() {
				document.getElementById("focused").focus();
			}
		</script>

		<div class="footer">
		</div>					
	</body>
</lams:html>
