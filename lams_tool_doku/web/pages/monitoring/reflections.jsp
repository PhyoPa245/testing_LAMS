<%@ include file="/common/taglibs.jsp"%>

<script type="text/javascript">
	$(document).ready(function(){
		initializePortraitPopover("<lams:LAMSURL />");
	});
</script>
		
<div class="accordian" id="accordionReflection" role="tablist" aria-multiselectable="true"> 
    <div class="card card-plain" >
        <div class="card-header collapsable-icon-left" id="headingReflection">
        	<span class="card-title">
	    		<a class="collapsed" role="button" data-toggle="collapse" href="#collapseReflection" 
	    				aria-expanded="false" aria-controls="collapseReflection" >
          			<fmt:message key="title.reflection"/>
        		</a>
      		</span>
        </div>

        <div id="collapseReflection" class="card-collapse collapse" role="tabcard" aria-labelledby="headingReflection">
			<table class="table table-striped table-sm">
				<c:forEach var="reflectDTO" items="${sessionMap.reflectList}">
					<tr>
						<td>
							<lams:Portrait userId="${reflectDTO.userId}" hover="true"><strong><c:out value="${reflectDTO.fullName}" escapeXml="true"/></strong></lams:Portrait>
							 - <lams:Date value="${reflectDTO.date}"/>
							<br>
							<lams:out value="${reflectDTO.reflect}" escapeHtml="true" />
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</div>
