<%@ taglib uri="tags-lams" prefix="lams"%>
<%@ taglib uri="tags-fmt" prefix="fmt"%>
<%@ taglib uri="tags-core" prefix="c"%>
<%-- Handles creation of multiple mcq questions based on QTI input. 
Expects an input of questionNumber (which will be the question number of the first question) & contentFolderID, 
which are passed on to the individual question jsps to generate the form fields.  --%>
<c:set var="currentNumber" scope="page">${questionNumber}</c:set>

<c:forEach items="${questions}" var="question">
<c:set scope="request" var="questionNumber">${currentNumber}</c:set>
<c:set scope="request" var="question">${question}</c:set>

<div id="divq${currentNumber}">
	<%@ include file="../tool/mcquestion.jsp" %>
</div>

<c:set var="currentNumber" scope="page">${currentNumber + 1}</c:set>

</c:forEach>
<script>
	$('#${numQuestionsFieldname}').val("${currentNumber-1}");
</script>
