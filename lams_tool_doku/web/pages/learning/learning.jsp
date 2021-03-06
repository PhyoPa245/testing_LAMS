<!DOCTYPE html>
<%@ include file="/common/taglibs.jsp"%>
<%-- param has higher level for request attribute --%>
<c:if test="${not empty param.sessionMapID}">
	<c:set var="sessionMapID" value="${param.sessionMapID}" />
</c:if>
<c:set var="sessionMap" value="${sessionScope[sessionMapID]}" />
<c:set var="mode" value="${sessionMap.mode}" />
<c:set var="toolSessionID" value="${sessionMap.toolSessionID}" />
<c:set var="dokumaran" value="${sessionMap.dokumaran}" />
<c:set var="finishedLock" value="${sessionMap.finishedLock}" />
<c:set var="hasEditRight" value="${sessionMap.hasEditRight}"/>
<c:set var="isTimeLimitEnabled" value="${hasEditRight && assessment.getTimeLimit() != 0 && !finishedLock}" />
<c:set var="localeLanguage"><lams:user property="localeLanguage" /></c:set>
	
<lams:html>
<lams:head>
	<title><fmt:message key="label.learning.title" /></title>
	<%@ include file="/common/header.jsp"%>
	<link rel="stylesheet" type="text/css" href="${lams}css/jquery.countdown.css" />
	<style media="screen,projection" type="text/css">
		#countdown {
			width: 150px; 
			font-size: 110%; 
			font-style: italic; 
			color:#47bc23;
			text-align: center;
		}
		#countdown-label {
			font-size: 170%; padding-top:5px; padding-bottom:5px; font-style: italic; color:#47bc23;
		}
		
		.lower-to-fit-countdown {
			margin-top: 70px;
		}
	</style>

	<script type="text/javascript" src="${lams}includes/javascript/jquery.plugin.js"></script>
	<script type="text/javascript" src="${lams}includes/javascript/jquery.countdown.js"></script>
	<script type="text/javascript" src="${lams}includes/javascript/jquery.blockUI.js"></script>
	<script type="text/javascript" src="<lams:WebAppURL/>includes/javascript/etherpad.js"></script>
	<script type="text/javascript">
		$(document).ready(function(){
			
			$('#etherpad-container').pad({
				'padId':'${padId}',
				'host':'${etherpadServerUrl}',
				'lang':'${fn:toLowerCase(localeLanguage)}',
				'showControls':'${hasEditRight}',
				'showChat':'${dokumaran.showChat}',
				'showLineNumbers':'${dokumaran.showLineNumbers}',
				'height':'' + ($(window).height() - 200)
				<c:if test="${hasEditRight}">,'userName':'<lams:user property="firstName" />&nbsp;<lams:user property="lastName" />'</c:if>
			});
			
			//hide finish button for non-leaders until leader will finish activity 
			if (${!hasEditRight && !sessionMap.userFinished && !sessionMap.isLeaderResponseFinalized}) {
				$("#finish-button").hide();
			}
			
			if (${secondsLeft > 0}) {
				displayCountdown()
			}
			
		});
		
		if (${!hasEditRight && mode != "teacher" && !finishedLock}) {
			setInterval("checkLeaderProgress();", 15000);// Auto-Refresh every 15 seconds for non-leaders
		}
		
		function checkLeaderProgress() {
	        $.ajax({
	        	async: false,
	            url: '<c:url value="/learning/checkLeaderProgress.do"/>',
	            data: 'toolSessionID=${toolSessionID}',
	            dataType: 'json',
	            type: 'post',
	            success: function (json) {
	            	if (json.isLeaderResponseFinalized) {
	            		$("#finish-button").show();
	            	}
	            }
	       	});
		}
	
		function finishSession(){
			document.getElementById("finish-button").disabled = true;
			document.location.href ='<c:url value="/learning/finish.do?sessionMapID=${sessionMapID}&mode=${mode}&toolSessionID=${toolSessionID}"/>';
			return false;
		}
		
		function continueReflect(){
			document.location.href='<c:url value="/learning/newReflection.do?sessionMapID=${sessionMapID}"/>';
		}
		
		<c:if test="${isTimeLimitEnabled}">
			//init the connection with server using server URL but with different protocol
			var dokuWebsocketInitTime = Date.now(),
				dokuWebsocket = new WebSocket('<lams:WebAppURL />'.replace('http', 'ws') 
							+ 'learningWebsocket?toolContentID=' + ${toolContentID}),
				dokuWebsocketPingTimeout = null,
				dokuWebsocketPingFunc = null;
			
			dokuWebsocket.onclose = function(){
				// react only on abnormal close
				if (e.code === 1006 &&
					Date.now() - dokuWebsocketInitTime > 1000) {
					location.reload();
				}
			};
			
			dokuWebsocketPingFunc = function(skipPing){
				if (dokuWebsocket.readyState == dokuWebsocket.CLOSING 
						|| dokuWebsocket.readyState == dokuWebsocket.CLOSED){
					return;
				}
				
				// check and ping every 3 minutes
				dokuWebsocketPingTimeout = setTimeout(dokuWebsocketPingFunc, 3*60*1000);
				// initial set up does not send ping
				if (!skipPing) {
					dokuWebsocket.send("ping");
				}
			};
			
			// set up timer for the first time
			dokuWebsocketPingFunc(true);
			
			// run when the server pushes new reports and vote statistics
			dokuWebsocket.onmessage = function(e) {
				// reset ping timer
				clearTimeout(dokuWebsocketPingTimeout);
				dokuWebsocketPingFunc(true);
				
				// create JSON object
				var input = JSON.parse(e.data);
				
				//monitor has added one minute to the total timeLimit time
				if (input.addTime) {
					//reload page in order to allow editing the pad again
					if (!$('#countdown').length) {
						location.reload();
					}
					
			    	var times = $("#countdown").countdown('getTimes'),
			    		secondsLeft = times[4]*3600 + times[5]*60 + times[6] + input.addTime*60;
			    	$('#countdown').countdown('option', "until", '+' + secondsLeft + 'S');
					
					return;
				}
			};			
		</c:if>
		
		function displayCountdown() {
			$.blockUI({
				message: '<div id="countdown"></div>', 
				showOverlay: false,
				focusInput: false,
				css: { 
					top: '10px',
					left: '',
					right: '1%',
					width: '150px',
			        opacity: '.8',
			        cursor: 'default',
			        border: 'none'
		        }   
			});
			
			$('#countdown').countdown({
				until: '+${secondsLeft}S',
				format: 'hMS',
				compact: true,
				description: "<div id='countdown-label'><fmt:message key='label.time.left' /></div>",
				onTick: function(periods) {
					//check for 30 seconds
					if ((periods[4] == 0) && (periods[5] == 0) && (periods[6] <= 30)) {
						$('#countdown').css('color', '#FF3333');
					}					
				},
				onExpiry: function(periods) {
			        $.blockUI({ message: '<h1 id="timelimit-expired"><i class="fa fa-refresh fa-spin fa-1x fa-fw"></i> <fmt:message key="label.time.is.over" /></h1>' }); 
			        
			        setTimeout(function() { 
			        	location.reload();
			        }, 4000); 
				}
			});
		}
	</script>
</lams:head>
<body class="stripes">

	<lams:Page type="learner" title="${dokumaran.title}" style="">
	
		<!--  Warnings -->
		<c:if test="${sessionMap.lockOnFinish and mode != 'teacher'}">
			<lams:Alert type="danger" id="warn-lock" close="false">
				<c:choose>
					<c:when test="${sessionMap.userFinished}">
						<fmt:message key="message.activityLocked" /> 
					</c:when>
					<c:otherwise>
						<fmt:message key="message.warnLockOnFinish" />
					</c:otherwise>
				</c:choose>
			</lams:Alert>
		</c:if>

		<lams:errors/>

		<div class='card card-plain 
				<c:if test="${isTimeLimitEnabled}">lower-to-fit-countdown</c:if>'>			
			<div id="etherpad-container"></div>
			<div id="etherpad-containera"></div>
			<div id="etherpad-containerb"></div>
		</div>

		<!-- Reflection -->
		<c:if test="${sessionMap.userFinished and sessionMap.reflectOn}">
			<div class="card card-plain">
				<div class="card-header card-title">
					<fmt:message key="title.reflection" />
				</div>
				<div class="card-body">
					<div class="reflectionInstructions">
						<lams:out value="${sessionMap.reflectInstructions}" escapeHtml="true" />
					</div>

					<c:choose>
					<c:when test="${empty sessionMap.reflectEntry}">
						<p>
							<em> 
								<fmt:message key="message.no.reflection.available" />
							</em>
						</p>
					</c:when>
					<c:otherwise>
						<p>
							<lams:out escapeHtml="true" value="${sessionMap.reflectEntry}" />
						</p>
					</c:otherwise>
					</c:choose>

					<c:if test="${mode != 'teacher'}">
						<button name="ContinueButton" onclick="return continueReflect()" class="btn btn-sm btn-default voffset5">
						<fmt:message key="label.edit" />
						</button>
					</c:if>
				</div>
			</div>
		</c:if>
		<!-- End Reflection -->

		<c:if test="${mode != 'teacher'}">
			<div>
				<c:choose>
					<c:when test="${sessionMap.reflectOn && (not sessionMap.userFinished)}">
						<button name="FinishButton" id="finish-button"
								onclick="return continueReflect()" class="btn btn-default voffset5 pull-right">
							<fmt:message key="label.continue" />
						</button>
					</c:when>
					<c:otherwise>
						<a href="#nogo" name="FinishButton" id="finish-button"
								onclick="return finishSession()" class="btn btn-primary voffset5 pull-right na">
							<span class="nextActivity">
								<c:choose>
				 					<c:when test="${sessionMap.isLastActivity}">
				 						<fmt:message key="label.submit" />
				 					</c:when>
				 					<c:otherwise>
				 		 				<fmt:message key="label.finished" />
				 					</c:otherwise>
				 				</c:choose>
							</span>
						</a>
					</c:otherwise>
				</c:choose>
			</div>
		</c:if>

	<!--closes content-->

	<div id="footer">
	</div>
	<!--closes footer-->

	</lams:Page>
</body>
</lams:html>
