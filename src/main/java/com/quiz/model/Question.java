package com.quiz.model;

/**
 * Represents a single quiz question loaded from questions.txt.
 * File format (pipe-delimited, 8 fields):
 *   id|category|questionText|optionA|optionB|optionC|optionD|correctOption
 *   correctOption is one of: A, B, C, D
 */
public class Question {

    private final int    id;
    private final String category;
    private final String questionText;
    private final String optionA;
    private final String optionB;
    private final String optionC;
    private final String optionD;
    private final String correctOption;   // "A", "B", "C", or "D"

    public Question(int id, String category, String questionText,
                    String optionA, String optionB, String optionC, String optionD,
                    String correctOption) {
        this.id            = id;
        this.category      = category;
        this.questionText  = questionText;
        this.optionA       = optionA;
        this.optionB       = optionB;
        this.optionC       = optionC;
        this.optionD       = optionD;
        this.correctOption = correctOption.toUpperCase().trim();
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public int    getId()            { return id; }
    public String getCategory()      { return category; }
    public String getQuestionText()  { return questionText; }
    public String getOptionA()       { return optionA; }
    public String getOptionB()       { return optionB; }
    public String getOptionC()       { return optionC; }
    public String getOptionD()       { return optionD; }
    public String getCorrectOption() { return correctOption; }

    /**
     * Returns an array of all four option strings in order [A, B, C, D].
     */
    public String[] getAllOptions() {
        return new String[]{ optionA, optionB, optionC, optionD };
    }

    /**
     * Checks whether the supplied answer letter matches the correct option.
     * @param selectedOption "A", "B", "C", or "D" (case-insensitive)
     * @return true if correct
     */
    public boolean isCorrect(String selectedOption) {
        if (selectedOption == null) return false;
        return correctOption.equalsIgnoreCase(selectedOption.trim());
    }

    /**
     * Returns the full text of the correct answer option.
     */
    public String getCorrectAnswerText() {
        switch (correctOption) {
            case "A": return optionA;
            case "B": return optionB;
            case "C": return optionC;
            case "D": return optionD;
            default:  return "Unknown";
        }
    }

    @Override
    public String toString() {
        return "Question{id=" + id + ", category='" + category + "', text='" + questionText + "'}";
    }
}
