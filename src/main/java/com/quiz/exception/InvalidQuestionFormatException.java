package com.quiz.exception;

public class InvalidQuestionFormatException extends RuntimeException {

    public InvalidQuestionFormatException(String message) {
        super(message);
    }
}