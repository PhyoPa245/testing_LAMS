<!DOCTYPE html>

<%@ page contentType="text/html; charset=utf-8" language="java"%>
<%@ taglib uri="tags-lams" prefix="lams"%>
<%@ taglib uri="tags-fmt" prefix="fmt"%>
<%@ taglib uri="tags-core" prefix="c"%>
<%@ page import="org.lamsfoundation.lams.util.Configuration"%>
<%@ page import="org.lamsfoundation.lams.util.ConfigurationKeys"%>

<lams:html>
<lams:head>
	<title><fmt:message key="label.2FA.shared.secret" /></title>
	<lams:css/>
	<script type="text/javascript" src="includes/javascript/jquery.js"></script>
</lams:head>

<body>

    <!-- Fixed navbar -->
	<nav class="navbar navbar-default navbar-login">
		<div class="container">
			<div class="navbar-header">
				<a class="navbar-brand navbar-brand-login" href="#">
					<%=Configuration.get(ConfigurationKeys.SITE_NAME)%>
				</a>
			</div>
			<div class="navbar-collapse collapse navbar-right">
				<div class="pull-right login-logo" title="LAMS - Learning Activity Management System"></div>
			</div>
		</div>
	</nav>
	
	<div class="container">
		<div class="card card-plain center-block" style="max-width: 300px;">
			<div class="card-header text-center">
				<div class="card-title"> 
					<fmt:message key="label.2FA.shared.secret" />
				</div>
			</div>
			     			
			<div class="card-body text-center">
					<div class="input-group">
						<p>
							<fmt:message key="label.your.new.shared.secret">
								<fmt:param>${sharedSecret}</fmt:param>
							</fmt:message>
						</p>
						<p>
							<img alt="" src="${QRCode}">
						</p>
					</div>
		
					<div class="form-group voffset10">
						<div class="col-sm-12 controls">
							<input type="submit" class="btn btn-default pull-right" value="Ok"
								onClick="javascript:document.location='<lams:LAMSURL/>index.do';" tabindex="1"/>
						</div>	
					</div>
			</div>		
		</div>
	</div>

</body>
</lams:html>
