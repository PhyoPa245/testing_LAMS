<%@ include file="/common/taglibs.jsp"%>
<c:set var="sessionMap" value="${sessionScope[sessionMapID]}"/>
<c:set var="resource" value="${sessionMap.resource}"/>

<c:if test="${sessionMap.isPageEditable}">
	<lams:Alert type="warn" id="no-edit" close="false">
		<fmt:message key="message.alertContentEdit" />
	</lams:Alert>
</c:if>

<table class="table table-sm">
	<tr>
		<td width="15%" nowrap>
			<fmt:message key="label.authoring.basic.title" />:
		</td>
		<td>
			<c:out value="${resource.title}" escapeXml="true" />
		</td>
	</tr>

	<tr>
		<td width="15%" nowrap>
			<fmt:message key="label.authoring.basic.instruction" />:
		</td>
		<td>
			<c:out value="${resource.instructions}" escapeXml="false" />
		</td>
	</tr>

	<tr>
		<td colspan="2">
			<c:url  var="authoringUrl" value="/authoring/definelater.do">
				<c:param name="toolContentID" value="${sessionMap.toolContentID}" />
				<c:param name="contentFolderID" value="${sessionMap.contentFolderID}" />
			</c:url>
			<a href="javascript:;" onclick="launchPopup('${authoringUrl}','definelater')" class="btn btn-default pull-right">
				<fmt:message key="label.monitoring.edit.activity.edit" />
			</a>
		</td>
	</tr>
</table>
