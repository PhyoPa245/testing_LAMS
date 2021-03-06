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

package org.lamsfoundation.lams.learning.web.controller;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.learning.service.ILearnerFullService;
import org.lamsfoundation.lams.learning.web.form.GroupingForm;
import org.lamsfoundation.lams.learning.web.util.ActivityMapping;
import org.lamsfoundation.lams.learning.web.util.LearningWebUtil;
import org.lamsfoundation.lams.learningdesign.Activity;
import org.lamsfoundation.lams.learningdesign.Group;
import org.lamsfoundation.lams.learningdesign.Grouping;
import org.lamsfoundation.lams.learningdesign.GroupingActivity;
import org.lamsfoundation.lams.learningdesign.LearnerChoiceGrouping;
import org.lamsfoundation.lams.learningdesign.dto.ActivityPositionDTO;
import org.lamsfoundation.lams.learningdesign.dto.GroupDTO;
import org.lamsfoundation.lams.lesson.LearnerProgress;
import org.lamsfoundation.lams.tool.ToolAccessMode;
import org.lamsfoundation.lams.usermanagement.dto.UserBasicDTO;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * <p>
 * The action servlet that triggers the system driven grouping (random grouping) and allows the learner to view the
 * result of the grouping.
 * </p>
 *
 * <p>
 * Has a special override key - if the parameter force is set and the lesson is a preview lesson, any chosen grouping
 * will be overridden.
 * </p>
 *
 * @author Jacky Fang
 *
 */
@Controller
@RequestMapping("/grouping")
public class GroupingController {
    private static Logger log = Logger.getLogger(GroupingController.class);

    @Autowired
    private ILearnerFullService learnerService;
    @Autowired
    private ActivityMapping activityMapping;
    
    // ---------------------------------------------------------------------
    // Class level constants - Session Attributes
    // ---------------------------------------------------------------------
    public static final String GROUPS = "groups";
    public static final String FINISHED_BUTTON = "finishedButton";
    public static final String LOCAL_FILES = "localFiles";
    public static final String TITLE = "title";
    public static final String MAX_LEARNERS_PER_GROUP = "maxLearnersPerGroup";
    public static final String VIEW_STUDENTS_BEFORE_SELECTION = "viewStudentsBeforeSelection";
    /** Input parameter. Boolean value */
    public static final String PARAM_FORCE_GROUPING = "force";

    /**
     * Perform the grouping for the users who are currently running the lesson. If force is set to true, then we should
     * be in preview mode, and we want to override the chosen grouping to make it group straight away.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    @RequestMapping("/performGrouping")
    public String performGrouping(@ModelAttribute GroupingForm groupingForm, HttpServletRequest request)
	    throws IOException, ServletException {

	boolean forceGroup = WebUtil.readBooleanParam(request, GroupingController.PARAM_FORCE_GROUPING, false);

	// initialize service object
	LearnerProgress learnerProgress = LearningWebUtil.getLearnerProgress(request, learnerService);

	long activityId = WebUtil.readLongParam(request, AttributeNames.PARAM_ACTIVITY_ID);
	Activity activity = learnerService.getActivity(activityId);
	if (!(activity instanceof GroupingActivity)) {
	    log.error("activity not GroupingActivity");
	    return "error";
	}
	Long lessonId = learnerProgress.getLesson().getLessonId();
	boolean groupingDone = learnerService.performGrouping(lessonId, activity.getActivityId(),
		LearningWebUtil.getUserId(), forceGroup);

	groupingForm.setPreviewLesson(learnerProgress.getLesson().isPreviewLesson());
	groupingForm.setTitle(activity.getTitle());
	groupingForm.setActivityID(activity.getActivityId());

	request.setAttribute(AttributeNames.PARAM_LESSON_ID, lessonId);
	if (groupingDone) {
	    request.setAttribute(GroupingController.FINISHED_BUTTON, Boolean.TRUE);
	    return viewGrouping(request, learnerProgress);
	}
	// forward to group choosing page
	if (((GroupingActivity) activity).getCreateGrouping().isLearnerChoiceGrouping()) {
	    Long groupingId = ((GroupingActivity) activity).getCreateGrouping().getGroupingId();
	    Integer maxNumberOfLeaernersPerGroup = learnerService.calculateMaxNumberOfLearnersPerGroup(lessonId,
		    groupingId);

	    LearnerChoiceGrouping grouping = (LearnerChoiceGrouping) learnerService.getGrouping(groupingId);
	    prepareGroupData(request);
	    request.setAttribute(GroupingController.MAX_LEARNERS_PER_GROUP, maxNumberOfLeaernersPerGroup);
	    request.setAttribute(GroupingController.LOCAL_FILES, Boolean.FALSE);
	    request.setAttribute(GroupingController.VIEW_STUDENTS_BEFORE_SELECTION,
		    grouping.getViewStudentsBeforeSelection());
	    return "grouping/choose";
	}
	return "grouping/wait";
    }

    /**
     * Load up the grouping information and forward to the jsp page to display all the groups and members.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
//    @RequestMapping("/viewGroup")
//    public String viewGrouping(HttpServletRequest request) throws IOException, ServletException {
//	return viewGrouping(request, null);
//    }

    @RequestMapping("/viewGroup")
    public String viewGrouping(HttpServletRequest request,
	    @RequestParam(required = false) LearnerProgress learnerProgress) throws IOException, ServletException {
	prepareGroupData(request);
	request.setAttribute(GroupingController.LOCAL_FILES, Boolean.FALSE);
	ToolAccessMode mode = WebUtil.readToolAccessModeParam(request, AttributeNames.PARAM_MODE, true);
	request.setAttribute(GroupingController.FINISHED_BUTTON, new Boolean((mode == null) || !mode.isTeacher()));

	long activityId = WebUtil.readLongParam(request, AttributeNames.PARAM_ACTIVITY_ID);
	//find activity position within Learning Design and store it as request attribute
	ActivityPositionDTO positionDTO = learnerService.getActivityPosition(activityId);
	if (positionDTO != null) {
	    request.setAttribute(AttributeNames.ATTR_ACTIVITY_POSITION, positionDTO);
	}

	// make sure the lesson id is always in the request for the progress bar.
	if (request.getAttribute(AttributeNames.PARAM_LESSON_ID) == null) {
	    if (learnerProgress == null) {
		learnerProgress = LearningWebUtil.getLearnerProgress(request, learnerService);
	    }
	    request.setAttribute(AttributeNames.PARAM_LESSON_ID, learnerProgress.getLesson().getLessonId());
	}
	return "grouping/show";
    }

    /**
     * Complete the current tool activity and forward to the url of next activity in the learning design.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    @RequestMapping("/completeActivity")
    public String completeActivity(HttpServletRequest request, HttpServletResponse response)
	    throws IOException, ServletException {
	// initialize service object
	LearnerProgress progress = LearningWebUtil.getLearnerProgress(request, learnerService);
	long activityId = WebUtil.readLongParam(request, AttributeNames.PARAM_ACTIVITY_ID);
	Activity groupingActivity = learnerService.getActivity(activityId);
	Integer learnerId = LearningWebUtil.getUserId();

	// so manually resume the progress. The completeActivity code can cope with a missing activity.
	return learnerService.completeActivity(progress, groupingActivity, learnerId, true);
    }

    /**
     * Inserts into the request most of the data required by JSP page. This method is common for several pages.
     *
     * @param request
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/prepareGroupData")
    private void prepareGroupData(HttpServletRequest request) {

	SortedSet<GroupDTO> groups = new TreeSet<>(GroupDTO.GROUP_NAME_COMPARATOR);
	long activityId = WebUtil.readLongParam(request, AttributeNames.PARAM_ACTIVITY_ID);
	Activity activity = learnerService.getActivity(activityId);

	Grouping grouping = ((GroupingActivity) activity).getCreateGrouping();
	if (grouping != null) {
	    for (Group group : grouping.getGroups()) {
		GroupDTO groupDTO = new GroupDTO(group, true);
		groupDTO.getUserList().sort(UserBasicDTO.USER_BASIC_DTO_COMPARATOR);
		groups.add(groupDTO);
	    }
	}

	request.setAttribute(GroupingController.GROUPS, groups);
	request.setAttribute(GroupingController.TITLE, activity.getTitle());
	request.setAttribute(AttributeNames.PARAM_ACTIVITY_ID, activity.getActivityId());
    }

    /**
     * Responds to a learner's group choice. Might forward back to group choice page if the chosen group was full.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws IOException
     * @throws ServletException
     */
    @RequestMapping("/learnerChooseGroup")
    public String learnerChooseGroup(@ModelAttribute GroupingForm groupingForm, HttpServletRequest request)
	    throws IOException, ServletException {
	long activityId = WebUtil.readLongParam(request, AttributeNames.PARAM_ACTIVITY_ID);
	Activity activity = learnerService.getActivity(activityId);
	Long groupId = WebUtil.readLongParam(request, "groupId");
	LearnerProgress learnerProgress = LearningWebUtil.getLearnerProgress(request, learnerService);
	Long lessonId = learnerProgress.getLesson().getLessonId();
	learnerService.learnerChooseGroup(lessonId, activity.getActivityId(), groupId, LearningWebUtil.getUserId());

	String redirectURL = "redirect:/grouping/performGrouping.do";
	redirectURL = WebUtil.appendParameterToURL(redirectURL, "activityID", activity.getActivityId().toString());

	return redirectURL;
    }

}