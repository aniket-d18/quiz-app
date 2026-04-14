package com.quiz.service;

import com.quiz.model.Feedback;
import com.quiz.model.Result;
import com.quiz.util.GradeUtil;

/**
 * Generates a Feedback object from a completed Result.
 */
public class FeedbackGenerator {

    public Feedback generate(Result result) {
        String grade   = result.getGrade();
        String label   = GradeUtil.getLabel(grade);
        String message = GradeUtil.getFeedbackMessage(grade);
        return new Feedback(grade, label, message);
    }
}
