/****************************************************************
 * Copyright (C) 2005 LAMS Foundation (http://lamsfoundation.org)
 * =============================================================
 * License Information: http://lamsfoundation.org/licensing/lams/2.0/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation.
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

package org.lamsfoundation.lams.tool.scribe.web.controller;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.notebook.model.NotebookEntry;
import org.lamsfoundation.lams.notebook.service.CoreNotebookConstants;
import org.lamsfoundation.lams.tool.scribe.dto.ScribeDTO;
import org.lamsfoundation.lams.tool.scribe.dto.ScribeSessionDTO;
import org.lamsfoundation.lams.tool.scribe.dto.ScribeUserDTO;
import org.lamsfoundation.lams.tool.scribe.model.Scribe;
import org.lamsfoundation.lams.tool.scribe.model.ScribeSession;
import org.lamsfoundation.lams.tool.scribe.model.ScribeUser;
import org.lamsfoundation.lams.tool.scribe.service.IScribeService;
import org.lamsfoundation.lams.tool.scribe.util.ScribeConstants;
import org.lamsfoundation.lams.tool.scribe.util.ScribeUtils;
import org.lamsfoundation.lams.tool.scribe.web.forms.MonitoringForm;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.session.SessionManager;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author
 * @version
 *
 *
 *
 *
 *
 *
 *
 */
@Controller
@RequestMapping("/monitoring")
public class MonitoringController {

    private static Logger log = Logger.getLogger(MonitoringController.class);

    @Autowired
    @Qualifier("lascrbScribeService")
    private IScribeService scribeService;

    @RequestMapping("")
    public String unspecified(@ModelAttribute("monitoringForm") MonitoringForm monitoringForm,
	    HttpServletRequest request) {
	log.info("excuting monitoring action");

	Long toolContentID = new Long(WebUtil.readLongParam(request, AttributeNames.PARAM_TOOL_CONTENT_ID));

	String contentFolderID = WebUtil.readStrParam(request, AttributeNames.PARAM_CONTENT_FOLDER_ID);
	monitoringForm.setContentFolderID(contentFolderID);

	monitoringForm.setCurrentTab(WebUtil.readLongParam(request, AttributeNames.PARAM_CURRENT_TAB, true));

	Scribe scribe = scribeService.getScribeByContentId(toolContentID);

	ScribeDTO scribeDTO = setupScribeDTO(scribe);
	boolean isGroupedActivity = scribeService.isGroupedActivity(toolContentID);
	request.setAttribute("isGroupedActivity", isGroupedActivity);
	request.setAttribute("monitoringDTO", scribeDTO);
	request.setAttribute("contentFolderID", contentFolderID);

	return "pages/monitoring/monitoring";
    }

    @RequestMapping("/openNotebook")
    public String openNotebook(HttpServletRequest request) {

	Long uid = WebUtil.readLongParam(request, "uid", false);

	ScribeUser scribeUser = scribeService.getUserByUID(uid);
	NotebookEntry notebookEntry = scribeService.getEntry(scribeUser.getScribeSession().getSessionId(),
		CoreNotebookConstants.NOTEBOOK_TOOL, ScribeConstants.TOOL_SIGNATURE, scribeUser.getUserId().intValue());

	ScribeUserDTO scribeUserDTO = new ScribeUserDTO(scribeUser);
	scribeUserDTO.setNotebookEntry(notebookEntry.getEntry());

	request.setAttribute("scribeUserDTO", scribeUserDTO);

	return "pages/monitoring/notebook";
    }

    @RequestMapping("/appointScribe")
    public String appointScribe(@ModelAttribute("monitoringForm") MonitoringForm monitoringForm,
	    HttpServletRequest request) {

	ScribeSession session = scribeService.getSessionBySessionId(monitoringForm.getToolSessionID());
	ScribeUser user = scribeService.getUserByUID(monitoringForm.getAppointedScribeUID());

	session.setAppointedScribe(user);
	scribeService.saveOrUpdateScribeSession(session);

	ScribeDTO scribeDTO = setupScribeDTO(session.getScribe());
	boolean isGroupedActivity = scribeService.isGroupedActivity(session.getScribe().getToolContentId());
	request.setAttribute("isGroupedActivity", isGroupedActivity);
	request.setAttribute("monitoringDTO", scribeDTO);
	request.setAttribute("contentFolderID", monitoringForm.getContentFolderID());

	monitoringForm.setCurrentTab(WebUtil.readLongParam(request, AttributeNames.PARAM_CURRENT_TAB, true));

	return "pages/monitoring/monitoring";
    }

    @RequestMapping("/forceCompleteActivity")
    public String forceCompleteActivity(@ModelAttribute("monitoringForm") MonitoringForm monitoringForm,
	    HttpServletRequest request) throws IOException {

	ScribeSession session = scribeService.getSessionBySessionId(monitoringForm.getToolSessionID());
	session.setForceComplete(true);
	scribeService.saveOrUpdateScribeSession(session);

	ScribeDTO scribeDTO = setupScribeDTO(session.getScribe());
	request.setAttribute("monitoringDTO", scribeDTO);
	request.setAttribute("contentFolderID", monitoringForm.getContentFolderID());

	return "pages/monitoring/monitoring";
    }

    /* Private Methods */

    private ScribeDTO setupScribeDTO(Scribe scribe) {
	ScribeDTO scribeDTO = new ScribeDTO(scribe);

	for (Iterator sessIter = scribe.getScribeSessions().iterator(); sessIter.hasNext();) {
	    ScribeSession session = (ScribeSession) sessIter.next();

	    ScribeSessionDTO sessionDTO = new ScribeSessionDTO(session);
	    int numberOfVotes = 0;

	    for (Iterator userIter = session.getScribeUsers().iterator(); userIter.hasNext();) {
		ScribeUser user = (ScribeUser) userIter.next();
		ScribeUserDTO userDTO = new ScribeUserDTO(user);

		// get the notebook entry.
		NotebookEntry notebookEntry = scribeService.getEntry(session.getSessionId(),
			CoreNotebookConstants.NOTEBOOK_TOOL, ScribeConstants.TOOL_SIGNATURE,
			user.getUserId().intValue());
		if (notebookEntry != null) {
		    userDTO.finishedReflection = true;
		} else {
		    userDTO.finishedReflection = false;
		}

		if (user.isReportApproved()) {
		    numberOfVotes++;
		}

		sessionDTO.getUserDTOs().add(userDTO);
	    }
	    int numberOfLearners = session.getScribeUsers().size();

	    sessionDTO.setNumberOfVotes(numberOfVotes);
	    sessionDTO.setNumberOfLearners(numberOfLearners);
	    sessionDTO.setVotePercentage(ScribeUtils.calculateVotePercentage(numberOfVotes, numberOfLearners));
	    scribeDTO.getSessionDTOs().add(sessionDTO);
	}

	return scribeDTO;

    }

    private ScribeUser getCurrentUser(Long toolSessionID) {
	UserDTO user = (UserDTO) SessionManager.getSession().getAttribute(AttributeNames.USER);

	// attempt to retrieve user using userId and toolSessionID
	ScribeUser scribeUser = scribeService.getUserByUserIdAndSessionId(new Long(user.getUserID().intValue()),
		toolSessionID);

	if (scribeUser == null) {
	    ScribeSession scribeSession = scribeService.getSessionBySessionId(toolSessionID);
	    scribeUser = scribeService.createScribeUser(user, scribeSession);
	}

	return scribeUser;
    }
}
