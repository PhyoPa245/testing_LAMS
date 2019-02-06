<%@ include file="/common/taglibs.jsp"%>
<c:set var="lams"><lams:LAMSURL/></c:set>
<c:set var="tool"><lams:WebAppURL/></c:set>
<c:set var="ctxPath" value="${pageContext.request.contextPath}" scope="request"/>
<c:set var="spreadsheetURL"><lams:WebAppURL/>includes/javascript/simple_spreadsheet/spreadsheet_offline.html?lang=${pageContext.response.locale.language}</c:set>

<!-- ********************  CSS ********************** -->
<lams:css />

<!-- ********************  javascript ********************** -->
<script type="text/javascript" src="${lams}includes/javascript/common.js"></script>
<script type="text/javascript" src="<lams:WebAppURL/>includes/javascript/spreadsheetcommon.js"></script>
<script type="text/javascript" src="${lams}includes/javascript/jquery.js"></script>
<script type="text/javascript" src="${lams}includes/javascript/popper.min.js"></script>
<script type="text/javascript" src="${lams}includes/javascript/bootstrap-material-design.min.js"></script>
<script type="text/javascript" src="${lams}includes/javascript/bootstrap.tabcontroller.js"></script>
	

	
