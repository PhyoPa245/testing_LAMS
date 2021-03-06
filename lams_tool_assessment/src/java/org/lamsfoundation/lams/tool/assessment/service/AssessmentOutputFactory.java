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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 * USA
 *
 * http://www.gnu.org/licenses/gpl.txt
 * ****************************************************************
 */

package org.lamsfoundation.lams.tool.assessment.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.lamsfoundation.lams.learningdesign.BranchCondition;
import org.lamsfoundation.lams.tool.OutputFactory;
import org.lamsfoundation.lams.tool.ToolOutput;
import org.lamsfoundation.lams.tool.ToolOutputDefinition;
import org.lamsfoundation.lams.tool.assessment.AssessmentConstants;
import org.lamsfoundation.lams.tool.assessment.dto.AssessmentUserDTO;
import org.lamsfoundation.lams.tool.assessment.model.Assessment;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentOptionAnswer;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentQuestion;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentQuestionOption;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentQuestionResult;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentResult;
import org.lamsfoundation.lams.tool.assessment.model.AssessmentSession;
import org.lamsfoundation.lams.tool.assessment.model.QuestionReference;
import org.lamsfoundation.lams.tool.assessment.util.SequencableComparator;
import org.lamsfoundation.lams.util.WebUtil;

public class AssessmentOutputFactory extends OutputFactory {

    /**
     * @see org.lamsfoundation.lams.tool.OutputDefinitionFactory#getToolOutputDefinitions(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public SortedMap<String, ToolOutputDefinition> getToolOutputDefinitions(Object toolContentObject,
	    int definitionType) {

	TreeMap<String, ToolOutputDefinition> definitionMap = new TreeMap<String, ToolOutputDefinition>();

	ToolOutputDefinition definition = buildRangeDefinition(AssessmentConstants.OUTPUT_NAME_LEARNER_NUMBER_ATTEMPTS,
		0L, null);
	definitionMap.put(AssessmentConstants.OUTPUT_NAME_LEARNER_NUMBER_ATTEMPTS, definition);

	definition = buildRangeDefinition(AssessmentConstants.OUTPUT_NAME_LEARNER_TIME_TAKEN, 0L, null);
	definitionMap.put(AssessmentConstants.OUTPUT_NAME_LEARNER_TIME_TAKEN, definition);

	if (toolContentObject != null) {
	    Assessment assessment = (Assessment) toolContentObject;
	    Set<QuestionReference> questionReferences = new TreeSet<QuestionReference>(new SequencableComparator());
	    questionReferences.addAll(assessment.getQuestionReferences());

	    Long totalMarksPossible = 0L;
	    for (QuestionReference questionReference : questionReferences) {
		totalMarksPossible += questionReference.getDefaultGrade();
	    }
	    definition = buildRangeDefinition(AssessmentConstants.OUTPUT_NAME_LEARNER_TOTAL_SCORE, 0L,
		    totalMarksPossible, true);
	    definition.setWeightable(true);
	    definitionMap.put(AssessmentConstants.OUTPUT_NAME_LEARNER_TOTAL_SCORE, definition);

	    definition = buildRangeDefinition(AssessmentConstants.OUTPUT_NAME_BEST_SCORE, 0L, totalMarksPossible,
		    false);
	    definition.setWeightable(true);
	    definitionMap.put(AssessmentConstants.OUTPUT_NAME_BEST_SCORE, definition);

	    definition = buildRangeDefinition(AssessmentConstants.OUTPUT_NAME_FIRST_SCORE, 0L, totalMarksPossible,
		    false);
	    definition.setWeightable(true);
	    definitionMap.put(AssessmentConstants.OUTPUT_NAME_FIRST_SCORE, definition);

	    definition = buildRangeDefinition(AssessmentConstants.OUTPUT_NAME_AVERAGE_SCORE, 0L, totalMarksPossible,
		    false);
	    definition.setWeightable(true);
	    definitionMap.put(AssessmentConstants.OUTPUT_NAME_AVERAGE_SCORE, definition);

	    int randomQuestionsCount = 1;
	    for (QuestionReference questionReference : questionReferences) {
		Long markAvailable = null;
		if (questionReference.getDefaultGrade() != 0) {
		    markAvailable = Long.valueOf(questionReference.getDefaultGrade());
		}

		String description = getI18NText("output.user.score.for.question", false) + " ";
		if (questionReference.isRandomQuestion()) {
		    description += getI18NText("label.authoring.basic.type.random.question", false)
			    + randomQuestionsCount++;
		} else {
		    description += questionReference.getQuestion().getTitle();
		}

		definition = buildRangeDefinition(String.valueOf(questionReference.getSequenceId()), 0L, markAvailable);
		definition.setDescription(description);
		definitionMap.put(String.valueOf(questionReference.getSequenceId()), definition);
	    }

	    for (AssessmentQuestion question : assessment.getQuestions()) {
		if (question.getType() == AssessmentConstants.QUESTION_TYPE_ORDERING) {
		    String outputName = AssessmentConstants.OUTPUT_NAME_ORDERED_ANSWERS + "#"
			    + question.getSequenceId();
		    ToolOutputDefinition orderedAnswersDefinition = buildLongOutputDefinition(outputName);
		    orderedAnswersDefinition.setShowConditionNameOnly(true);
		    orderedAnswersDefinition.setDescription(
			    getI18NText("output.ordered.answers.for.question", false) + " " + question.getTitle());
		    List<BranchCondition> conditions = new LinkedList<BranchCondition>();
		    orderedAnswersDefinition.setConditions(conditions);
		    int orderId = 1;
		    for (AssessmentQuestionOption option : question.getOptions()) {
			conditions.add(new BranchCondition(null, null, orderId++,
				AssessmentConstants.OUTPUT_NAME_CONDITION_ORDERED_ANSWER + "#"
					+ question.getSequenceId() + "#" + option.getSequenceId(),
				WebUtil.removeHTMLtags(option.getOptionString()), BranchCondition.OUTPUT_TYPE_LONG,
				null, null, null));
		    }
		    definitionMap.put(outputName, orderedAnswersDefinition);
		}
	    }
	}

	return definitionMap;
    }

    public SortedMap<String, ToolOutput> getToolOutput(List<String> names, IAssessmentService assessmentService,
	    Long toolSessionId, Long learnerId) {

	TreeMap<String, ToolOutput> output = new TreeMap<String, ToolOutput>();

	AssessmentSession session = assessmentService.getSessionBySessionId(toolSessionId);
	if ((session != null) && (session.getAssessment() != null)) {
	    Assessment assessment = session.getAssessment();

	    if (names == null || names.contains(AssessmentConstants.OUTPUT_NAME_LEARNER_TOTAL_SCORE)) {
		output.put(AssessmentConstants.OUTPUT_NAME_LEARNER_TOTAL_SCORE,
			getLastTotalScore(assessmentService, learnerId, assessment));
	    }
	    if (names == null || names.contains(AssessmentConstants.OUTPUT_NAME_BEST_SCORE)) {
		output.put(AssessmentConstants.OUTPUT_NAME_BEST_SCORE,
			getBestTotalScore(assessmentService, toolSessionId, learnerId));
	    }
	    if (names == null || names.contains(AssessmentConstants.OUTPUT_NAME_FIRST_SCORE)) {
		output.put(AssessmentConstants.OUTPUT_NAME_FIRST_SCORE,
			getFirstTotalScore(assessmentService, toolSessionId, learnerId));
	    }
	    if (names == null || names.contains(AssessmentConstants.OUTPUT_NAME_AVERAGE_SCORE)) {
		output.put(AssessmentConstants.OUTPUT_NAME_AVERAGE_SCORE,
			getAverageTotalScore(assessmentService, toolSessionId, learnerId));
	    }
	    if (names == null || names.contains(AssessmentConstants.OUTPUT_NAME_LEARNER_TIME_TAKEN)) {
		output.put(AssessmentConstants.OUTPUT_NAME_LEARNER_TIME_TAKEN,
			getTimeTaken(assessmentService, learnerId, assessment));
	    }
	    if (names == null || names.contains(AssessmentConstants.OUTPUT_NAME_LEARNER_NUMBER_ATTEMPTS)) {
		output.put(AssessmentConstants.OUTPUT_NAME_LEARNER_NUMBER_ATTEMPTS,
			getNumberAttempts(assessmentService, learnerId, assessment));
	    }
	    if (names != null) {
		for (String name : names) {
		    if (name.startsWith(AssessmentConstants.OUTPUT_NAME_CONDITION_ORDERED_ANSWER)) {
			output.put(AssessmentConstants.OUTPUT_NAME_CONDITION_ORDERED_ANSWER,
				getAnswerOrder(assessmentService, assessment, learnerId, name));
		    }
		}
	    }
	    Set<AssessmentQuestion> questions = assessment.getQuestions();
	    for (AssessmentQuestion question : questions) {
		if (names == null || names.contains(String.valueOf(question.getSequenceId()))) {
		    output.put(AssessmentConstants.OUTPUT_NAME_LEARNER_NUMBER_ATTEMPTS,
			    getQuestionScore(assessmentService, learnerId, assessment, question.getSequenceId()));
		}
	    }
	}

	return output;
    }

    public ToolOutput getToolOutput(String name, IAssessmentService assessmentService, Long toolSessionId,
	    Long learnerId) {
	if (name != null) {
	    AssessmentSession session = assessmentService.getSessionBySessionId(toolSessionId);

	    if ((session != null) && (session.getAssessment() != null)) {
		Assessment assessment = session.getAssessment();

		if (name.equals(AssessmentConstants.OUTPUT_NAME_LEARNER_TOTAL_SCORE)) {
		    return getLastTotalScore(assessmentService, learnerId, assessment);

		} else if (name.equals(AssessmentConstants.OUTPUT_NAME_BEST_SCORE)) {
		    return getBestTotalScore(assessmentService, toolSessionId, learnerId);

		} else if (name.equals(AssessmentConstants.OUTPUT_NAME_FIRST_SCORE)) {
		    return getFirstTotalScore(assessmentService, toolSessionId, learnerId);

		} else if (name.equals(AssessmentConstants.OUTPUT_NAME_AVERAGE_SCORE)) {
		    return getAverageTotalScore(assessmentService, toolSessionId, learnerId);

		} else if (name.equals(AssessmentConstants.OUTPUT_NAME_LEARNER_TIME_TAKEN)) {
		    return getTimeTaken(assessmentService, learnerId, assessment);

		} else if (name.equals(AssessmentConstants.OUTPUT_NAME_LEARNER_NUMBER_ATTEMPTS)) {
		    return getNumberAttempts(assessmentService, learnerId, assessment);

		} else if (name.startsWith(AssessmentConstants.OUTPUT_NAME_CONDITION_ORDERED_ANSWER)) {
		    return getAnswerOrder(assessmentService, assessment, learnerId, name);
		} else {
		    Set<AssessmentQuestion> questions = assessment.getQuestions();
		    for (AssessmentQuestion question : questions) {
			if (name.equals(String.valueOf(question.getSequenceId()))) {
			    return getQuestionScore(assessmentService, learnerId, assessment, question.getSequenceId());
			}
		    }
		}
	    }
	}
	return null;
    }

    public List<ToolOutput> getToolOutputs(String name, IAssessmentService assessmentService, Long toolContentId) {
	if ((name != null) && (toolContentId != null)) {
	    if (name.equals(AssessmentConstants.OUTPUT_NAME_LEARNER_TOTAL_SCORE)) {
		List<AssessmentUserDTO> results = assessmentService.getLastTotalScoresByContentId(toolContentId);
		return convertToToolOutputs(results);
	    }
	    if (name.equals(AssessmentConstants.OUTPUT_NAME_BEST_SCORE)) {
		List<AssessmentUserDTO> results = assessmentService.getBestTotalScoresByContentId(toolContentId);
		return convertToToolOutputs(results);
	    }
	    if (name.equals(AssessmentConstants.OUTPUT_NAME_FIRST_SCORE)) {
		List<AssessmentUserDTO> results = assessmentService.getFirstTotalScoresByContentId(toolContentId);
		return convertToToolOutputs(results);
	    }
	    if (name.equals(AssessmentConstants.OUTPUT_NAME_AVERAGE_SCORE)) {
		List<AssessmentUserDTO> results = assessmentService.getAverageTotalScoresByContentId(toolContentId);
		return convertToToolOutputs(results);
	    }
	    if (name.equals(AssessmentConstants.OUTPUT_NAME_LEARNER_TIME_TAKEN)) {
		return null;
	    }
	    if (name.equals(AssessmentConstants.OUTPUT_NAME_LEARNER_NUMBER_ATTEMPTS)) {
		return null;
	    }
	    if (name.startsWith(AssessmentConstants.OUTPUT_NAME_ORDERED_ANSWERS)) {
		return null;
	    }
	    Assessment assessment = assessmentService.getAssessmentByContentId(toolContentId);
	    Set<AssessmentQuestion> questions = assessment.getQuestions();
	    for (AssessmentQuestion question : questions) {
		if (name.equals(String.valueOf(question.getSequenceId()))) {
		    return null;
		}
	    }
	}
	return null;
    }

    /**
     * Simply converts List<AssessmentUserDTO> to List<ToolOutput>.
     *
     * @param results
     * @return
     */
    private List<ToolOutput> convertToToolOutputs(List<AssessmentUserDTO> results) {
	List<ToolOutput> toolOutputs = new ArrayList<ToolOutput>();
	for (AssessmentUserDTO result : results) {
	    float totalScore = result.getGrade();

	    ToolOutput toolOutput = new ToolOutput(AssessmentConstants.OUTPUT_NAME_LEARNER_TOTAL_SCORE,
		    getI18NText(AssessmentConstants.OUTPUT_NAME_LEARNER_TOTAL_SCORE, true), totalScore);
	    toolOutput.setUserId(result.getUserId().intValue());
	    toolOutputs.add(toolOutput);
	}

	return toolOutputs;
    }

    /**
     * Get total score for a user. Will always return a ToolOutput object.
     */
    private ToolOutput getLastTotalScore(IAssessmentService assessmentService, Long learnerId, Assessment assessment) {
	Float assessmentResultGrade = assessmentService.getLastTotalScoreByUser(assessment.getUid(), learnerId);

	float totalScore = (assessmentResultGrade == null) ? 0 : assessmentResultGrade;

	return new ToolOutput(AssessmentConstants.OUTPUT_NAME_LEARNER_TOTAL_SCORE,
		getI18NText(AssessmentConstants.OUTPUT_NAME_LEARNER_TOTAL_SCORE, true), totalScore);
    }

    /**
     * Get the best score for a user. Will always return a ToolOutput object.
     */
    private ToolOutput getBestTotalScore(IAssessmentService assessmentService, Long sessionId, Long userId) {
	Float bestTotalScore = assessmentService.getBestTotalScoreByUser(sessionId, userId);
	float bestTotalScoreFloat = (bestTotalScore == null) ? 0 : bestTotalScore;

	return new ToolOutput(AssessmentConstants.OUTPUT_NAME_BEST_SCORE,
		getI18NText(AssessmentConstants.OUTPUT_NAME_BEST_SCORE, true), bestTotalScoreFloat);
    }

    /**
     * Get the first score for a user. Will always return a ToolOutput object.
     */
    private ToolOutput getFirstTotalScore(IAssessmentService assessmentService, Long sessionId, Long userId) {
	Float firstTotalScore = assessmentService.getFirstTotalScoreByUser(sessionId, userId);
	float firstTotalScoreFloat = (firstTotalScore == null) ? 0 : firstTotalScore;

	return new ToolOutput(AssessmentConstants.OUTPUT_NAME_FIRST_SCORE,
		getI18NText(AssessmentConstants.OUTPUT_NAME_FIRST_SCORE, true), firstTotalScoreFloat);
    }

    /**
     * Get the average score for a user. Will always return a ToolOutput object.
     */
    private ToolOutput getAverageTotalScore(IAssessmentService assessmentService, Long sessionId, Long userId) {
	Float averageTotalScore = assessmentService.getAvergeTotalScoreByUser(sessionId, userId);
	float averageTotalScoreFloat = (averageTotalScore == null) ? 0 : averageTotalScore;

	return new ToolOutput(AssessmentConstants.OUTPUT_NAME_AVERAGE_SCORE,
		getI18NText(AssessmentConstants.OUTPUT_NAME_AVERAGE_SCORE, true), averageTotalScoreFloat);
    }

    /**
     * Get time taken for a specific user to accomplish this assessment. Will always return a ToolOutput object.
     */
    private ToolOutput getTimeTaken(IAssessmentService assessmentService, Long learnerId, Assessment assessment) {
	Integer assessmentResultTimeTaken = assessmentService
		.getLastFinishedAssessmentResultTimeTaken(assessment.getUid(), learnerId);

	long timeTaken = (assessmentResultTimeTaken == null) ? 0 : assessmentResultTimeTaken;

	return new ToolOutput(AssessmentConstants.OUTPUT_NAME_LEARNER_TIME_TAKEN,
		getI18NText(AssessmentConstants.OUTPUT_NAME_LEARNER_TIME_TAKEN, true), timeTaken);
    }

    /**
     * Get the number of attempts done by user. Will always return a ToolOutput object.
     */
    private ToolOutput getNumberAttempts(IAssessmentService assessmentService, Long learnerId, Assessment assessment) {
	int numberAttempts = assessmentService.getAssessmentResultCount(assessment.getUid(), learnerId);

	return new ToolOutput(AssessmentConstants.OUTPUT_NAME_LEARNER_NUMBER_ATTEMPTS,
		getI18NText(AssessmentConstants.OUTPUT_NAME_LEARNER_NUMBER_ATTEMPTS, true), numberAttempts);
    }

    /**
     * Get user's score for the question. Will always return a ToolOutput object.
     */
    private ToolOutput getQuestionScore(IAssessmentService assessmentService, Long learnerId, Assessment assessment,
	    int questionSequenceId) {
	Float questionResultMarkDB = assessmentService.getQuestionResultMark(assessment.getUid(), learnerId,
		questionSequenceId);

	float questionResultMark = (questionResultMarkDB == null) ? 0 : questionResultMarkDB;
	return new ToolOutput(String.valueOf(questionSequenceId), "description", questionResultMark);
    }

    /**
     * Get order ID selected by the learner for the given option
     */
    private ToolOutput getAnswerOrder(IAssessmentService assessmentService, Assessment assessment, Long learnerId,
	    String conditionName) {
	// condition name is prefix#questionSequenceId#optionSequenceId
	String[] conditionNameSplit = conditionName.split("#");
	Integer questionSequenceId = Integer.valueOf(conditionNameSplit[1]);
	Integer optionSequenceId = Integer.valueOf(conditionNameSplit[2]);
	AssessmentQuestion question = null;
	// find question
	for (AssessmentQuestion questionCandidate : assessment.getQuestions()) {
	    if (questionSequenceId.equals(questionCandidate.getSequenceId())) {
		question = questionCandidate;
		break;
	    }
	}
	// find option
	Long optionUid = null;
	for (AssessmentQuestionOption optionCandidate : question.getOptions()) {
	    if (optionSequenceId.equals(optionCandidate.getSequenceId())) {
		optionUid = optionCandidate.getUid();
		break;
	    }
	}
	// find order in which the given learner put the option
	AssessmentResult result = assessmentService.getLastAssessmentResult(assessment.getUid(), learnerId);
	for (AssessmentQuestionResult questionResult : result.getQuestionResults()) {
	    if (questionResult.getAssessmentQuestion().getUid().equals(question.getUid())) {
		for (AssessmentOptionAnswer answer : questionResult.getOptionAnswers()) {
		    if (answer.getOptionUid().equals(optionUid)) {
			return new ToolOutput(conditionName, null, answer.getAnswerInt());
		    }
		}
	    }
	}
	return null;
    }

}
