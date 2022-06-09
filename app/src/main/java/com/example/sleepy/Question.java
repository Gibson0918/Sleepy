package com.example.sleepy;

public class Question {
    String Question, Answer, PuzzleType, TaskID;

    public Question(String question, String answer, String PuzzleType, String TaskID) {
        Question = question;
        Answer = answer;
        this.PuzzleType = PuzzleType;
        TaskID = TaskID;
    }

    public String getTaskID() {
        return TaskID;
    }

    public void setTaskID(String taskID) {
        TaskID = taskID;
    }

    public String getPuzzleType() {
        return PuzzleType;
    }

    public void setPuzzleType(String puzzleType) {
        PuzzleType = puzzleType;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }
}
