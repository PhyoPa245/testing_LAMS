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
package org.lamsfoundation.lams.authoring.template.web;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.lamsfoundation.lams.authoring.template.AssessMCAnswer;
import org.lamsfoundation.lams.authoring.template.Assessment;
import org.lamsfoundation.lams.authoring.template.PeerReviewCriteria;
import org.lamsfoundation.lams.authoring.template.TemplateData;
import org.lamsfoundation.lams.rest.RestTags;
import org.lamsfoundation.lams.usermanagement.dto.UserDTO;
import org.lamsfoundation.lams.util.AuthoringJsonTags;
import org.lamsfoundation.lams.util.JsonUtil;
import org.lamsfoundation.lams.util.ValidationUtil;
import org.lamsfoundation.lams.util.WebUtil;
import org.lamsfoundation.lams.web.util.AttributeNames;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A Team Based Learning template. There are two versions - the full blown one that is accessed from authoring
 * and one that allows just sections of a TBL sequence to be created for the LAMS TBL system. Which bits are created
 * are controlled by a series of checkboxes (set to true and hidden in the full authoring version). The LAMS TBL 
 * version also has the option for timed gates, but the standard version uses permission gates.
 */
@Controller
@RequestMapping("authoring/template/tbl")
public class TBLTemplateController extends LdTemplateController {

    private static Logger log = Logger.getLogger(TBLTemplateController.class);
    private static String templateCode = "TBL";
    private static final DateFormat LESSON_SCHEDULING_DATETIME_FORMAT = new SimpleDateFormat("MM/dd/yy HH:mm");

    /**
     * Sets up the CKEditor stuff
     */
    @Override
    @RequestMapping("/init")
    public String init(HttpServletRequest request) throws Exception {
	request.setAttribute("questionNumber", "1");
	boolean isConfigurableVersion = WebUtil.readBooleanParam(request, "configure", false);
	String sessionMapID = WebUtil.readStrParam(request, "sessionMapID", true);
	if (sessionMapID != null) {
	    request.setAttribute("sessionMapID", sessionMapID);
	}
	if (isConfigurableVersion)
	    return super.init(request, "authoring/template/tbl/tbloptional");
	else
	    return super.init(request);
    }

    @Override
    protected ObjectNode createLearningDesign(HttpServletRequest request, HttpSession httpSession) throws Exception {
	TBLData data = new TBLData(request);
	if (data.getErrorMessages() != null && data.getErrorMessages().size() > 0) {
	    ObjectNode restRequestJSON = JsonNodeFactory.instance.objectNode();
	    restRequestJSON.set("errors", JsonUtil.readArray(data.getErrorMessages()));
	    return restRequestJSON;
	}

	UserDTO userDTO = (UserDTO) httpSession.getAttribute(AttributeNames.USER);
	Integer workspaceFolderID = workspaceManagementService.getUserWorkspaceFolder(userDTO.getUserID())
		.getResourceID().intValue();
	AtomicInteger maxUIID = new AtomicInteger();
	int order = 0;

	// create activities
	ArrayNode activities = JsonNodeFactory.instance.arrayNode();
	ArrayNode groupings = JsonNodeFactory.instance.arrayNode();

	Integer[] firstActivityInRowPosition = new Integer[] { 20, 125 }; // the very first activity, all other locations can be calculated from here if needed!
	String activityTitle = null;
	Integer[] currentActivityPosition = null;
	Integer groupingUIID = null;

	// Welcome
	if (data.useIntroduction) {
	    activityTitle = data.getText("boilerplate.introduction.title");
	    Long welcomeToolContentId = createNoticeboardToolContent(userDTO, activityTitle,
		    data.getText("boilerplate.introduction.instructions"), null);
	    activities.add(createNoticeboardActivity(maxUIID, order++, firstActivityInRowPosition, welcomeToolContentId,
		    data.contentFolderID, null, null, null, activityTitle));
	    currentActivityPosition = calcPositionNextRight(firstActivityInRowPosition);
	} else {
	    currentActivityPosition = firstActivityInRowPosition;
	}

	// Grouping
	activityTitle = data.getText("boilerplate.grouping.title");
	ObjectNode[] groupingJSONs = createGroupingActivity(maxUIID, order++, currentActivityPosition,
		data.groupingType, data.numLearners, data.numGroups, activityTitle, null, data.getUIBundle(),
		data.getFormatter());
	activities.add(groupingJSONs[0]);
	groupings.add(groupingJSONs[1]);
	groupingUIID = groupingJSONs[1].get("groupingUIID").asInt();

	if (data.useIRATRA) {
	    // Stop!
	    currentActivityPosition = calcPositionNextRight(currentActivityPosition);
	    activityTitle = data.getText("boilerplate.before.ira.gate");
	    if (data.useScheduledGates && data.iraStartOffset != null) {
		activities.add(createScheduledGateActivity(maxUIID, order++, calcGateOffset(currentActivityPosition),
			activityTitle, data.iraStartOffset));
	    } else {
		activities.add(
			createGateActivity(maxUIID, order++, calcGateOffset(currentActivityPosition), activityTitle));
	    }

	    // iRA Test - MCQ
	    currentActivityPosition = calcPositionNextRight(currentActivityPosition);
	    activityTitle = data.getText("boilerplate.ira.title");
	    Long iRAToolContentId = createMCQToolContent(userDTO, activityTitle,
		    data.getText("boilerplate.ira.instructions"), false, data.confidenceLevelEnable, false,
		    JsonUtil.readArray(data.testQuestions.values()));
	    ObjectNode iraActivityJSON = createMCQActivity(maxUIID, order++, currentActivityPosition, iRAToolContentId,
		    data.contentFolderID, groupingUIID, null, null, activityTitle);
	    activities.add(iraActivityJSON);

	    // Leader Selection
	    firstActivityInRowPosition = calcPositionBelow(firstActivityInRowPosition);
	    currentActivityPosition = firstActivityInRowPosition;
	    activityTitle = data.getText("boilerplate.leader.title");
	    Long leaderSelectionToolContentId = createLeaderSelectionToolContent(userDTO, activityTitle,
		    data.getText("boilerplate.leader.instructions"));
	    activities.add(createLeaderSelectionActivity(maxUIID, order++, currentActivityPosition,
		    leaderSelectionToolContentId, data.contentFolderID, groupingUIID, null, null, activityTitle));

	    // Stop!
	    currentActivityPosition = calcPositionNextRight(currentActivityPosition);
	    activityTitle = data.getText("boilerplate.before.tra.gate");
	    if (data.useScheduledGates && data.traStartOffset != null) {
		activities.add(createScheduledGateActivity(maxUIID, order++, calcGateOffset(currentActivityPosition),
			activityTitle, data.traStartOffset));
	    } else {
		activities.add(createGateActivity(maxUIID, order++, calcGateOffset(currentActivityPosition),
			activityTitle));
	    }

	    // tRA Test
	    currentActivityPosition = calcPositionNextRight(currentActivityPosition);
	    activityTitle = data.getText("boilerplate.tra.title");
		Integer confidenceLevelsActivityUIID = data.confidenceLevelEnable
			? JsonUtil.optInt(iraActivityJSON, AuthoringJsonTags.ACTIVITY_UIID)
			: null;
	    Long tRAToolContentId = createScratchieToolContent(userDTO, activityTitle,
		    data.getText("boilerplate.tra.instructions"), false,  confidenceLevelsActivityUIID,
		    JsonUtil.readArray(data.testQuestions.values()));
	    activities.add(createScratchieActivity(maxUIID, order++, currentActivityPosition, tRAToolContentId,
		    data.contentFolderID, groupingUIID, null, null, activityTitle));

	}

	// Application Exercises - multiple exercises with gates between each exercise. There may also be a grouped 
	// notebook after an exercise if indicated by the user. Each Application Exercise will have one or more questions.
	// Start a new row so that they group nicely together
	int displayOrder = 1;
	int noticeboardCount = 0;
	if (data.useApplicationExercises) {

	    for (AppExData applicationExercise : data.applicationExercises.values()) {
		
		// Start a new row for each application exercise and potentially the notebook.
		firstActivityInRowPosition = calcPositionBelow(firstActivityInRowPosition);
		currentActivityPosition = firstActivityInRowPosition;

		//  Gate before Application Exercise 
		String gateTitleBeforeAppEx = data.getText("boilerplate.before.app.ex", new String[] {applicationExercise.title});
		if (data.useScheduledGates && data.aeStartOffset != null) {
		    activities.add(createScheduledGateActivity(maxUIID, order++,
			    calcGateOffset(currentActivityPosition), gateTitleBeforeAppEx, data.aeStartOffset));
		} else {
		    activities.add(createGateActivity(maxUIID, order++, calcGateOffset(currentActivityPosition),
			    gateTitleBeforeAppEx));
		}

		// Application Exercise
		int assessmentNumber = 1;
		String applicationExerciseTitle = applicationExercise.title;
		currentActivityPosition = calcPositionNextRight(currentActivityPosition);
		ArrayNode questionsJSONArray = JsonNodeFactory.instance.arrayNode();
		for (Assessment exerciseQuestion : applicationExercise.assessments.values()) {
			questionsJSONArray.add(exerciseQuestion.getAsObjectNode(assessmentNumber));
			assessmentNumber++;
		}
		Long aetoolContentId = createAssessmentToolContent(userDTO, applicationExerciseTitle,
			data.getText("boilerplate.ae.instructions"), null, true, false, questionsJSONArray);
		activities.add(createAssessmentActivity(maxUIID, order++, currentActivityPosition, aetoolContentId,
			data.contentFolderID, groupingUIID, null, null, applicationExerciseTitle));

		// Optional Gate / Noticeboard. Don't add the extra gate in LAMS TBL or we will get too many scheduled gates to manage
		if (applicationExercise.useNoticeboard) {
		    noticeboardCount++;
		    String noticeboardCountAsString =  Integer.toString(noticeboardCount);

		    if (!data.useScheduledGates) {
			currentActivityPosition = calcPositionNextRight(currentActivityPosition);
			activities.add(createGateActivity(maxUIID, order++, calcGateOffset(currentActivityPosition),
				data.getText("boilerplate.before.app.ex.noticeboard", new String[] {noticeboardCountAsString} )));
		    }
		    currentActivityPosition = calcPositionNextRight(currentActivityPosition);
		    String notebookTitle = data.getText("boilerplate.grouped.ae.noticeboard.num",
				new String[] { noticeboardCountAsString });
		    Long aeNoticeboardContentId = createNoticeboardToolContent(userDTO, notebookTitle,
			    applicationExercise.noticeboardInstructions, null);
		    activities.add(createNoticeboardActivity(maxUIID, order++, currentActivityPosition, aeNoticeboardContentId,
			    data.contentFolderID, groupingUIID, null, null, notebookTitle));
		}

		displayOrder++;
	    }

	}

	firstActivityInRowPosition = calcPositionBelow(firstActivityInRowPosition);
	currentActivityPosition = firstActivityInRowPosition;

	if (data.usePeerReview) {
	    // Peer Review - optional. Start by eliminating all criterias with no title. Then if any are left 
	    // we create the gate before the Peer Review and the Peer Review tool data and activity
	    ArrayNode criterias = JsonNodeFactory.instance.arrayNode();
	    for (PeerReviewCriteria criteria : data.peerReviewCriteria.values()) {
		if (criteria.getTitle() != null && criteria.getTitle().length() > 0)
		    criterias.add(criteria.getAsObjectNode());
	    }
	    if (criterias.size() > 0) {
		// Stop!
		activities.add(createGateActivity(maxUIID, order++, calcGateOffset(currentActivityPosition),
			data.getText("boilerplate.before.peer.review")));

		currentActivityPosition = calcPositionNextRight(currentActivityPosition);
		String peerReviewTitle = data.getText("boilerplate.peerreview");
		Long prtoolContentId = createPeerReviewToolContent(userDTO, peerReviewTitle,
			data.getText("boilerplate.peerreview.instructions"), null, criterias);
		activities.add(createPeerReviewActivity(maxUIID, order++, currentActivityPosition, prtoolContentId,
			data.contentFolderID, groupingUIID, null, null, peerReviewTitle));
		displayOrder++;
		currentActivityPosition = calcPositionNextRight(currentActivityPosition);
	    }
	}

	if (data.useReflection) {
	    // Stop!
	    activities.add(createGateActivity(maxUIID, order++, calcGateOffset(currentActivityPosition), null));

	    // Individual Reflection
	    currentActivityPosition = calcPositionNextRight(currentActivityPosition);
	    activityTitle = data.getText("boilerplate.individual.reflection.title");
	    Long reflectionToolContentId = createNotebookToolContent(userDTO, activityTitle,
		    data.getText("boilerplate.individual.reflection.instructions"), false, false);
	    activities.add(createNotebookActivity(maxUIID, order++, currentActivityPosition, reflectionToolContentId,
		    data.contentFolderID, null, null, null, activityTitle));

	}

	// fill in required LD data and send it to the LearningDesignRestServlet
	ArrayNode transitions = createTransitions(maxUIID, activities);
	
	return saveLearningDesign(templateCode, data.sequenceTitle, "", workspaceFolderID, data.contentFolderID,
		maxUIID.get(), activities, transitions, groupings, null);

    }

    class AppExData {
	String title="Fix me"; 
	SortedMap<Integer, Assessment> assessments;
	boolean useNoticeboard = false;
	String noticeboardInstructions;
    }
    
    /**
     * TBLData contains all the data we need for the current instance. Created for each call to ensure that we have the
     * correct locale, messages, etc for this particular call.
     *
     * The complex types are converted straight to JSON objects ready for sending to the tools. No need to process
     * twice!
     *
     * For the test questions need to meet the spec: The questions entry should be ArrayNode containing JSON objects,
     * which in turn must contain "questionText", "displayOrder" (Integer) and a ArrayNode "answers". The options entry
     * should be ArrayNode containing JSON objects, which in turn must contain "answerText", "displayOrder" (Integer),
     * "correctOption" (Boolean).
     */
    class TBLData extends TemplateData {
	/* Fields from form */
	String contentFolderID;
	String sequenceTitle;
	Integer groupingType;
	Integer numLearners;
	Integer numGroups;

	Long iraGateStartOffset = null;
	Long traGateStartOffset = null;
	Long aeGateStartOffset = null;

	// used to configure which parts to use for the TBL system
	boolean useFirstGateBeforeGrouping = false;
	boolean useIntroduction = false;
	boolean useIRATRA = false;
	boolean useApplicationExercises = false;
	boolean usePeerReview = false;
	boolean useReflection = false;

	boolean useScheduledGates = false;
	Long startTime = null;
	Long iraStartOffset = null;
	Long traStartOffset = null;
	Long aeStartOffset = null;

	SortedMap<Integer, ObjectNode> testQuestions;
	boolean confidenceLevelEnable;
	SortedMap<Integer, AppExData> applicationExercises;
	SortedMap<Integer, PeerReviewCriteria> peerReviewCriteria;

	int questionOffset = 8;
	int assessmentOffset = 10;

	TBLData(HttpServletRequest request) {
	    super(request, templateCode);

	    // Debugging .....String name = (String) parameterNames.nextElement();
	    for ( Map.Entry<String, String[]>  paramEntry : request.getParameterMap().entrySet() ) {
		StringBuffer debugStr = new StringBuffer("Parameter name ").append(paramEntry.getKey()).append(" values ");
		for ( String value : paramEntry.getValue() ) {
		    debugStr.append(value).append(", ");
		}
		log.debug(debugStr.toString());
	    }

	    contentFolderID = getTrimmedString(request, "contentFolderID", false);

	    sequenceTitle = getTrimmedString(request, "sequenceTitle", false);

	    groupingType = WebUtil.readIntParam(request, "grouping");
	    numLearners = WebUtil.readIntParam(request, "numLearners", true);
	    numGroups = WebUtil.readIntParam(request, "numGroups", true);

	    testQuestions = new TreeMap<Integer, ObjectNode>();
	    applicationExercises = new TreeMap<Integer, AppExData>();
	    peerReviewCriteria = new TreeMap<Integer, PeerReviewCriteria>();

	    useIntroduction = WebUtil.readBooleanParam(request, "introduction", false);
	    useIRATRA = WebUtil.readBooleanParam(request, "iratra", false);
	    useApplicationExercises = WebUtil.readBooleanParam(request, "appex", false);
	    usePeerReview = WebUtil.readBooleanParam(request, "preview", false);
	    useReflection = WebUtil.readBooleanParam(request, "reflect", false);

	    String start = WebUtil.readStrParam(request, "startLogistic", true);
	    if ( start != null ) {
		// we are using the TBL version of LAMS to start the lessons, so we need to use schedule gates
		// and set the timings of the gates. Need to take into account if the lesson is started now or in the future.
		useScheduledGates = true;
		String dateTimeString = WebUtil.readStrParam(request, "lessonStartDatetime", true);
		if (start.equals("startNow") || dateTimeString == null || dateTimeString.length() == 0) {
		    startTime = System.currentTimeMillis();
		} else {
		    try {
			Date startDate = TBLTemplateController.LESSON_SCHEDULING_DATETIME_FORMAT.parse(dateTimeString);
			startTime = startDate.getTime();
		    } catch (ParseException e) {
			log.error("Unable to parse date from " + dateTimeString + ". Leaving start date as now.");
			startTime = System.currentTimeMillis();
		    }
		}
		iraStartOffset = getOffsetFromRequest(request, "iraLogistic", "iraScheduled", "iraStartDatetime");
		traStartOffset = getOffsetFromRequest(request, "traLogistic", "traScheduled", "traStartDatetime");
		aeStartOffset = getOffsetFromRequest(request, "aeLogistic", "aeScheduled", "aeStartDatetime");
	    }
	    
	    TreeMap<Integer, Integer> correctAnswers = new TreeMap<Integer, Integer>();
	    Enumeration parameterNames = request.getParameterNames();
	    while (parameterNames.hasMoreElements()) {
		String name = (String) parameterNames.nextElement();
		if (useIRATRA && name.startsWith("question")) {
		    int correctIndex = name.indexOf("correct");
		    if (correctIndex > 0) { // question1correct
			Integer questionDisplayOrder = Integer.valueOf(name.substring(questionOffset, correctIndex));
			Integer correctValue = WebUtil.readIntParam(request, name);
			correctAnswers.put(questionDisplayOrder, correctValue);
		    } else {
			int optionIndex = name.indexOf("option");
			if (optionIndex > 0) {
			    Integer questionDisplayOrder = Integer.valueOf(name.substring(questionOffset, optionIndex));
			    Integer optionDisplayOrder = Integer.valueOf(name.substring(optionIndex + 6));
			    processTestQuestion(name, null, null, questionDisplayOrder, optionDisplayOrder,
				    getTrimmedString(request, name, true));
			} else {
			    int titleIndex = name.indexOf("title");
			    if (titleIndex > 0) { // question1title
				Integer questionDisplayOrder = Integer.valueOf(name.substring(questionOffset, titleIndex));
				processTestQuestion(name, null, getTrimmedString(request, name, false),
					questionDisplayOrder, null, null);
			    } else {
				Integer questionDisplayOrder = Integer.valueOf(name.substring(questionOffset));
				processTestQuestion(name, getTrimmedString(request, name, true), null,
					questionDisplayOrder, null, null);
			    }
			}
		    }
		} else if (usePeerReview && name.startsWith("peerreview")) {
		    processInputPeerReviewRequestField(name, request);
		}
	    }

	    confidenceLevelEnable = WebUtil.readBooleanParam(request,  "confidenceLevelEnable", false);
	    if (useIRATRA) {
		if ( testQuestions.size() == 0 ) {
		    addValidationErrorMessage("authoring.error.rat.not.blank", null);
		}
		updateCorrectAnswers(correctAnswers);
	    }

	    if (useApplicationExercises) {
		processApplicationExercises(request);
	    }

	    validate();

	}

	private Long getOffsetFromRequest(HttpServletRequest request, String radioButtonField,
		String radioButtonScheduledString, String dateField) {
	    String radioValue = WebUtil.readStrParam(request, radioButtonField, true);
	    if (radioButtonScheduledString.equals(radioValue)) {
		String dateTimeString = WebUtil.readStrParam(request, dateField);
		if (dateTimeString != null) {
		    try {
			Long offset = TBLTemplateController.LESSON_SCHEDULING_DATETIME_FORMAT.parse(dateTimeString).getTime()
				- this.startTime;
			return offset > 0 ? offset / 60000 : 0; // from ms to minutes
		    } catch (ParseException e) {
			log.error("Unable to parse date from " + dateField
				+ ". Leaving gate as a permission gate to be opened manually.");
		    }
		}
	    }
	    return null;
	}

	// Assessment entries can't be deleted or rearranged so the count should always line up with numAppEx and numAssessments
	private void processApplicationExercises(HttpServletRequest request) {
	    int numAppEx = WebUtil.readIntParam(request, "numAppEx");
	    for (int i = 1; i <= numAppEx; i++) {
		String appexDiv = "divappex"+i;
		AppExData newAppex = new AppExData();
		newAppex.title = WebUtil.readStrParam(request,  appexDiv+"Title");
		newAppex.assessments = processAssessments(request, i, newAppex.title);
		newAppex.useNoticeboard = WebUtil.readBooleanParam(request,  appexDiv+"NB", false);
		if ( newAppex.useNoticeboard ) {
		    newAppex.noticeboardInstructions = getTrimmedString(request, appexDiv+"NBEntry", true);
		    if ( newAppex.noticeboardInstructions == null )
			addValidationErrorMessage(
				    "authoring.error.application.exercise.needs.noticeboard.text", new Object[] {"\"" + newAppex.title + "\""});
		}
		applicationExercises.put(i,  newAppex);
	    }
	}

	private SortedMap<Integer, Assessment> processAssessments(HttpServletRequest request, int appexNumber, String appexTitle) {
	    SortedMap<Integer, Assessment> applicationExercises = new TreeMap<Integer, Assessment>();
	    int numAssessments = WebUtil.readIntParam(request, "numAssessments" + appexNumber);
	    
	    for (int i = 1; i <= numAssessments; i++) {
		String assessmentPrefix = new StringBuilder("divass").append(appexNumber).append("assessment")
			.append(i).toString();
		String questionText = getTrimmedString(request, assessmentPrefix, true);
		String questionTitle = getTrimmedString(request, assessmentPrefix + "title", true);
		Assessment assessment = new Assessment();
		if (questionText != null) {
		    assessment.setTitle(questionTitle);
		    assessment.setQuestionText(questionText);
		    assessment.setType(WebUtil.readStrParam(request, assessmentPrefix + "type"));
		    assessment.setRequired(true);
		    if (assessment.getType() == Assessment.ASSESSMENT_QUESTION_TYPE_MULTIPLE_CHOICE) {
			String optionPrefix = new StringBuilder("divass").append(appexNumber).append("assmcq")
				.append(i).append("option").toString();
			for (int o = 1, order = 0; o <= MAX_OPTION_COUNT; o++) {
			    String answer = getTrimmedString(request, optionPrefix + o, true);
			    if (answer != null) {
				String grade = request.getParameter(optionPrefix + o + "grade");
				try {
				    assessment.getAnswers()
					    .add(new AssessMCAnswer(order++, answer, Float.valueOf(grade)));
				} catch (Exception e) {
				    log.error("Error parsing " + grade + " for float", e);
				    addValidationErrorMessage(
					    "authoring.error.application.exercise.not.blank.and.grade", new Object[] {"\"" + appexTitle + "\"", i});
				}
			    }
			}
		    }
		    applicationExercises.put(i, assessment);
		}
	    }
	    return applicationExercises;
	}

	void processInputPeerReviewRequestField(String name, HttpServletRequest request) {
	    log.debug("process peer review " + name + " order " + name.substring(10));
	    int fieldIndex = name.indexOf("EnableComments");
	    if (fieldIndex > 0) { // peerreview1EnableComments
		Integer criteriaNumber = Integer.valueOf(name.substring(10, fieldIndex));
		findCriteriaInMap(criteriaNumber).setCommentsEnabled(WebUtil.readBooleanParam(request, name, false));
	    } else {
		fieldIndex = name.indexOf("MinWordsLimit");
		if (fieldIndex > 0) {
		    Integer criteriaNumber = Integer.valueOf(name.substring(10, fieldIndex));
		    findCriteriaInMap(criteriaNumber)
			    .setCommentsMinWordsLimit(WebUtil.readIntParam(request, name, true));
		} else {
		    Integer criteriaNumber = Integer.valueOf(name.substring(10));
		    findCriteriaInMap(criteriaNumber).setTitle(getTrimmedString(request, name, true));
		}
	    }
	}

	private PeerReviewCriteria findCriteriaInMap(Integer criteriaNumber) {
	    PeerReviewCriteria criteria = peerReviewCriteria.get(criteriaNumber);
	    if (criteria == null) {
		criteria = new PeerReviewCriteria(criteriaNumber);
		peerReviewCriteria.put(criteriaNumber, criteria);
	    }
	    return criteria;
	}

	void processTestQuestion(String name, String questionText, String questionTitle, Integer questionDisplayOrder,
		Integer optionDisplayOrder, String optionText) {

	    ObjectNode question = testQuestions.get(questionDisplayOrder);
	    if (question == null) {
		question = JsonNodeFactory.instance.objectNode();
		question.set(RestTags.ANSWERS, JsonNodeFactory.instance.arrayNode());
		question.put(RestTags.DISPLAY_ORDER, questionDisplayOrder);
		testQuestions.put(questionDisplayOrder, question);
	    }

	    if (questionText != null) {
		question.put(RestTags.QUESTION_TEXT, questionText);
		// default title just in case - used for scratchie. Should be replaced with a user value
		if ( ! question.has(RestTags.QUESTION_TITLE) ) {
		    question.put(RestTags.QUESTION_TITLE, "Q" + questionDisplayOrder.toString()); 
		}
	    }
	    
	    if (questionTitle != null) {
		question.put(RestTags.QUESTION_TITLE, questionTitle);
	    }
	    
	    if (optionDisplayOrder != null && optionText != null) {
		ObjectNode newOption = JsonNodeFactory.instance.objectNode();
		newOption.put(RestTags.DISPLAY_ORDER, optionDisplayOrder);
		newOption.put(RestTags.CORRECT, false);
		newOption.put(RestTags.ANSWER_TEXT, optionText);
		((ArrayNode) question.get(RestTags.ANSWERS)).add(newOption);
	    }

	}

	void updateCorrectAnswers(TreeMap<Integer, Integer> correctAnswers) {
	    for (Entry<Integer, ObjectNode> entry : testQuestions.entrySet()) {
		Integer questionNumber = entry.getKey();
		ObjectNode question = entry.getValue();
		if (!question.has(RestTags.QUESTION_TEXT)) {
		    Object param = question.has(RestTags.QUESTION_TITLE) ? question.get(RestTags.QUESTION_TITLE) : questionNumber;
		    addValidationErrorMessage("authoring.error.question.num", new Object[] { param });
		}

		Integer correctAnswerDisplay = correctAnswers.get(entry.getKey());
		if (correctAnswerDisplay == null) {
		    Object param = question.has(RestTags.QUESTION_TITLE) ? question.get(RestTags.QUESTION_TITLE): questionNumber;
		    addValidationErrorMessage("authoring.error.question.correct.num", new Object[] { param });
		} else {

		    ArrayNode answers = (ArrayNode) question.get(RestTags.ANSWERS);
		    if (answers == null || answers.size() == 0) {
			Object param = question.has(RestTags.QUESTION_TITLE) ? question.get(RestTags.QUESTION_TITLE): questionNumber;
			addValidationErrorMessage("authoring.error.question.must.have.answer.num",
				new Object[] { param });
		    } else {
			boolean correctAnswerFound = false; // may not exist as the user didn't put any text in!
			for (int i = 0; i < answers.size(); i++) {
			    ObjectNode option = (ObjectNode) answers.get(i);
			    if (correctAnswerDisplay.equals(option.get(RestTags.DISPLAY_ORDER).asInt())) {
				option.remove(RestTags.CORRECT);
				option.put(RestTags.CORRECT, true);
				correctAnswerFound = true;
			    }
			}
			if (!correctAnswerFound) {
			    Object param = question.has(RestTags.QUESTION_TITLE) ? question.get(RestTags.QUESTION_TITLE): questionNumber;
			    addValidationErrorMessage("authoring.error.question.correct.num",
				    new Object[] { param });
			}
		    }
		}
	    }
	}

	// do any additional validation not included in other checking
	void validate() {
	    if (contentFolderID == null) {
		addValidationErrorMessage("authoring.error.content.id", null);
	    }
	    if (sequenceTitle == null || !ValidationUtil.isOrgNameValid(sequenceTitle)) {
		addValidationErrorMessage("authoring.fla.title.validation.error", null);
	    }

	    if (useApplicationExercises) {
		validateApplicationExercises();
	    }
	    // grouping is enforced on the front end by the layout & by requiring the groupingType
	    // questions are validated in updateCorrectAnswers
	}

	private void validateApplicationExercises() {
	    if (applicationExercises.size() == 0) {
	        addValidationErrorMessage("authoring.error.application.exercise.num", new Integer[] { 1 });
	    } else {
	        for (Map.Entry<Integer, AppExData> appExEntry : applicationExercises.entrySet()) {
	    	AppExData appEx = appExEntry.getValue();
	    	if (appEx.assessments == null || appEx.assessments.size() == 0) {
	    	    addValidationErrorMessage("authoring.error.application.exercise.num",
	    		    new String[] { "\""+appEx.title +"\""});
	    	} else {
	    	    for (Map.Entry<Integer, Assessment> assessmentEntry : appEx.assessments.entrySet()) {
	    		List<String> errors = assessmentEntry.getValue().validate(appBundle, formatter,
	    			appExEntry.getKey(), "\""+appEx.title +"\"", assessmentEntry.getKey());
	    		if (errors != null)
	    		    errorMessages.addAll(errors);
	    	    }
	    	}
	        }
	    }
	}

    }

    // Create the application exercise form on the screen. Not in LdTemplateController as this is specific to TBL
    // and hence the jsp is in the tbl template folder, not the tool template folder
    @RequestMapping("/createApplicationExercise")
    public String createApplicationExercise(HttpServletRequest request) {
	request.setAttribute("appexNumber", WebUtil.readIntParam(request, "appexNumber"));
	return "/authoring/template/tbl/appex";
    }


}
