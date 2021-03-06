<%@ include file="/common/taglibs.jsp"%>

<c:set var="dto" value="${contentDTO}" />

<div class="card card-no-border">
	<h4>
	    <c:out value="${contentDTO.title}" escapeXml="true"/>
	</h4>
	<div class="instructions voffset5">
	    <c:out value="${contentDTO.instructions}" escapeXml="false"/>
	</div>
	
 	<c:if test="${empty dto.sessionDTOs}">
		<lams:Alert type="info" id="no-session-summary" close="false">
			<fmt:message key="message.monitoring.summary.no.session" />
		</lams:Alert>
	</c:if>

</div>


<form action="/monitoring/startMeeting.do" target="zoomMonitor${dto.toolContentId}"
	  onsubmit="window.open('', 'zoomMonitor${dto.toolContentId}', 'resizable=yes,scrollbars=yes')"> 
	<input type="hidden" name="toolContentID" value="${dto.toolContentId}" />
	<button type="submit" class="btn btn-primary"><fmt:message key="label.monitoring.startConference" /></button>
</form>

<div class="voffset5">&nbsp;</div>
<c:if test="${dto.reflectOnActivity}">
	<table class="table table-striped">
		<tr>
			<th style="width: 30%;">
				<fmt:message key="heading.learner" />
			</th>
			<th style="width: 70%;">
				<fmt:message key="heading.notebookEntry" />
			</th>
		</tr>

		<c:forEach var="session" items="${dto.sessionDTOs}" varStatus="status">
			<c:forEach var="user" items="${session.userDTOs}">
				<tr>
					<td>
						<c:out value="${user.firstName} ${user.lastName}" escapeXml="true"/>
					</td>
					<td>
						<c:choose>
							<c:when test="${user.notebookEntryUID == null}">
								<fmt:message key="label.notAvailable" />
							</c:when>
	
							<c:otherwise>
								<a href="./monitoring/openNotebook.do?userUID=${user.uid}">
									<fmt:message key="label.view" /> </a>
							</c:otherwise>
						</c:choose>
	
					</td>
				</tr>
			</c:forEach>
		</c:forEach>
	</table>
</c:if>

		
<div class="voffset5">&nbsp;</div>

<c:set var="adTitle"><fmt:message key="monitor.summary.th.advancedSettings" /></c:set>
<lams:AdvancedAccordian title="${adTitle}">
	<table class="table table-striped table-sm">

	</table>
</lams:AdvancedAccordian>