<!DOCTYPE html>
            
<%@include file="/common/taglibs.jsp"%>
<c:set var="lams">
	<lams:LAMSURL />
</c:set>
<c:set var="tool">
	<lams:WebAppURL />
</c:set>

<%@ page import="org.lamsfoundation.lams.tool.sbmt.SbmtConstants"%>
<lams:html>
<lams:head>
	
	<lams:headItems />
	<title><fmt:message key="activity.title" /></title>
	
	<script type="text/javascript">
        function doSelectTab(tabId) {
	    	selectTab(tabId);
        } 
    </script>

</lams:head>
<body class="stripes">
<form:form action="updateContent.do" id="authoringForm" modelAttribute="authoringForm" method="post" enctype="multipart/form-data">
	<c:set var="sessionMap" value="${sessionScope[authoringForm.sessionMapID]}" />
	<c:set var="title"><fmt:message key="activity.title" /></c:set>
	
	<input type="hidden" name="mode" value="${mode}" />
	<form:hidden path="sessionMapID" />
	<form:hidden path="toolContentID" />
	<form:hidden path="contentFolderID" />
	
	<lams:Page title="${title}" type="navbar" formID="authoringForm">
		
		<lams:Tabs control="true" title="${title}" helpToolSignature="<%= SbmtConstants.TOOL_SIGNATURE %>" helpModule="authoring">
			<lams:Tab id="1" key="label.authoring.heading.basic" />
			<lams:Tab id="2" key="label.authoring.heading.advance" />
		</lams:Tabs> 
		
		<lams:TabBodyArea>
			<lams:errors/>
	
		     <lams:TabBodys>
				<lams:TabBody id="1" titleKey="label.authoring.heading.basic.desc" page="basic.jsp" />
				<lams:TabBody id="2" titleKey="label.authoring.heading.advance.desc" page="advance.jsp" />
		    </lams:TabBodys>
	
			<lams:AuthoringButton formID="authoringForm"
				clearSessionActionUrl="/clearsession.do"
				toolSignature="<%=SbmtConstants.TOOL_SIGNATURE%>"
				accessMode="${sessionMap.mode}"
				defineLater="${sessionMap.mode == 'teacher'}"
				toolContentID="${authoringForm.toolContentID}"
				customiseSessionID="${authoringForm.sessionMapID}" 
				contentFolderID="${authoringForm.contentFolderID}" />
		</lams:TabBodyArea>
		
		<div id="footer"></div>
		
	</lams:Page>
</form:form>
</body>
</lams:html>