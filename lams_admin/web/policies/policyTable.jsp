<%@ include file="/taglibs.jsp"%>

<table class="table table-striped table-bordered" >
	<thead class="thead-light">
	<tr>
		<th><fmt:message key="label.name" /></th>
		<th><fmt:message key="label.policy.status" /></th>
		<th><fmt:message key="label.policy.type" /></th>
		<th><fmt:message key="label.version" /></th>
		<th><fmt:message key="label.last.modified" /></th>
		<th><fmt:message key="label.consents"/></th>
		<th colspan="3"><fmt:message key="admin.actions"/></th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${policies}" var="policy">
		<tr>
			<td class="text-left">
				<a href="<lams:WebAppURL />policyManagement/edit.do?policyUid=${policy.uid}">
					<c:out value="${policy.policyName}" escapeXml="true" />
				</a>
			</td>
			<td>
				<c:choose>
					<c:when test="${policy.policyStateId == 1}">
						<span class="label label-success">
							<span class="badge badge-success"><fmt:message key="label.policy.status.active"/></span>
						</span>
					</c:when>
					<c:otherwise>
						<span class="label label-warning">
							<span class="badge badge-warning"><fmt:message key="label.policy.status.inactive"/>
						</span>
					</c:otherwise>
				</c:choose>
			</td>
			<td>
				<c:choose>
					<c:when test="${policy.policyTypeId == 1}">
						<fmt:message key="label.policy.type.site"/>
					</c:when>
					<c:when test="${policy.policyTypeId == 2}">
						<fmt:message key="label.policy.type.privacy"/>
					</c:when>
					<c:when test="${policy.policyTypeId == 3}">
						<fmt:message key="label.policy.type.third.party"/>
					</c:when>
					<c:when test="${policy.policyTypeId == 4}">
						<fmt:message key="label.policy.type.other"/>
					</c:when>
				</c:choose>
			</td>
			<td>
				<c:out value="${policy.version}" escapeXml="true" />
			</td>
			<td>
				<lams:Date value="${policy.lastModified}" timeago="true"/>
			</td>
			<td>
				<a href="#nogo" class="policy-consents-link" data-policy-uid="${policy.uid}">
					${fn:length(policy.consents)}/${userCount}
				</a>
			</td>
			
			<!-- Actions columns -->
			<td>
				<a href="<lams:WebAppURL />policyManagement/edit.do?policyUid=${policy.uid}">
					<button class="btn btn-outline-primary btn-sm" title="<fmt:message key="admin.edit"/>">
						<i class="fa fa-pencil"></i>
					</button>
				</a>
			</td>
						
			<td>
				<c:if test="${!viewPreviousVersions && policy.hasPreviousVersions()}">
					<a href="<lams:WebAppURL />policyManagement/viewPreviousVersions.do?policyId=${policy.policyId}">
						<button  title="<fmt:message key="label.view.previous.versions"/>" class="btn btn-outline-primary btn-sm" >
							<i class="fa fa-code-fork fa-fw"></i>
						</button>
					</a>
				</c:if>	
			</td>

			<td>
				<a href="#nogo" class="change-status-link" data-policy-uid="${policy.uid}" data-policy-id="${policy.policyId}">
					<c:choose>
						<c:when test="${policy.policyStateId == 1}">
							<button class="btn btn-outline-warning btn-sm" title="<fmt:message key="label.deactivate"/>"><i class="fa fa-pause"></i></button>
						</c:when>
						<c:otherwise>
							<button class="btn btn-outline-success btn-sm" title="<fmt:message key="label.activate"/>">
								<i class="fa fa-power-off"></i>
							</button>
											
						</c:otherwise>
					</c:choose>
				</a>
			</td>
			<!-- End of Actions columns -->
				
		</tr>
	</c:forEach>
	</tbody>
</table>
