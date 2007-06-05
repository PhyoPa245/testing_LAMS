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
<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>

<%@ taglib uri="tags-tiles" prefix="tiles" %>
<%@ taglib uri="tags-html" prefix="html" %>
<%@ taglib uri="tags-fmt" prefix="fmt" %>
<%@ taglib uri="tags-core" prefix="c" %>
<%@ taglib uri="tags-lams" prefix="lams" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
            "http://www.w3.org/TR/html4/loose.dtd">
<lams:html>
    <lams:head>
       
		<%-- if localFiles == true then wanted for export portfolio and must run offline --%>
		<c:choose>
		<c:when test="${not empty GateForm and GateForm.map.localFiles == true}">
			<lams:css localLinkPath="../"/>
		</c:when>
		<c:otherwise>
			<lams:css/>
		</c:otherwise>
		</c:choose>
       
       
	  <fmt:setBundle basename = "org.lamsfoundation.lams.monitoring.MonitoringResources" />
    </lams:head>
    
    <body class="stripes">
		<tiles:insert attribute="content" />

		<div id="footer">
		</div><!--closes footer-->

    </body>
</lams:html>
