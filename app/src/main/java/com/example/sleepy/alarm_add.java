package com.example.sleepy;

public class alarm_add {
    String AlarmID;
    String PuzzleType;
    String Days;
    Integer TaskID;
    String Time;
    String UserID;
    String label;
    Integer isup;

    public alarm_add() {
    }

    public alarm_add(String AlarmID , String Time, String UserID, String days, String PuzzleType, String label, Integer isup, Integer taskID){
        this.AlarmID = AlarmID;
        this.Time = Time;
        this.UserID = UserID;
        this.Days = days;
        this.PuzzleType = PuzzleType;
        this.label = label;
        this.isup = isup;
        this.TaskID = taskID;
    }


    public String getPuzzleType() {
        return PuzzleType;
    }

    public void setPuzzleType(String puzzleType) {
        PuzzleType = puzzleType;
    }

    public Integer getIsup() {
        return isup;
    }

    public void setIsup(Integer isup) {
        this.isup = isup;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    public String getAlarmID() {
        return AlarmID;
    }

    public void setAlarmID(String alarmID) {
        AlarmID = alarmID;
    }

    public String getDays() {
        return Days;
    }

    public void setDays(String days) {
        Days = days;
    }

    public Integer getTaskID() {
        return TaskID;
    }

    public void setTaskID(Integer taskID) {
        TaskID = taskID;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }
}
