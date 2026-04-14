package com.quiz.util;

public class GradeUtil {

    public static String getGrade(double percentage) {
        if (percentage >= Constants.GRADE_A_MIN) return "A";
        if (percentage >= Constants.GRADE_B_MIN) return "B";
        if (percentage >= Constants.GRADE_C_MIN) return "C";
        if (percentage >= Constants.GRADE_D_MIN) return "D";
        return "F";
    }

    public static String getLabel(String grade) {
        switch (grade) {
            case "A": return "Excellent";
            case "B": return "Good";
            case "C": return "Average";
            case "D": return "Below Average";
            default:  return "Needs Improvement";
        }
    }

    public static String getFeedbackMessage(String grade) {
        switch (grade) {
            case "A": return "Outstanding! You have a strong command of the subject. Keep it up!";
            case "B": return "Well done! A few areas to revisit but overall a solid performance.";
            case "C": return "Decent effort. Focus on your weaker categories to improve further.";
            case "D": return "Keep practicing. Review the topics where you lost marks carefully.";
            default:  return "Don't be discouraged. Go through the material again and retry. You can do it!";
        }
    }

    // Prevent instantiation
    private GradeUtil() {}
}