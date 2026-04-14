package com.quiz.model;

/**
 * Holds identifying information for the student taking the quiz.
 */
public class Student {

    private final String name;
    private final String rollNumber;

    public Student(String name, String rollNumber) {
        this.name       = name.trim();
        this.rollNumber = rollNumber.trim();
    }

    public String getName()       { return name; }
    public String getRollNumber() { return rollNumber; }

    @Override
    public String toString() {
        return "Student{name='" + name + "', roll='" + rollNumber + "'}";
    }
}
