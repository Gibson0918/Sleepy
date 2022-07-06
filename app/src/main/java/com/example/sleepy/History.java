package com.example.sleepy;

public class History {
    String date , pass;

    History() {

    }

    public History(String date, String pass) {
        this.date = date;
        this.pass = pass;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
