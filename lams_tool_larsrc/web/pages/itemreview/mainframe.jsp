<!DOCTYPE html>
<%-- 
Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
License Information: http://lamsfoundation.org/licensing/lams/2.0/

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License version 2 as
  published by the Free Software Foundation.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
  USA

  http://www.gnu.org/licenses/gpl.txt 
--%>
<%@ include file="/common/taglibs.jsp" %>
<%@ page import="org.lamsfoundation.lams.tool.rsrc.ResourceConstants"%>

<lams:html>
<lams:head>
	<%@ include file="/common/header.jsp"%>

	<c:set var="initNavUrl"><c:url value="/pages/itemreview/initnav.jsp"/>?mode=${mode}&itemIndex=${itemIndex}&itemUid=${itemUid}&toolSessionID=${toolSessionID}&sessionMapID=${sessionMapID}</c:set>
	<c:set var="sessionMap" value="${sessionScope[sessionMapID]}"/>
	
	<style media="screen,projection" type="text/css">
 	 	#collapseComments, #collapseComments .row {
	 		padding-left: 6px;
	 		padding-right: 6px;
	 	}
	}
	
	</style>

	<script type="text/javascript">
		$(document).ready(function(){
			$.ajaxSetup({ cache: true });
    			$('#headerFrame').load('${initNavUrl}');
   		});
		
		function setIframeHeight() {
			var rscFrame = document.getElementById('resourceFrame');
		    var doc = rscFrame.contentDocument? rscFrame.contentDocument : rscFrame.contentWindow.document;
	        var body = doc.body;
	        var html = doc.documentElement;
	        var height = Math.max( body.scrollHeight, body.offsetHeight, 
	            html.clientHeight, html.scrollHeight, html.offsetHeight );
		    rscFrame.style.height = height + "px";
		}
	</script>
</lams:head>

<body class="stripes">

	<lams:Page title="" type="learner" usePanel="false" hideProgressBar="${!sessionMap.runAuto}">
		<div class="card card-plain">
 			<div class="card-header">
				<div id="headerFrame"></div>
			</div>
 			<c:if test="${allowComments and (mode eq 'learner' or mode eq 'author') and not empty toolSessionID}">
 				<c:set var="accordianTitle"><fmt:message key="label.comments"/></c:set>
				<lams:Comments toolSessionId="${toolSessionID}" toolSignature="<%=ResourceConstants.TOOL_SIGNATURE%>" embedInAccordian="true" accordionTitle="${accordianTitle}" mode="${mode}" toolItemId="${itemUid}" readOnly="${sessionMap.finishedLock}"/>	
			</c:if>
   			<div class="card-body" style="height:100vh;">
 				<iframe src="<c:url value='${resourceItemReviewUrl}'/>" id="resourceFrame" style="border:0px;width:100%;height:100%;" onload="setIframeHeight()"></iframe>
 			</div> 
   		</div>
	</lams:Page>

</body>	
</lams:html>
