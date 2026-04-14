package com.quiz.data;

import com.quiz.exception.InvalidQuestionFormatException;
import com.quiz.model.Question;
import com.quiz.util.Constants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reads questions from data/questions.txt and returns a shuffled subset.
 *
 * File format — each line must have exactly 8 pipe-delimited fields:
 *   id|category|questionText|optionA|optionB|optionC|optionD|correctOption
 *
 * Lines starting with '#' are treated as comments and skipped.
 * Blank lines are also skipped.
 */
public class QuestionLoader {

    private static final int EXPECTED_FIELDS = 8;
    private static final String DELIMITER     = "\\|";

    /**
     * Loads all valid questions from the configured file, shuffles them,
     * and returns up to {@link Constants#DEFAULT_QUESTION_COUNT} questions.
     *
     * @return an unmodifiable list of questions
     * @throws IOException if the file cannot be read
     * @throws InvalidQuestionFormatException if any line has wrong field count
     */
    public List<Question> loadQuestions() throws IOException {
        List<Question> all = readAll();
        Collections.shuffle(all);
        int count = Math.min(all.size(), Constants.DEFAULT_QUESTION_COUNT);
        return Collections.unmodifiableList(all.subList(0, count));
    }

    /**
     * Loads ALL questions in file order — useful for admin/debug views.
     */
    public List<Question> loadAll() throws IOException {
        return Collections.unmodifiableList(readAll());
    }

    // ── Private helpers ────────────────────────────────────────────────────────

    private List<Question> readAll() throws IOException {
        List<Question> questions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(Constants.QUESTIONS_FILE))) {
            String line;
            int lineNum = 0;

            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();

                // Skip blank lines and comments
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(DELIMITER, -1);

                if (parts.length != EXPECTED_FIELDS) {
                    throw new InvalidQuestionFormatException(
                            "Line " + lineNum + " has " + parts.length +
                            " field(s), expected " + EXPECTED_FIELDS + ". Content: [" + line + "]"
                    );
                }

                try {
                    int    id            = Integer.parseInt(parts[0].trim());
                    String category      = parts[1].trim();
                    String questionText  = parts[2].trim();
                    String optionA       = parts[3].trim();
                    String optionB       = parts[4].trim();
                    String optionC       = parts[5].trim();
                    String optionD       = parts[6].trim();
                    String correctOption = parts[7].trim();

                    // Validate correct option
                    if (!correctOption.matches("[AaBbCcDd]")) {
                        throw new InvalidQuestionFormatException(
                                "Line " + lineNum + ": correctOption must be A/B/C/D, got '" + correctOption + "'"
                        );
                    }

                    questions.add(new Question(id, category, questionText,
                            optionA, optionB, optionC, optionD, correctOption));

                } catch (NumberFormatException e) {
                    throw new InvalidQuestionFormatException(
                            "Line " + lineNum + ": id must be an integer. " + e.getMessage()
                    );
                }
            }
        }

        if (questions.isEmpty()) {
            throw new InvalidQuestionFormatException("No questions found in " + Constants.QUESTIONS_FILE);
        }

        return questions;
    }
}
