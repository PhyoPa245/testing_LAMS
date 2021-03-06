<!DOCTYPE html>
<%@ page contentType="text/html; charset=utf-8" language="java"%>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt"%>
<%@ taglib uri="tags-lams" prefix="lams"%>

<lams:html>
<lams:head>
	<title><fmt:message key="label.policy.details" /></title>
	<link rel="icon" href="/lams/favicon.ico" type="image/x-icon" />
	<link rel="shortcut icon" href="/lams/favicon.ico" type="image/x-icon" />
	<lams:css/>
	<lams:css suffix="main"/>
</lams:head>

<body>
<div style="clear: both;"></div>
<div class="container">
<div class="row vertical-center-row">
<div>
<div class="card card-plain">
	<div class="card-header">
		<div class="card-title">
			<h4><fmt:message key="label.policy.details" /></h4>
		</div>
	</div>

<div class="card-body">		
	<table class="table table-striped table-no-border">
		<tr>
			<td><fmt:message key="label.name" /></td>
			<td><c:out value="${policy.policyName}" /></td>
		</tr>
		<tr>
			<td style="width: 250px;"><fmt:message key="label.policy.type" /></td>
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
		</tr>
		<tr>
			<td><fmt:message key="label.version" /></td>
			<td><c:out value="${policy.version}" escapeXml="false"/></td>
		</tr>
		<tr>
			<td><fmt:message key="label.summary" /></td>
			<td>
				<c:out value="${policy.summary}" escapeXml="false"/>
			</td>
		</tr>
		<tr>
			<td><fmt:message key="label.full.policy" /></td>
			<td>
				<c:out value="${policy.fullPolicy}" escapeXml="false"/>
			</td>
		</tr>
	</table>
					
	<div class="pull-right">
		<input class="btn btn-default" type="button" value="<fmt:message key="label.authoring.close" />"
				onclick="javascript:window.close();" />
	</div>

</div>
</div>
</div>
</div>
</div>
</body>
</lams:html>
