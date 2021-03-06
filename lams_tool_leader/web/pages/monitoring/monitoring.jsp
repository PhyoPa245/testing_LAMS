<!DOCTYPE html>

<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.lamsfoundation.lams.tool.leaderselection.util.LeaderselectionConstants"%>
<lams:html>
	<c:set var="lams">
		<lams:LAMSURL />
	</c:set>
	<c:set var="tool">
		<lams:WebAppURL />
	</c:set>

	<lams:head>
		<title>
			<fmt:message key="activity.title" />
		</title>
		<lams:headItems />
		<c:set var="tool">
			<lams:WebAppURL />
		</c:set>
		<c:set var="lams"><lams:LAMSURL /></c:set>
		
		<link type="text/css" href="${lams}css/thickbox.css" rel="stylesheet"  media="screen">
		<link type="text/css" href="${lams}/css/jquery-ui-bootstrap-theme.css" rel="stylesheet">
		<style media="screen,projection" type="text/css">
			.bottom-buttons {margin: 20px 20px 0px; padding-bottom: 10px;}
			table {padding: 10px;}
		</style>
		
		<script type="text/javascript" src="${lams}includes/javascript/jquery-ui.js"></script>
		<script type="text/javascript" src="${lams}includes/javascript/thickbox.js"></script>
		<script type="text/javascript" src="${lams}includes/javascript/portrait.js"></script>
		<script type="text/javascript" src="${tool}includes/javascript/monitoring.js"></script>

	</lams:head>

	<body class="stripes" onload="init();">
		<c:set var="title"><fmt:message key="activity.title" /></c:set>
		<lams:Page title="${title}" type="navbar">
	
		<lams:Tabs title="${title}" control="true" helpToolSignature="<%= LeaderselectionConstants.TOOL_SIGNATURE %>" helpModule="monitoring">
			<lams:Tab id="1" key="button.summary" />
			<lams:Tab id="2" key="button.editActivity" />
			<lams:Tab id="3" key="button.statistics" />
		</lams:Tabs>

		<script type="text/javascript">
			var initialTabId = "${leaderselectionDTO.currentTab}";
		</script>
	
		<lams:TabBodyArea>
			<lams:TabBodys>
				<lams:TabBody id="1" titleKey="button.summary" page="summary.jsp" />
				<lams:TabBody id="2" titleKey="button.editActivity" page="editActivity.jsp" />
				<lams:TabBody id="3" titleKey="button.statistics" page="statistics.jsp" />
			</lams:TabBodys>
		</lams:TabBodyArea>
		
		<div id="footer" />
	
		</lams:Page>
	</body>
</lams:html>


