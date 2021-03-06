<%@ include file="/common/taglibs.jsp"%>
<lams:html>
	<lams:head>
		<%@ include file="/common/header.jsp"%>
		<script type="text/javascript" src="${tool}includes/javascript/authoring.js"></script>
	</lams:head>

	<body>

		<div class="card card-plain add-file">
		<div class="card-header card-title">
			<fmt:message key="label.edit.nomination"></fmt:message>
		</div>
		
		<div class="card-body">

		<form:form action="saveSingleNomination.do" modelAttribute="voteAuthoringForm" id="newNominationForm" method="POST">
			<form:hidden path="toolContentID" />
			<form:hidden path="currentTab" />
			<form:hidden path="httpSessionID" />
			<form:hidden path="contentFolderID" />
			<form:hidden path="editableNominationIndex" />
			<input type="hidden" name="editNominationBoxRequest" value="true" />

			<div class="form-group">
				<lams:CKEditor id="newNomination"
					value="${voteGeneralAuthoringDTO.editableNominationText}"   
					contentFolderID="${voteGeneralAuthoringDTO.contentFolderID}">
				</lams:CKEditor>
			</div>
			
			<div class="voffset5 pull-right">
			    <a href="#" onclick="javascript:hideMessage()"
				class="btn btn-default btn-sm"> <fmt:message key="label.cancel" /></a>
				<a href="#" onclick="javascript:submitNomination()" class="btn btn-default btn-sm">
					<i class="fa fa-plus"></i>&nbsp; <fmt:message key="label.save.nomination" /> </a>
			</div>
					
		</form:form>
		</div>
		</div>

	</body>
</lams:html>
