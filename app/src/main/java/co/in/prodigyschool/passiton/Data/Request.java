package co.in.prodigyschool.passiton.Data;

import com.google.firebase.firestore.DocumentId;

public class Request {

    public static final String FIELD_CITY = "city";
    public static final String FIELD_AREA = "area";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_PRICE = "price";


    private String bookName,bookDescription,bookAddress,bookPhoto;
    boolean isTextbook;
    private int bookYear;
    private int gradeNumber,boardNumber;
    private String userId,bookTime;
    private double lat,lng;

    @DocumentId
    private String DocumentId;


    public String getDocumentId() {
        return DocumentId;
    }

    public void setDocumentId(String documentId) {
        this.DocumentId = documentId;
    }

    public int getBookYear() {
        return bookYear;
    }

    public void setBookYear(int bookYear) {
        this.bookYear = bookYear;
    }

    public String getBookTime() {
        return bookTime;
    }

    public void setBookTime(String bookTime) {
        this.bookTime = bookTime;
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

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookDescription() {
        return bookDescription;
    }

    public void setBookDescription(String bookDescription) {
        this.bookDescription = bookDescription;
    }

    public String getBookAddress() {
        return bookAddress;
    }

    public void setBookAddress(String bookAddress) {
        this.bookAddress = bookAddress;
    }

    public boolean isTextbook() {
        return isTextbook;
    }

    public void setTextbook(boolean textbook) {
        isTextbook = textbook;
    }


    public int getGradeNumber() {
        return gradeNumber;
    }

    public void setGradeNumber(int gradeNumber) {
        this.gradeNumber = gradeNumber;
    }

    public int getBoardNumber() {
        return boardNumber;
    }

    public void setBoardNumber(int boardNumber) {
        this.boardNumber = boardNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
