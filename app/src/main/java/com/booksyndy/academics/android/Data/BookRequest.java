package com.booksyndy.academics.android.Data;

public class BookRequest {
    private String title,description,bookAddress, phone,userId,time;
    private int grade,board,bookYear;

    private boolean isCompetitive,isComplete,isText;
    private double lat,lng;

    @com.google.firebase.firestore.DocumentId
    private String DocumentId;


    public BookRequest() {
    }

    public BookRequest(String title, String description, String bookAddress, String phone, String userId, int grade, int board,int  year, boolean isCompetitive, boolean isComplete, boolean isText,double lat,double lng) {
        this.title = title;
        this.description = description;
        this.bookAddress = bookAddress;
        this.phone = phone;
        this.userId = userId;
        this.grade = grade;
        this.board = board;
        this.bookYear = year;
        this.lat = lat;
        this.lng = lng;
        this.isCompetitive = isCompetitive;
        this.isComplete = isComplete;
        this.isText = isText;
    }

    public String getDocumentId() {
        return DocumentId;
    }

    public void setDocumentId(String documentId) {
        DocumentId = documentId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getBookYear() {
        return bookYear;
    }

    public void setBookYear(int bookYear) {
        this.bookYear = bookYear;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBookAddress() {
        return bookAddress;
    }

    public void setBookAddress(String bookAddress) {
        this.bookAddress = bookAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getBoard() {
        return board;
    }

    public void setBoard(int board) {
        this.board = board;
    }



    public boolean isCompetitive() {
        return isCompetitive;
    }

    public void setCompetitive(boolean competitive) {
        isCompetitive = competitive;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public boolean isText() {
        return isText;
    }

    public void setText(boolean text) {
        isText = text;
    }
}
