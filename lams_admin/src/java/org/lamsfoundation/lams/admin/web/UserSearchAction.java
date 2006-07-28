/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2.0
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

/* $Id$ */
package org.lamsfoundation.lams.admin.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.DynaActionForm;
import org.lamsfoundation.lams.usermanagement.Role;
import org.lamsfoundation.lams.usermanagement.User;
import org.lamsfoundation.lams.usermanagement.service.IUserManagementService;
import org.lamsfoundation.lams.util.WebUtil;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author jliew
 *
 */

/**
 * struts doclets
 * 
 * @struts:action path="/usersearch"
 *                name="UserSearchForm"
 *                input=".usersearchlist"
 *                scope="request"
 *                validate="false"
 *
 * @struts:action-forward name="usersearchlist"
 *                        path=".usersearchlist"
 */
public class UserSearchAction extends Action {
	
	private static Logger log = Logger.getLogger(UserSearchAction.class);
	private static IUserManagementService service;

	public ActionForward execute(ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
		
		if(!request.isUserInRole(Role.SYSADMIN)){
			log.debug("user not in role sysadmin");
			ActionMessages errors = new ActionMessages();
			errors.add("authorisation",new ActionMessage("error.authorisation"));
			saveErrors(request,errors);
			request.setAttribute("isSysadmin",false);
			return mapping.findForward("usersearchlist");
		}

		DynaActionForm userSearchForm = (DynaActionForm)form;
		
		String userId = ((String)userSearchForm.get("userId")).trim();
		String login = ((String)userSearchForm.get("login")).trim();
		String firstName = ((String)userSearchForm.get("firstName")).trim();
		String lastName = ((String)userSearchForm.get("lastName")).trim();
		
		log.debug("got userId: "+userId);
		log.debug("got login: "+login);
		log.debug("got firstName: "+firstName);
		log.debug("got lastName: "+lastName);
		
		List userList = new ArrayList();
		if(userId=="") {
			Map<String, String> stringProperties = new HashMap<String,String>();
			if(login!="") stringProperties.put("login","%"+login+"%");
			if(firstName!="") stringProperties.put("firstName","%"+firstName+"%");
			if(lastName!="") stringProperties.put("lastName","%"+lastName+"%");
			if(!stringProperties.isEmpty()) userList = getService().searchByStringProperties(User.class,stringProperties);
		}else{
			Map<String, Object> objectProperties = new HashMap<String,Object>();
			objectProperties.put("userId",userId);
			if(login!="") objectProperties.put("login",login);
			if(firstName!="") objectProperties.put("firstName",firstName);
			if(lastName!="") objectProperties.put("lastName",lastName);
			if(!objectProperties.isEmpty()) userList = getService().findByProperties(User.class,objectProperties);
		}
		
		if(userList.isEmpty() && (Boolean)userSearchForm.get("searched")){
			ActionMessages messages = new ActionMessages();
			messages.add("results",new ActionMessage("error.results.none"));
			saveMessages(request,messages);
		}
		
		userSearchForm.set("searched", true);
		request.setAttribute("userList",userList);
		return mapping.findForward("usersearchlist");
	}
	
	private IUserManagementService getService(){
		if(service==null){
			WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet().getServletContext());
			service = (IUserManagementService) ctx.getBean("userManagementServiceTarget");
		}
		return service;
	}
	
}
