package com.booksyndy.academics.android.Data;

public class Feedback {

    private String message;
    private String date;
    private String userId;

    public Feedback(String message, String date, String userPhone) {
        this.message = message;
        this.date = date;
        this.userId = userPhone;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
