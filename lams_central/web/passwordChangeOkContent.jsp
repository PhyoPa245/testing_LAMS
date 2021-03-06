<!DOCTYPE html>

<%@ page contentType="text/html; charset=utf-8" language="java"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %> 
<%@ taglib uri="tags-core" prefix="c"%>
<%@ taglib uri="tags-fmt" prefix="fmt"%>
<%@ taglib uri="tags-lams" prefix="lams"%>

<lams:html>
<lams:head>
	<lams:css/>
</lams:head>

<body>
	<div style="clear: both"></div>

	<div class="container">
		<div class="row vertical-center-row">
			<div class="col-xs-12 col-sm-8 col-sm-offset-2 col-md-6 col-md-offset-3">
				<div class="card voffset20">
					<div class="card-body">
					<div class="col-xs-12 text-center">
						<fmt:message key="msg.password.changed"/>
					</div>
					
					<div class="col-xs-12 text-center voffset10">
						<a class="btn btn-sm btn-default"
								href="<lams:LAMSURL/>${empty redirectURL ? 'index.do' : redirectURL}" role="button">
							Ok
						</a>
					</div>
					
					</div>
				</div>
			</div>
		</div>
	</div>

</body>
</lams:html>