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

<!DOCTYPE html>

<%@ include file="/common/taglibs.jsp"%>

<lams:html>

<lams:head>
	<title><fmt:message key="label.view.groups.title" />
	</title>

	<lams:css />
	<c:set var="lams">
		<lams:LAMSURL />
	</c:set>

	<script type="text/javascript" src="${lams}includes/javascript/jquery.js"></script>
	<script type="text/javascript"
		src="${lams}includes/javascript/common.js"></script>
</lams:head>

<body class="stripes">
	<style type="text/css">
	.user-container {
		padding: 2px;
	}
	
	.you {
		font-weight: bolder;
	}
	</style>
	<lams:Page type="learner" title="${title}">
	
		<form:form
			action="/lams/learning/grouping/completeActivity.do?userId=${user.userID}&lessonId=${lessonID}&activityID=${activityID}"
			target="_self" modelAttribute="messageForm" id="messageForm">
	
			<c:set var="userId" value="${user.userID}" />
	
			<div class="card card-plain">
				<div class="card-header card-title">
					<i class="fa fa-sm fa-users text-primary"></i>&nbsp;
					<fmt:message key="label.view.groups.title" />
				</div>
				<div class="card-body">
	
					<div class="table-responsive">
						<table class="table table-sm table-hover" cellspacing="0">
							<c:forEach var="group" items="${groups}">
								<tr>
									<td width="15%"><strong><c:out value="${group.groupName}" /></strong></td>
									<td><c:forEach items="${group.userList}" var="user">
											<div name="u-${user.userID}" class="user-container">
												<lams:Portrait userId="${user.userID}"/>&nbsp;<c:out value="${user.firstName}" />&nbsp;<c:out value="${user.lastName}" />
											</div>
										</c:forEach></td>
								</tr>
							</c:forEach>
						</table>
					</div>
	
				</div>
			</div>
	
			<c:if test="${finishedButton}">
				<script type="text/javascript">
					function submitForm(methodName) {
						var f = document.getElementById('messageForm');
						f.submit();
					}
				</script>
	
				<div class="row no-gutter">
					<div class="col-xs-12">
						<div id="right-buttons" class="pull-right voffset10">
							<a href="javascript:;" class="btn btn btn-primary na" id="finishButton"
								onclick="submitForm('finish')">
								<span class="nextActivity"> <c:choose>
										<c:when test="${activityPosition.last}">
											<fmt:message key="label.submit.button" />
										</c:when>
										<c:otherwise>
											<fmt:message key="label.finish.button" />
										</c:otherwise>
									</c:choose>
								</span>
							</a>
						</div>
	
					</div>
				</div>
			</c:if>
	
	
			<div id="footer"></div>
			<!--closes footer-->
			<script type="text/javascript">
				function codeAddress() {
					var youUser = document.getElementsByName('u-${userId}');
					if (youUser) {
						youUser[0].className += ' alert-info you';
					}
				}
				window.onload = codeAddress;
			</script>
	
	
		</form:form>
	</lams:Page>
</body>

</lams:html>
