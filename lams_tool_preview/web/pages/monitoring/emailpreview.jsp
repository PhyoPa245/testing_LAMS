<!DOCTYPE html>
<%@ include file="/common/taglibs.jsp"%>
<c:set var="sessionMap" value="${sessionScope[sessionMapID]}"/>
<c:set var="emailPreviewDTO" value="${sessionMap.emailPreviewDTO}"/>
<lams:html>
<lams:head>
</lams:head>
<body>
<div class="card card-plain">
<div class="card-header">
<div class="card-title"><fmt:message key="label.email.preview"/></div>
</div>
<div class="card-body">
<div id="emailHTML">${emailPreviewDTO.emailHTML}</div>
<div class="pull-right">
	<button onclick="javascript:closeResultsForLearner();return false;" class="btn btn-default btn-sm btn-disable-on-submit"><fmt:message key="label.hide"/></button>
	<button id="sendEmailNowButton" onclick="javascript:sendResultsForLearner(${emailPreviewDTO.toolSessionId}, ${emailPreviewDTO.learnerUserId}, ${emailPreviewDTO.dateTimeStamp});return false;" 
		class="btn btn-default btn-sm loffset5 btn-disable-on-submit"><i class="fa fa-sm fa-envelope-o">&nbsp;</i><fmt:message key="button.email.results"/></button>
</div>
</div>
</div>
</body>
</lams:html>