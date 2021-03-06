<!DOCTYPE html>
<%@ include file="/common/taglibs.jsp"%>
<c:set var="sessionMapID" value="${param.sessionMapID}" />
<c:set var="sessionMap" value="${sessionScope[sessionMapID]}" />
	
<lams:html>
<lams:head>
	<title><fmt:message key="label.learning.title" />
	</title>
	<%@ include file="/common/header.jsp"%>
	
	<script type="text/javascript">
		function disableFinishButton() {
			document.getElementById("finishButton").disabled = true;
		}
	    $(document).ready(function(){
	    	window.onload = function() {
	    		document.getElementById("focused").focus();
	    	}
	    });
	</script>
</lams:head>
<body class="stripes">
	<lams:Page type="learner" title="${sessionMap.title}">
	
		<form:form action="submitReflection.do" method="post" onsubmit="disableFinishButton();" modelAttribute="reflectionForm" id="reflectionForm">
			<form:hidden path="userID" />
			<form:hidden path="sessionMapID" />

			<lams:errors/>

			<div class="card card-plain">
				<lams:out value="${sessionMap.reflectInstructions}" escapeHtml="true" />
			</div>

			<form:textarea rows="5" path="entryText" cssClass="form-control" id="focused" />

			<button type="submit" class="btn btn-primary voffset10 pull-right na" id="finishButton">
				<c:choose>
					<c:when test="${sessionMap.isLastActivity}">
						<fmt:message key="label.submit" />
					</c:when>
					<c:otherwise>
						<fmt:message key="label.finished" />
					</c:otherwise>
				</c:choose>
			</button>
				
		</form:form>

	</lams:Page>
</body>
</lams:html>
