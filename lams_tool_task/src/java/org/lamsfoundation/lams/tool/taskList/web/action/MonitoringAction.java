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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301
 * USA
 * 
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

/* $Id$ */
package org.lamsfoundation.lams.tool.taskList.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.tomcat.util.json.JSONArray;
import org.apache.tomcat.util.json.JSONException;
import org.apache.tomcat.util.json.JSONObject;
import org.lamsfoundation.lams.tool.taskList.TaskListConstants;
import org.lamsfoundation.lams.tool.taskList.dto.ItemSummary;
import org.lamsfoundation.lams.tool.taskList.dto.ReflectDTO;
import org.lamsfoundation.lams.tool.taskList.dto.SessionDTO;
import org.lamsfoundation.lams.tool.taskList.dto.TaskListUserDTO;
import org.lamsfoundation.lams.tool.taskList.model.TaskList;
import org.lamsfoundation.lams.tool.taskList.model.TaskListItem;
import org.lamsfoundation.lams.tool.taskList.model.TaskListUser;
import org.lamsfoundation.lams.tool.taskList.service.ITaskListService;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.util.Configuration;
import org.lamsfoundation.lams.util.ConfigurationKeys;
import org.lamsfoundation.lams.util.DateUtil;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.lamsfoundation.lams.web.util.SessionMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class MonitoringAction extends Action {
    public static Logger log = Logger.getLogger(MonitoringAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException, ServletException, JSONException {
	String param = mapping.getParameter();

	if (param.equals("summary")) {
	    return summary(mapping, form, request, response);
	}
	if (param.equals("itemSummary")) {
	    return itemSummary(mapping, form, request, response);
	}
	if (param.equals("getPagedUsers")) {
	    return getPagedUsers(mapping, form, request, response);
	}
	if (param.equals("getPagedUsersByItem")) {
	    return getPagedUsersByItem(mapping, form, request, response);
	}
	if (param.equals("setVerifiedByMonitor")) {
	    return setVerifiedByMonitor(mapping, form, request, response);
	}

	if (param.equals("setSubmissionDeadline")) {
	    return setSubmissionDeadline(mapping, form, request, response);
	}

	return mapping.findForward(TaskListConstants.ERROR);
    }

    private ActionForward summary(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {

	// initial Session Map
	SessionMap<String, Object> sessionMap = new SessionMap<String, Object>();
	request.getSession().setAttribute(sessionMap.getSessionID(), sessionMap);

	request.setAttribute(TaskListConstants.ATTR_SESSION_MAP_ID, sessionMap.getSessionID());
	request.setAttribute("initialTabId", WebUtil.readLongParam(request, AttributeNames.PARAM_CURRENT_TAB, true));
	Long contentId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID);
	request.setAttribute(AttributeNames.PARAM_TOOL_CONTENT_ID, contentId);

	ITaskListService service = getTaskListService();
	TaskList taskList = service.getTaskListByContentId(contentId);

	List<SessionDTO> sessionDtos = service.getSummary(contentId);

	// cache into sessionMap
	sessionMap.put(TaskListConstants.ATTR_SESSION_DTOS, sessionDtos);
	sessionMap.put(TaskListConstants.ATTR_MONITOR_VERIFICATION_REQUIRED, taskList.isMonitorVerificationRequired());
	sessionMap.put(TaskListConstants.PAGE_EDITABLE, taskList.isContentInUse());
	sessionMap.put(TaskListConstants.ATTR_TASKLIST, taskList);
	sessionMap.put(TaskListConstants.ATTR_TOOL_CONTENT_ID, contentId);
	sessionMap.put(AttributeNames.PARAM_CONTENT_FOLDER_ID,
		WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID));

	if (taskList.getSubmissionDeadline() != null) {
	    Date submissionDeadline = taskList.getSubmissionDeadline();
	    HttpSession ss = SessionManager.getSession();
	    UserDTO teacher = (UserDTO) ss.getAttribute(AttributeNames.USER);
	    TimeZone teacherTimeZone = teacher.getTimeZone();
	    Date tzSubmissionDeadline = DateUtil.convertToTimeZoneFromDefault(teacherTimeZone, submissionDeadline);
	    sessionMap.put(TaskListConstants.ATTR_SUBMISSION_DEADLINE, tzSubmissionDeadline.getTime());
	}
	
	// Create reflectList if reflection is enabled.
	if (taskList.isReflectOnActivity()) {
	    List<ReflectDTO> reflectList = service.getReflectList(taskList.getContentId());
	    // Add reflectList to sessionMap
	    sessionMap.put(TaskListConstants.ATTR_REFLECT_LIST, reflectList);
	}

	return mapping.findForward(TaskListConstants.SUCCESS);
    }

    private ActionForward itemSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	ITaskListService service = getTaskListService();

	Long contentId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID);
	Long taskListItemId = WebUtil.readLongParam(request, TaskListConstants.ATTR_TASK_LIST_ITEM_UID);
	ItemSummary ItemSummary = service.getItemSummary(contentId, taskListItemId, false);

	request.setAttribute(TaskListConstants.ATTR_ITEM_SUMMARY, ItemSummary);
	request.setAttribute(TaskListConstants.ATTR_IS_GROUPED_ACTIVITY, service.isGroupedActivity(contentId));
	return mapping.findForward(TaskListConstants.SUCCESS);
    }

    /**
     * Refreshes user list.
     */
    public ActionForward getPagedUsers(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse res) throws IOException, ServletException, JSONException {
	ITaskListService service = getTaskListService();
	String serverUrl = Configuration.get(ConfigurationKeys.SERVER_URL) + "/tool/" + TaskListConstants.TOOL_SIGNATURE + "/";
	
	String sessionMapID = request.getParameter(TaskListConstants.ATTR_SESSION_MAP_ID);
	SessionMap<String, Object> sessionMap = (SessionMap<String, Object>) request.getSession()
		.getAttribute(sessionMapID);
	TaskList tasklist = (TaskList) sessionMap.get(TaskListConstants.ATTR_TASKLIST);
	Long sessionId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_SESSION_ID);
	List<SessionDTO> sessionDtos = (List<SessionDTO>) sessionMap.get(TaskListConstants.ATTR_SESSION_DTOS);
	
	//find according sessionDto
	SessionDTO sessionDto = null;
	for (SessionDTO sessionDtoIter : sessionDtos) {
	    if (sessionDtoIter.getSessionId().equals(sessionId)) {
		sessionDto = sessionDtoIter;
	    }
	}
	List<TaskListItem> items = sessionDto.getTaskListItems();

	// Getting the params passed in from the jqGrid
	int page = WebUtil.readIntParam(request, AttributeNames.PARAM_PAGE);
	int rowLimit = WebUtil.readIntParam(request, AttributeNames.PARAM_ROWS);
	String sortOrder = WebUtil.readStrParam(request, AttributeNames.PARAM_SORD);
	String sortBy = WebUtil.readStrParam(request, AttributeNames.PARAM_SIDX, true);
	if (sortBy == "") {
	    sortBy = "userName";
	}
	String searchString = WebUtil.readStrParam(request, "userName", true);

	// Get the user list from the db
	Collection<TaskListUserDTO> userDtos = service.getPagedUsersBySession(sessionId, page - 1, rowLimit, sortBy,
		sortOrder, searchString);
	int countSessionUsers = service.getCountPagedUsersBySession(sessionId, searchString);

	int totalPages = new Double(
		Math.ceil(new Integer(countSessionUsers).doubleValue() / new Integer(rowLimit).doubleValue()))
			.intValue();

	JSONArray rows = new JSONArray();
	int i = 1;
	for (TaskListUserDTO userDto : userDtos) {

	    JSONArray userData = new JSONArray();
	    userData.put(userDto.getUserId());
//	    userData.put(sessionId);
	    String fullName = StringEscapeUtils.escapeHtml(userDto.getFullName());
	    userData.put(fullName);
	    
	    Set<Long> completedTaskUids = userDto.getCompletedTaskUids();
	    for (TaskListItem item : items) {
		String completionImage = completedTaskUids.contains(item.getUid())
			? "<img src='" + serverUrl + "/includes/images/completeitem.gif' border='0'>"
			: "<img src='" + serverUrl + "/includes/images/dash.gif' border='0'>";
		userData.put(completionImage);
	    }

	    if (tasklist.isMonitorVerificationRequired()) {
		String label = StringEscapeUtils.escapeHtml(service.getMessage("label.confirm"));

		String verificationStatus = userDto.isVerifiedByMonitor()
			? "<img src='" + serverUrl + "/includes/images/tick.gif' border='0'>"
			: "<a id='verif-" + userDto.getUserId()
				+ "' href='javascript:;' onclick='return setVerifiedByMonitor(this, "
				+ userDto.getUserId() + ");'>" + label + "</a>";
		userData.put(verificationStatus);
	    }

	    JSONObject userRow = new JSONObject();
	    userRow.put("id", i++);
	    userRow.put("cell", userData);

	    rows.put(userRow);
	}

	JSONObject responseJSON = new JSONObject();
	responseJSON.put("total", totalPages);
	responseJSON.put("page", page);
	responseJSON.put("records", countSessionUsers);
	responseJSON.put("rows", rows);

	res.setContentType("application/json;charset=utf-8");
	res.getWriter().print(new String(responseJSON.toString()));
	return null;
    }

    /**
     * Refreshes user list.
     */
    public ActionForward getPagedUsersByItem(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse res) throws IOException, ServletException, JSONException {
	ITaskListService service = getTaskListService();
	
	String sessionMapID = request.getParameter(TaskListConstants.ATTR_SESSION_MAP_ID);
	SessionMap<String, Object> sessionMap = (SessionMap<String, Object>) request.getSession()
		.getAttribute(sessionMapID);
	TaskList tasklist = (TaskList) sessionMap.get(TaskListConstants.ATTR_TASKLIST);
	Long sessionId = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_SESSION_ID);
	Long questionUid = WebUtil.readLongParam(request, "questionUid");

	// Getting the params passed in from the jqGrid
	int page = WebUtil.readIntParam(request, AttributeNames.PARAM_PAGE);
	int rowLimit = WebUtil.readIntParam(request, AttributeNames.PARAM_ROWS);
	String sortOrder = WebUtil.readStrParam(request, AttributeNames.PARAM_SORD);
	String sortBy = WebUtil.readStrParam(request, AttributeNames.PARAM_SIDX, true);
	if (sortBy == "") {
	    sortBy = "userName";
	}
	String searchString = WebUtil.readStrParam(request, "userName", true);

	// Get the user list from the db
	Collection<TaskListUserDTO> userDtos = service.getPagedUsersBySessionAndItem(sessionId, questionUid, page - 1,
		rowLimit, sortBy, sortOrder, searchString);
	int countSessionUsers = service.getCountPagedUsersBySession(sessionId, searchString);

	int totalPages = new Double(
		Math.ceil(new Integer(countSessionUsers).doubleValue() / new Integer(rowLimit).doubleValue()))
			.intValue();

	JSONArray rows = new JSONArray();
	int i = 1;
//	for (TaskListUserDTO userDto : userDtos) {
//
//	    Long questionResultUid = userDto.getQuestionResultUid();
//	    String fullName = StringEscapeUtils.escapeHtml(userDto.getFullName());
//
//	    JSONArray userData = new JSONArray();
//	    if (questionResultUid != null) {
//		AssessmentQuestionResult questionResult = service.getAssessmentQuestionResultByUid(questionResultUid);
//
//		userData.put(questionResultUid);
//		userData.put(questionResult.getMaxMark());
//		userData.put(fullName);
//		userData.put(AssessmentEscapeUtils.printResponsesForJqgrid(questionResult));
//		userData.put(questionResult.getMark());
//
//	    } else {
//		userData.put("");
//		userData.put("");
//		userData.put(fullName);
//		userData.put("-");
//		userData.put("-");
//	    }
//
//	    JSONObject userRow = new JSONObject();
//	    userRow.put("id", i++);
//	    userRow.put("cell", userData);
//
//	    rows.put(userRow);
//	}

	JSONObject responseJSON = new JSONObject();
	responseJSON.put("total", totalPages);
	responseJSON.put("page", page);
	responseJSON.put("records", countSessionUsers);
	responseJSON.put("rows", rows);

	res.setContentType("application/json;charset=utf-8");
	res.getWriter().print(new String(responseJSON.toString()));
	return null;
    }

    /**
     * Mark taskList user as verified.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException 
     */
    private ActionForward setVerifiedByMonitor(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws IOException {

	Long userUid = WebUtil.readLongParam(request, TaskListConstants.ATTR_USER_UID);
	ITaskListService service = getTaskListService();
	TaskListUser user = service.getUser(userUid);
	user.setVerifiedByMonitor(true);
	service.createUser(user); 
	
	response.setContentType("text/html");
	PrintWriter out = response.getWriter();
	out.write(userUid.toString());
	out.flush();
	out.close();
	return null;
    }

    /**
     * Set Submission Deadline
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    public ActionForward setSubmissionDeadline(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {

	ITaskListService service = getTaskListService();
	Long contentID = WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID);
	TaskList taskList = service.getTaskListByContentId(contentID);

	Long dateParameter = WebUtil.readLongParam(request, TaskListConstants.ATTR_SUBMISSION_DEADLINE, true);
	Date tzSubmissionDeadline = null;
	if (dateParameter != null) {
	    Date submissionDeadline = new Date(dateParameter);
	    HttpSession ss = SessionManager.getSession();
	    org.lamsfoundation.lams.usermanagement.dto.UserDTO teacher = (org.lamsfoundation.lams.usermanagement.dto.UserDTO) ss
		    .getAttribute(AttributeNames.USER);
	    TimeZone teacherTimeZone = teacher.getTimeZone();
	    tzSubmissionDeadline = DateUtil.convertFromTimeZoneToDefault(teacherTimeZone, submissionDeadline);
	}
	taskList.setSubmissionDeadline(tzSubmissionDeadline);
	service.saveOrUpdateTaskList(taskList);
	return null;
    }

    // *************************************************************************************
    // Private method
    // *************************************************************************************
    private ITaskListService getTaskListService() {
	WebApplicationContext wac = WebApplicationContextUtils
		.getRequiredWebApplicationContext(getServlet().getServletContext());
	return (ITaskListService) wac.getBean(TaskListConstants.RESOURCE_SERVICE);
    }

}
