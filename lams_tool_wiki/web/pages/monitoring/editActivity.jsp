<%@ include file="/common/taglibs.jsp"%>

<c:set var="dto" value="${wikiDTO}" />

<c:if test="${dto.contentInUse}">
	<lams:Alert type="warn" id="no-edit" close="false">
		<fmt:message key="message.alertContentEdit" />
	</lams:Alert>
</c:if>

<table class="table table-sm">
	<tbody>
		<tr>
			<td class="field-name" width="30%">
				<fmt:message key="label.authoring.basic.title" />
			</td>
			<td>
				<c:out value="${dto.title}" escapeXml="true" />
			</td>
		</tr>
		<tr>
			<td class="field-name" width="30%">
				<fmt:message key="label.authoring.basic.instructions" />
			</td>
			<td>
				<c:out value="${dto.instructions}" escapeXml="false" />
			</td>
		</tr>
	</tbody>
</table>

<c:url value="/authoring/authoring.do" var="authoringUrl">
	<c:param name="toolContentID" value="${dto.toolContentId}" />
	<c:param name="mode" value="teacher" />
	<c:param name="contentFolderID" value="${contentFolderID}" />
</c:url>
<a href="#nogo" onclick="javascript:launchPopup('${authoringUrl}','definelater')" class="btn btn-default pull-right">
	<fmt:message key="button.editActivity" />
</a>


