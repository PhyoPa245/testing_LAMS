<!DOCTYPE html>

<%@ include file="/common/taglibs.jsp"%>

<lams:html>
		
		<c:set var="lams"> <lams:LAMSURL /> </c:set>
		<c:set var="tool"> <lams:WebAppURL /> </c:set>
	
	<lams:head>
		<title>
			<fmt:message key="activity.title" />
		</title>
		<lams:headItems minimal="true"/> 
		<link href="${tool}includes/css/chat.css" rel="stylesheet" type="text/css">
		<script type="text/javascript">
		var MODE = "${MODE}", TOOL_SESSION_ID = '${param.toolSessionID}', APP_URL = '<lams:WebAppURL />', LEARNING_ACTION = "<c:url value='learning/learning.do'/>", LAMS_URL = '<lams:LAMSURL/>';
		</script>
		<script type="text/javascript" src="<lams:WebAppURL />includes/javascript/learning.js"></script>
		
	</lams:head>
	<body class="stripes">
			
						
		<c:set var="title" scope="request">
				<fmt:message key="activity.title" />
		</c:set>		
		
		<lams:Page type="learner" title="${title}">
			<lams:DefineLater defineLaterMessageKey="message.defineLaterSet" />
		</lams:Page>



			
	</body>
</lams:html>
