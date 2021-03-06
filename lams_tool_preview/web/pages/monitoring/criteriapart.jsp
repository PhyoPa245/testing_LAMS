<%@ include file="/common/taglibs.jsp"%>

<c:choose>
<c:when test="${criteria.ratingStyle eq 0 }">
	<c:set var="heading"></c:set>
</c:when>
<c:when test="${criteria.ratingStyle eq 1 }">
	<c:set var="heading"><fmt:message key="label.rating" /></c:set>
</c:when>
<c:when test="${criteria.ratingStyle eq 2 }">
	<c:set var="heading"><fmt:message key="label.rating.rank" /></c:set>
</c:when>
<c:when test="${criteria.ratingStyle eq 3 }">
	<c:set var="heading"><fmt:message key="label.mark" /></c:set>
</c:when>
</c:choose>

<script type="text/javascript">
	$(document).ready(function(){

		jQuery("#group${toolSessionId}").jqGrid({
		   	url: "<c:url value='/monitoring/getUsers.do'/>?toolContentId=${sessionMap.toolContentID}&toolSessionId=${toolSessionId}&criteriaId=${criteria.ratingCriteriaId}",
			datatype: "json",
			autoencode: false,
			height: 'auto',
			autowidth: true,
			shrinkToFit: false,
			guiStyle: "bootstrap",
			iconSet: 'fontAwesome',
		   	colNames:[
				'itemId',
				'<fmt:message key="label.user.name" />',
				'${heading}',
				'portraitId'
				<c:if test="${sessionMap.peerreview.notifyUsersOfResults}">, ''</c:if>
			],
		   	colModel:[
		   		{name:'itemId', index:'itemId', width:0, hidden: true},
		   		{name:'itemDescription', index:'itemDescription', width:200, searchoptions: { clearSearch: false }, formatter:userNameFormatter},
		   		{name:'rating', index:'rating', width:100, align:"center", search:false},
		   		{name:'itemDescription2', index:'itemId', width:0, hidden: true}
				<c:if test="${sessionMap.peerreview.notifyUsersOfResults}">, {name:'email', index:'email', width:100, align:"center", search:false}</c:if>		   		
		   	],
		   	rowNum:10,
		   	rowList:[10,20,30,40,50,100],
		   	pager: '#pager${toolSessionId}',
		   	viewrecords:true,
			loadComplete: function(){
				initializeJRating();
				initializePortraitPopover('<lams:LAMSURL/>');
			},
		   	// caption: "${groupSummary.sessionName}" use Bootstrap cards as the title bar
			subGrid: true,
			subGridOptions: {
				reloadOnExpand : false 
			},
			subGridRowExpanded: function(subgrid_id, row_id) {
					var subgridTableId = subgrid_id+"_t";
					var itemId = jQuery("#group${toolSessionId}").getRowData(row_id)["itemId"];
					   
					jQuery("#"+subgrid_id).html("<table id='" + subgridTableId + "' class='scroll'></table>");
					   
					jQuery("#"+subgridTableId).jqGrid({
						datatype: "json",
						loadonce:true,
						rowNum: 10000,
						url: "<c:url value='/monitoring/getSubgridData.do'/>?toolContentId=${sessionMap.toolContentID}&toolSessionId=${toolSessionId}&criteriaId=${criteria.ratingCriteriaId}&itemId=" + itemId,
						height: "100%",
						autowidth:true,
						grouping:true,	
						groupingView : {
							groupField : ['criteriaId'],
							groupColumnShow : [false]
						},
						guiStyle: "bootstrap",
						iconSet: 'fontAwesome',
						colNames:[
							'id',
							'<fmt:message key="label.user.name" />',
							'${heading}',
							'criteriaId'
							],
						colModel:[
						   {name:'id', index:'id', width:0, hidden:true},
						   {name:'userName', index:'userName', width:100 },
						   {name:'rating', index:'rating', width:200, align:"center",  title:false},
						   {name:'criteriaId', width:0, hidden:true}
						],
						loadComplete: function(){
							//remove empty subgrids
					        var table_value = $('#'+subgridTableId).getGridParam('records');
					        if(table_value === 0){
					            $('#'+subgrid_id).parent().unbind('click').html('<fmt:message key="label.no.ratings.left" />');
					        }
						},
						loadError: function(xhr,st,err) {
					    	jQuery("#"+subgridTableId).clearGridData();
					    	info_dialog("<fmt:message key="label.error"/>", "<fmt:message key="gradebook.error.loaderror"/>", "<fmt:message key="label.ok"/>");
					    }
					})
			}
		}).jqGrid('filterToolbar', { 
			searchOnEnter: false
		})
		.navGrid('#pager${toolSessionId}',{add:false,del:false,edit:false,search:false});
        
        //jqgrid autowidth (http://stackoverflow.com/a/1610197)
        $(window).bind('resize', function() {
            resizeJqgrid(jQuery(".ui-jqgrid-btable:visible"));
        });

        //resize jqGrid on openning of bootstrap collapsible
        $('div[id^="collapse"]').on('shown.bs.collapse', function () {
            resizeJqgrid(jQuery(".ui-jqgrid-btable:visible", this));
        })

        function userNameFormatter (cellvalue, options, rowObject) {
			return definePortraitPopover(rowObject.itemDescription2, rowObject.itemId,  rowObject.itemDescription);
		}

        function resizeJqgrid(jqgrids) {
            jqgrids.each(function(index) {
                var gridId = $(this).attr('id');
                var gridParentWidth = jQuery('#gbox_' + gridId).parent().width();
                jQuery('#' + gridId).setGridWidth(gridParentWidth, true);
            });
        };
        setTimeout(function(){ window.dispatchEvent(new Event('resize')); }, 300);
        
	});

	function closeResultsForLearner() {
		$("#emailPreviewArea").html("");
		$("#emailPreviewArea").hide();
		return false;
	}

	function clearMessage() {
		$("#messageArea2").html("");
		return false;
	}

	// Prview the email to be sent to the learner
	function previewResultsForLearner(sessionId, userId) {
		$(".btn-disable-on-submit").prop("disabled", true);
		var url = "<c:url value="/monitoring/previewResultsToUser.do"/>";
		clearMessage();
		$("#messageArea2_Busy").show();
		$("#emailPreviewArea").load(
			url,
			{
				sessionMapID: "${sessionMapID}",
				toolContentID: ${sessionMap.toolContentID},
				toolSessionId: sessionId, 
				userID: userId,
				reqID: (new Date()).getTime()
			},
			function() {
				$("#messageArea2_Busy").hide();
				$("#emailPreviewArea").show();
				$(".btn-disable-on-submit").prop("disabled", false);
			}
		);
		return false;
	}

	// Send the previewed email to the learner
	function sendResultsForLearner(sessionId, userId, dateTimeStamp) {
		$(".btn-disable-on-submit").prop("disabled", true);
		var url = "<c:url value="/monitoring/sendPreviewedResultsToUser.do"/>";
		clearMessage();
		$("#messageArea2_Busy").show();
		$("#messageArea2").load(
			url,
			{
				sessionMapID: "${sessionMapID}",
				toolContentID: ${sessionMap.toolContentID},
				toolSessionId: sessionId, 
				dateTimeStamp: dateTimeStamp,
				userID: userId,
				reqID: (new Date()).getTime()
			},
			function() {
				$("#messageArea2_Busy").hide();
				closeResultsForLearner();
				$(".btn-disable-on-submit").prop("disabled", false);
			}
		);
		return false;
	}

</script>

<!--For send results feature-->
<i class="fa fa-spinner" style="display:none" id="messageArea2_Busy"></i>
<div class="voffset5" id="messageArea2"></div>

<p>
	<c:choose>
	<c:when test="${criteria.ratingStyle eq 2}">
		<c:choose>
		<c:when test="${criteria.maxRating gt 0 }">
			<fmt:message key="label.monitoring.instructions.rank.some.learners">
				<fmt:param>${criteria.maxRating}</fmt:param>
			</fmt:message>
		</c:when>
		<c:otherwise>
			<fmt:message key="label.monitoring.instructions.rank.all.learners"/>
		</c:otherwise>
		</c:choose>			
	</c:when>
	<c:when test="${criteria.ratingStyle eq 3 }">
		<fmt:message key="label.monitoring.instructions.assign.some.marks">
			<fmt:param>${criteria.maxRating}</fmt:param>
		</fmt:message>
	</span>
	</c:when>
	</c:choose>
</p>

<table id="group${toolSessionId}" class="scroll" cellpadding="0" cellspacing="0"></table>
<div id="pager${toolSessionId}"></div> 

<div class="voffset10" id="emailPreviewArea" style="display:none" ></div>