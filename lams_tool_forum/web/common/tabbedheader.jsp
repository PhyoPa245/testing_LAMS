<%@ include file="/common/taglibs.jsp"%>
<c:set var="lams">
	<lams:LAMSURL />
</c:set>
<c:set var="tool">
	<lams:WebAppURL />
</c:set>
<c:set var="ctxPath" value="${pageContext.request.contextPath}" scope="request" />

<!-- HTTP 1.1 -->
<meta http-equiv="Cache-Control" content="no-store" />
<!-- HTTP 1.0 -->
<meta http-equiv="Pragma" content="no-cache" />
<!-- Prevents caching at the Proxy Server -->
<meta http-equiv="Expires" content="0" />

<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<!-- ********************  css and javascript ********************** -->
<lams:headItems/>
<script type="text/javascript" src="${lams}includes/javascript/jquery.blockUI.js"></script>  
<script type="text/javascript" src="${lams}includes/javascript/jquery.tablesorter.js"></script> 
<script type="text/javascript" src="${lams}includes/javascript/jquery.tablesorter-widgets.js"></script> 
<script type="text/javascript" src="${lams}includes/javascript/jquery.tablesorter-pager.js"></script> 
<script type="text/javascript" src="${lams}includes/javascript/upload.js"></script>
<script type="text/javascript" src="${tool}includes/javascript/message.js"></script>

