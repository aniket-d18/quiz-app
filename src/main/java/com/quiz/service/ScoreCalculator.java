package com.quiz.service;

import com.quiz.model.Question;
import com.quiz.model.Result;
import com.quiz.model.Student;
import com.quiz.util.GradeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pure calculation — no side effects.
 * Takes the question list + selected answers and produces a Result.
 */
public class ScoreCalculator {

    /**
     * @param student         the student who took the quiz
     * @param questions       ordered list of questions shown
     * @param selectedAnswers parallel list of answers ("A"/"B"/"C"/"D" or null if skipped)
     * @return fully populated Result
     */
    public Result calculate(Student student,
                            List<Question> questions,
                            List<String>   selectedAnswers) {

        int score = 0;
        // category → [correct, attempted]
        Map<String, int[]> categoryStats = new HashMap<>();

        for (int i = 0; i < questions.size(); i++) {
            Question q      = questions.get(i);
            String   answer = (i < selectedAnswers.size()) ? selectedAnswers.get(i) : null;

            String cat = q.getCategory();
            categoryStats.putIfAbsent(cat, new int[]{0, 0});

            if (answer != null) {
                categoryStats.get(cat)[1]++;   // attempted
                if (q.isCorrect(answer)) {
                    score++;
                    categoryStats.get(cat)[0]++; // correct
                }
            }
        }

        int    total      = questions.size();
        double percentage = (total == 0) ? 0.0 : (score * 100.0 / total);
        String grade      = GradeUtil.getGrade(percentage);

        return new Result(student, score, total, percentage, grade,
                          categoryStats, questions, selectedAnswers);
    }
}
