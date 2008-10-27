<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.lamsfoundation.lams.tool.chat.util.ChatConstants"%>

<html:form action="/authoring" styleId="authoringForm" method="post"
	enctype="multipart/form-data">

	<c:set var="formBean"
		value="<%=request
										.getAttribute(org.apache.struts.taglib.html.Constants.BEAN_KEY)%>" />
	<c:set var="sessionMap" value="${sessionScope[formBean.sessionMapID]}" />

	<c:set var="defineLater" value="no" />
	<c:if test="${sessionMap.mode == 'teacher'}">
		<c:set var="defineLater" value="yes" />
	</c:if>

	<div id="header">
		<lams:Tabs control="true">
			<lams:Tab id="1" key="button.basic" />
			<c:if test="${sessionMap.mode == 'author'}">
				<lams:Tab id="2" key="button.advanced" />
				<lams:Tab id="3" key="button.instructions" />
				<lams:Tab id="4" key="button.conditions" />
			</c:if>
		</lams:Tabs>
	</div>
	<!--closes header-->

	<div id="content">

		<div>
			<html:hidden property="currentTab" styleId="currentTab" />
			<html:hidden property="dispatch" value="updateContent" />
			<html:hidden property="sessionMapID" />
		</div>

		<div id="message" style="text-align: center;">
			<c:if test="${unsavedChanges}">
				<fmt:message key="message.unsavedChanges" />
			</c:if>
			<logic:messagesPresent>
				<p class="warning">
				        <html:messages id="error">
				            <c:out value="${error}" escapeXml="false"/><br/>
				        </html:messages>
				</p>
			</logic:messagesPresent>
		</div>
		
		<lams:help toolSignature="<%=ChatConstants.TOOL_SIGNATURE%>"
			module="authoring" />

		<%-- Page tabs --%>
		<lams:TabBody id="1" titleKey="button.basic" page="basic.jsp" />
		<c:if test="${sessionMap.mode == 'author'}">
			<lams:TabBody id="2" titleKey="button.advanced" page="advanced.jsp" />
			<lams:TabBody id="3" titleKey="button.instructions"
				page="instructions.jsp" />
			<lams:TabBody id="4" titleKey="button.conditions"
				page="conditions.jsp" />
		</c:if>

		<lams:AuthoringButton formID="authoringForm"
			clearSessionActionUrl="/clearsession.do" toolSignature="lachat11"
			cancelButtonLabelKey="button.cancel" saveButtonLabelKey="button.save"
			toolContentID="${sessionMap.toolContentID}"
			accessMode="${sessionMap.mode}" defineLater="${defineLater}"
			customiseSessionID="${sessionMap.sessionID}"
			contentFolderID="${sessionMap.contentFolderID}" />
	</div>
	<!--closes content-->
</html:form>

<div id="footer"></div>
