<!DOCTYPE html>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.lamsfoundation.lams.util.Configuration" %>
<%@ page import="org.lamsfoundation.lams.util.ConfigurationKeys" %>
<c:set var="UPLOAD_FILE_MAX_SIZE"><%=Configuration.get(ConfigurationKeys.UPLOAD_FILE_MAX_SIZE)%></c:set>
<c:set var="EXE_FILE_TYPES"><%=Configuration.get(ConfigurationKeys.EXE_EXTENSIONS)%></c:set>
<c:set var="lams"><lams:LAMSURL /></c:set>
<c:set var="tool"><lams:WebAppURL /></c:set>
<c:set var="ctxPath" value="${pageContext.request.contextPath}" scope="request" />

<lams:html>
	<lams:head>
		<title><fmt:message key="activity.title" /></title>

		<!-- ********************  CSS ********************** -->

		<lams:css />
		<style media="screen,projection" type="text/css">
			.info {
				margin: 10px;
			}
		</style>
		
		<!-- ********************  javascript ********************** -->
		<script type="text/javascript" src="${lams}includes/javascript/common.js"></script>
		<script type="text/javascript" src="${lams}includes/javascript/tabcontroller.js"></script>
		<script type="text/javascript" src="${lams}includes/javascript/jquery.js"></script>
		<script type="text/javascript" src="${lams}includes/javascript/jquery-ui.js"></script>
		<script type="text/javascript" src="${lams}includes/javascript/jquery.jRating.js"></script>
		<script type="text/javascript" src="${lams}includes/javascript/jquery.treetable.js"></script>
		<script type="text/javascript" src="${lams}includes/javascript/bootstrap-material-design.min.js"></script>		
		<script type="text/javascript" src="${lams}includes/javascript/jquery.jscroll.js"></script>
		<script type="text/javascript" src="${lams}includes/javascript/upload.js"></script>
		
		<script type="text/javascript">
			var removeItemAttachmentUrl = "<lams:WebAppURL />learning/deleteAttachment.do";
			var warning = '<fmt:message key="warn.minimum.number.characters" />';
			var LABEL_MAX_FILE_SIZE = '<fmt:message key="errors.maxfilesize"><param>{0}</param></fmt:message>';
			var LABEL_NOT_ALLOWED_FORMAT = '<fmt:message key="error.attachment.executable"/>';	
			var EXE_FILE_TYPES = '${EXE_FILE_TYPES}';
			var UPLOAD_FILE_MAX_SIZE = '${UPLOAD_FILE_MAX_SIZE}';
		</script>
		<script type="text/javascript" src="${tool}includes/javascript/learner.js"></script>	
		
	</lams:head>
	
	<body class="stripes">
		<script>
			function doSubmit() {
				disableSubmitButton(); 
				if (validateForm()) {
					showBusy("itemAttachmentArea");
					return true;
				}
				enableSubmitButton();
				return false;
			}
		</script>
		
		<form:form action="createTopic.do" id="messageForm" modelAttribute="messageForm" onsubmit="return doSubmit();" 
				method="post" focus="message.subject" enctype="multipart/form-data">
				
			<form:hidden path="sessionMapID" />
			<c:set var="sessionMapID" value="${messageForm.sessionMapID}" />
			<c:set var="sessionMap" value="${sessionScope[sessionMapID]}" />
		
			<lams:Page type="learner" title="${sessionMap.title}" formID="messageForm">
		
				<div class="container-fluid">
				<div class="card card-plain">
					<div class="card-header card-title">
						<fmt:message key="title.message.edit" />
					</div>
					<div class="card-body">
						<lams:errors/>
		 				<%@ include file="/jsps/learning/message/topicform.jsp"%>
					</div>
				</div>
				</div>		
			</lams:Page>
		
		</form:form>
		

	</body>
</lams:html>

