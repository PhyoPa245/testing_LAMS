<!DOCTYPE html>
		

<%@ include file="/common/taglibs.jsp"%>
<lams:html>
<lams:head>
	<%@ include file="/common/header.jsp"%>
	<lams:css/>
	<!-- To use in external script files. -->
	<script type="text/javascript">
	   var msgShowAdditionalOptions = "<fmt:message key='label.authoring.basic.additionaloptions.show' />";
       var msgHideAdditionalOptions = "<fmt:message key='label.authoring.basic.additionaloptions.hide' />";
	   	//Initial behavior
	   	$(document).ready(function() {
	   		defaultShowAdditionaOptionsArea();
	   	});
	</script>
</lams:head>
<body class="tabpart">

<div class="card card-plain">
<div class="card-header">
	<div class="card-title"><fmt:message key="label.authoring.basic.textarea" /></div>
</div>

<div class="card-body">

<!-- Add question form-->
<lams:errors/>
<form:form action="saveOrUpdateQuestion.do" modelAttribute="questionForm" method="post" id="dacoQuestionForm">
	<form:hidden path="sessionMapID" />
	<input type="hidden" id="questionType" name="questionType" value="2" />
	<form:hidden path="questionIndex" />
	
	<p><fmt:message key="label.authoring.basic.textarea.help" /></p>
	
	<%@ include file="description.jsp"%>
 
   	<!--  Options -->  
	<a href="javascript:toggleAdditionalOptionsArea()" class="fa-xs"><i id="faIcon" class="fa fa-plus-square-o"></i> <span id="toggleAdditionalOptionsAreaLink"><fmt:message key="label.authoring.basic.additionaloptions.show" /></span></a>
	<div id="additionalOptionsArea" style="display: none;" class="card-body">
 		<div class="form-group form-inline">
			<div class="checkbox">
			    <label>
		 	      <form:checkbox path="questionRequired" id="questionRequired"/>&nbsp;<fmt:message key="label.authoring.basic.required" />
			    </label>
	  	</div>
		</div>
 		<div class="form-group form-inline">
    	<label for="max"><fmt:message key="label.authoring.basic.max.word" />:</label>
	 		<form:input id="max" path="max" size="10"  cssClass="form-control"/>
		</div>	  	
 </div>
 	<!--  end options -->
  	
</form:form>

<c:set var="addButtonMessageKey" value="label.authoring.basic.textarea.add" />
<%@ include file="buttons.jsp"%>

</div>
</div>

</body>
</lams:html>
