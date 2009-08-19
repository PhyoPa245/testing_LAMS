/****************************************************************
 * Copyright (C) 2006 LAMS Foundation (http://lamsfoundation.org)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */
package org.lamsfoundation.lams.admin.web.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.lamsfoundation.lams.admin.service.AdminServiceProxy;
import org.lamsfoundation.lams.admin.web.form.ThemeForm;
import org.lamsfoundation.lams.config.ConfigurationItem;
import org.lamsfoundation.lams.themes.CSSThemeVisualElement;
import org.lamsfoundation.lams.themes.service.IThemeService;
import org.lamsfoundation.lams.usermanagement.Role;
import org.lamsfoundation.lams.util.CSSThemeUtil;
import org.lamsfoundation.lams.util.Configuration;
import org.lamsfoundation.lams.util.ConfigurationKeys;
import org.lamsfoundation.lams.util.MessageService;
import org.lamsfoundation.lams.web.action.LamsDispatchAction;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Luke Foxton
 * 
 *         Actions for maintaining and altering system themes
 * 
 * @struts.action path="/themeManagement" parameter="method" name="themeForm"
 *                input=".themeManagement" scope="request" validate="false"
 * @struts.action-forward name="success" path=".themeManagement"
 * @struts.action-forward name="error" path=".error"
 */
public class ThemeManagementAction extends LamsDispatchAction {

    private static IThemeService themeService;
    private static Configuration configurationService;

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {

	// check permission
	if (!request.isUserInRole(Role.SYSADMIN)) {
	    request.setAttribute("errorName", "RegisterAction");
	    request.setAttribute("errorMessage", AdminServiceProxy.getMessageService(getServlet().getServletContext())
		    .getMessage("error.authorisation"));
	    return mapping.findForward("error");
	}

	if (themeService == null) {
	    themeService = AdminServiceProxy.getThemeService(getServlet().getServletContext());
	}

	List<CSSThemeVisualElement> themes = themeService.getAllThemes();

	String currentTheme = Configuration.get(ConfigurationKeys.DEFAULT_HTML_THEME);
	for (CSSThemeVisualElement theme : themes) {
	    theme.setCurrentDefaultTheme(Boolean.FALSE);
	    if (theme.getName().equals(currentTheme)) {
		theme.setCurrentDefaultTheme(Boolean.TRUE);
	    }

	    theme.setNotEditable(Boolean.FALSE);
	    if (theme.getName().equals(CSSThemeUtil.DEFAULT_HTML_THEME)) {
		theme.setNotEditable(Boolean.TRUE);
	    }
	}

	request.setAttribute("themes", themes);
	return mapping.findForward("success");
    }

    public ActionForward addOrEditTheme(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	ThemeForm themeForm = (ThemeForm) form;

	CSSThemeVisualElement theme = null;

	if (themeForm.getId() != null && themeForm.getId() != 0) {
	    theme = themeService.getTheme(themeForm.getId());
	} else {
	    theme = new CSSThemeVisualElement();
	}
	updateThemeFromForm(theme, themeForm);

	themeService.saveOrUpdateTheme(theme);

	if (themeForm.getCurrentDefaultTheme() != null && themeForm.getCurrentDefaultTheme() == true) {
	    Configuration.updateItem(ConfigurationKeys.DEFAULT_HTML_THEME, themeForm.getName());
	    getConfiguration().persistUpdate();
	} else {
	    String currentTheme = Configuration.get(ConfigurationKeys.DEFAULT_HTML_THEME);
	    if (themeForm.getName().equals(currentTheme)) {
		Configuration.updateItem(ConfigurationKeys.DEFAULT_HTML_THEME, CSSThemeUtil.DEFAULT_HTML_THEME);
		getConfiguration().persistUpdate();
	    }
	}
	themeForm.clear();
	return unspecified(mapping, themeForm, request, response);
    }

    public ActionForward removeTheme(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	ThemeForm themeForm = (ThemeForm) form;
	if (themeForm.getId() != null) {
	    themeService.removeTheme(themeForm.getId());
	}

	String currentTheme = Configuration.get(ConfigurationKeys.DEFAULT_HTML_THEME);
	if (themeForm.getName().equals(currentTheme)) {
	    Configuration.updateItem(ConfigurationKeys.DEFAULT_HTML_THEME, CSSThemeUtil.DEFAULT_HTML_THEME);
	    getConfiguration().persistUpdate();
	}

	themeForm.clear();
	return unspecified(mapping, themeForm, request, response);
    }

    public ActionForward setAsDefault(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	ThemeForm themeForm = (ThemeForm) form;
	if (themeForm.getName() != null) {
	    Configuration.updateItem(ConfigurationKeys.DEFAULT_HTML_THEME, themeForm.getName());
	    getConfiguration().persistUpdate();
	}
	themeForm.clear();
	return unspecified(mapping, themeForm, request, response);
    }

    private CSSThemeVisualElement updateThemeFromForm(CSSThemeVisualElement theme, ThemeForm form) {
	theme.setName(form.getName());
	theme.setDescription(form.getDescription());
	theme.setImageDirectory(form.getImageDirectory());
	theme.setTheme(Boolean.TRUE);
	return theme;
    }

    private Configuration getConfiguration() {
	if (configurationService == null) {
	    WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServlet()
		    .getServletContext());
	    configurationService = (Configuration) ctx.getBean("configurationService");

	}
	return configurationService;
    }
}
