package com.example.ce316_project;

public class Student {
    private int studentID;
    private int score;

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Student(int studentID, int score) {
        this.studentID = studentID;
        this.score = score;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentID=" + studentID +
                ", score=" + score +
                '}';
    }
}
