package com.quiz.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores the complete outcome of one quiz session.
 * Built by ScoreCalculator after the session ends.
 */
public class Result {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Student  student;
    private final int      score;          // number of correct answers
    private final int      total;          // total questions attempted
    private final double   percentage;
    private final String   grade;
    private final LocalDateTime timestamp;

    /** Map of category → [correct, attempted] */
    private final Map<String, int[]> categoryStats;

    /** Per-question answer record: index matches the question list */
    private final List<String> selectedAnswers;   // null if skipped
    private final List<Question> questions;

    public Result(Student student, int score, int total, double percentage, String grade,
                  Map<String, int[]> categoryStats,
                  List<Question> questions, List<String> selectedAnswers) {
        this.student         = student;
        this.score           = score;
        this.total           = total;
        this.percentage      = percentage;
        this.grade           = grade;
        this.categoryStats   = Collections.unmodifiableMap(new HashMap<>(categoryStats));
        this.questions       = Collections.unmodifiableList(questions);
        this.selectedAnswers = Collections.unmodifiableList(selectedAnswers);
        this.timestamp       = LocalDateTime.now();
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public Student  getStudent()         { return student; }
    public int      getScore()           { return score; }
    public int      getTotal()           { return total; }
    public int      getTotalQuestions()   { return total; }   // alias for GUI convenience
    public double   getPercentage()      { return percentage; }
    public String   getGrade()           { return grade; }
    public LocalDateTime getTimestamp()  { return timestamp; }
    public Map<String, int[]> getCategoryStats()  { return categoryStats; }
    public List<Question> getQuestions()          { return questions; }
    public List<String>   getSelectedAnswers()    { return selectedAnswers; }

    public String getFormattedTimestamp() {
        return timestamp.format(FORMATTER);
    }

    /**
     * Returns a CSV row ready to append to results.csv.
     * Format: Timestamp,Name,RollNumber,Score,Total,Percentage,Grade
     */
    public String toCsvRow() {
        return String.join(",",
                getFormattedTimestamp(),
                student.getName(),
                student.getRollNumber(),
                String.valueOf(score),
                String.valueOf(total),
                String.format("%.2f", percentage),
                grade
        );
    }
}
