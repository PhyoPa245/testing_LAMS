<%@ include file="/common/taglibs.jsp"%>

<div class="card card-plain">
	<h4>
	    <c:out value="${content.title}" escapeXml="true"/>
	</h4>
	<div class="instructions voffset5">
	    <c:out value="${content.instructions}" escapeXml="false"/>
	</div>
	
	<c:if test="${empty listAllGroupsDTO}">
		<lams:Alert type="info" close="false">
			<fmt:message key="error.noLearnerActivity" />
		</lams:Alert>
	</c:if>
	
	<c:if test="${content.useSelectLeaderToolOuput && not empty listAllGroupsDTO}">
		<lams:Alert type="info" id="no-session-summary" close="false">
			<fmt:message key="label.info.use.select.leader.outputs" />
		</lams:Alert>
	</c:if>
</div>

<c:forEach var="groupDto" items="${listAllGroupsDTO}" varStatus="status">
			  
	<c:if test="${isGroupedActivity}">	
	    <div class="card card-plain" >
        <div class="card-header" id="heading${sessionDto.sessionID}">
        	<span class="card-title collapsable-icon-left">
	        	<a class="${status.first ? '' : 'collapsed'}" role="button" data-toggle="collapse" href="#collapse${groupDto.sessionId}" 
						aria-expanded="${status.first ? 'false' : 'true'}" aria-controls="collapse${groupDto.sessionId}" >
					<fmt:message key="group.label" />:	<c:out value="${groupDto.sessionName}" />
				</a>
			</span>
        </div>
        
        <div id="collapse${groupDto.sessionId}" class="card-collapse collapse ${status.first ? 'in' : ''}" role="tabcard"
				aria-labelledby="heading${groupDto.sessionId}">
	</c:if>	 
	
	<c:if test="${content.reflect}"> 			
		<div style="margin-bottom: 20px">
			<c:set var="viewReflectionsURL"><lams:WebAppURL/>/monitoring/Reflections.jsp?toolSessionID=${groupDto.sessionId}</c:set>
			<button name="viewAllMarks" onclick="javascript:launchPopup('${viewReflectionsURL}')"
					class="btn btn-default loffset5 voffset10" >
				<fmt:message key="label.notebook.entries" />
			</button>
		</div>
	</c:if>
				  	
	<c:forEach var="question" items="${questionDTOs}">
		<div class="card card-plain"">
			<div class="card-header">
				<a href="javascript:launchPopup('<lams:WebAppURL/>monitoring/getPrintAnswers.do?questionUid=${question.uid}&toolSessionID=${groupDto.sessionId}');"	
					id="printAnswers" class="btn btn-default btn-xs pull-right"><i class="fa fa-print"></i></a>
				<c:out value="${question.question}" escapeXml="false"/>
			</div>
			<lams:TSTable numColumns="${content.allowRateAnswers ? (isCommentsEnabled ? 3 : 2) : (isCommentsEnabled ? 2 : 1)}"
						  dataId="data-session-id='${groupDto.sessionId}' data-question-uid='${question.uid}'">
				<th title="<fmt:message key='label.sort.by.answer'/>">
					<fmt:message key="label.learning.answer"/>
				</th>
				
				<c:if test="${isRatingsEnabled}">
					<th title="<fmt:message key='label.sort.by.rating'/>">
						<fmt:message key="label.learning.rating" />
					</th>
				</c:if>
				
				<c:if test="${isCommentsEnabled}">
					<th>
						<fmt:message key="label.comment" />
					</th>
				</c:if>
			</lams:TSTable>
		</div>
	</c:forEach>
	
	<c:if test="${isGroupedActivity}">
		</div> <!-- end collapse area  -->
		</div> <!-- end collapse card  -->
	</c:if>
	${ !isGroupedActivity || ! status.last ? '<div class="voffset5">&nbsp;</div>' :  ''}
		
</c:forEach>
		
<%@include file="AdvanceOptions.jsp"%>

<%@include file="dateRestriction.jsp"%>