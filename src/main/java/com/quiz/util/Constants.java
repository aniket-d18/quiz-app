package com.quiz.util;

public class Constants {

    // Quiz session settings
    public static final int DEFAULT_QUESTION_COUNT = 20;
    public static final int QUIZ_DURATION_SECONDS  = 1800; // 30 minutes

    // Grade thresholds (percentage)
    public static final double GRADE_A_MIN = 90.0;
    public static final double GRADE_B_MIN = 75.0;
    public static final double GRADE_C_MIN = 60.0;
    public static final double GRADE_D_MIN = 40.0;
    // Below GRADE_D_MIN = F

    // File paths
    public static final String QUESTIONS_FILE = "data/questions.txt";
    public static final String RESULTS_FILE   = "data/results.csv";
    public static final String RESULTS_HEADER = "Timestamp,Name,RollNumber,Score,Total,Percentage,Grade";

    // UI settings
    public static final String APP_TITLE    = "Quiz Application";
    public static final int    WINDOW_WIDTH  = 900;
    public static final int    WINDOW_HEIGHT = 650;

    // Prevent instantiation
    private Constants() {}
}