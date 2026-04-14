package com.quiz.service;

import com.quiz.data.QuestionLoader;
import com.quiz.data.ResultManager;
import com.quiz.model.Feedback;
import com.quiz.model.Question;
import com.quiz.model.Result;
import com.quiz.model.Student;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates a full quiz session.
 *
 * Lifecycle:
 *   1. startSession(student)  — loads & shuffles questions
 *   2. getCurrentQuestion()   — returns question at current index
 *   3. answerCurrent(option)  — records answer, advances index
 *   4. hasMore()              — false when all questions answered
 *   5. finish()               — calculates Result, persists to CSV, returns Result
 */
public class QuizEngine {

    private final QuestionLoader    loader    = new QuestionLoader();
    private final ScoreCalculator   scorer    = new ScoreCalculator();
    private final FeedbackGenerator feedback  = new FeedbackGenerator();
    private final ResultManager     manager   = new ResultManager();

    private Student        student;
    private List<Question> questions;
    private List<String>   selectedAnswers;
    private int            currentIndex;
    private boolean        sessionActive;
    private boolean        isPracticeMode;

    // ── Session control ────────────────────────────────────────────────────────

    /**
     * Loads questions and initialises a new session.
     * Must be called before any other method.
     */
    public void startSession(Student student, boolean isPracticeMode) throws IOException {
        this.student         = student;
        this.isPracticeMode  = isPracticeMode;
        this.questions       = new ArrayList<>(loader.loadQuestions());
        this.selectedAnswers = new ArrayList<>();
        this.currentIndex    = 0;
        this.sessionActive   = true;

        // Pre-fill answers list with null (unanswered)
        for (int i = 0; i < questions.size(); i++) {
            selectedAnswers.add(null);
        }
    }

    /** @return the question at the current index */
    public Question getCurrentQuestion() {
        checkActive();
        return questions.get(currentIndex);
    }

    /** @return 0-based index of the current question */
    public int getCurrentIndex() { return currentIndex; }

    /** @return total number of questions in this session */
    public int getTotalQuestions() { return questions.size(); }

    /** @return true if there is at least one more unanswered question */
    public boolean hasMore() {
        return sessionActive && currentIndex < questions.size();
    }

    /**
     * Records the answer for the current question and moves to the next.
     * @param selectedOption "A"/"B"/"C"/"D", or null to skip
     */
    public void answerCurrent(String selectedOption) {
        checkActive();
        selectedAnswers.set(currentIndex, selectedOption);
        currentIndex++;
    }

    /**
     * Jump directly to a specific question (used for "Previous" navigation).
     */
    public void navigateTo(int index) {
        if (index >= 0 && index < questions.size()) {
            currentIndex = index;
        }
    }

    /**
     * Records an answer without advancing (used when user changes a previous answer).
     */
    public void setAnswer(int index, String option) {
        if (index >= 0 && index < questions.size()) {
            selectedAnswers.set(index, option);
        }
    }

    /** @return the stored answer for a given index, or null if unanswered */
    public String getAnswer(int index) {
        if (index < 0 || index >= selectedAnswers.size()) return null;
        return selectedAnswers.get(index);
    }

    /**
     * Ends the session, calculates the result, saves it to CSV, and returns it.
     * Also returns a Feedback object via {@link #getLastFeedback()}.
     */
    public Result finish() throws IOException {
        checkActive();
        sessionActive = false;

        Result result = scorer.calculate(student, questions, selectedAnswers);
        if (!isPracticeMode) {
            manager.save(result);
        }
        lastFeedback = feedback.generate(result);
        return result;
    }

    // ── Convenience ───────────────────────────────────────────────────────────

    private Feedback lastFeedback;

    /** Available after {@link #finish()} has been called. */
    public Feedback getLastFeedback() { return lastFeedback; }

    public List<Question> getQuestions()       { return questions; }
    public List<String>   getSelectedAnswers() { return selectedAnswers; }

    private void checkActive() {
        if (!sessionActive || questions == null) {
            throw new IllegalStateException("No active quiz session. Call startSession() first.");
        }
    }
}
