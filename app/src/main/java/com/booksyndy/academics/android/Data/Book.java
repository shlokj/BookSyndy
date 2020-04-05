package com.booksyndy.academics.android.Data;


import com.google.firebase.firestore.DocumentId;

/**
*Book POJO.
 */
public class Book {

    /* for filtering */
    public static final String FIELD_CITY = "city";
    public static final String FIELD_AREA = "area";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_PRICE = "price";

    private String bookName,bookDescription,bookAddress,bookPhoto;
    boolean isTextbook,isBookSold,general;
    private int bookPrice,bookYear;
    private int gradeNumber,boardNumber;
    private String userId,bookTime;
    private double lat,lng;
    private long createdAt;
    @DocumentId
    private String DocumentId;


    public  Book(){

    }
    //for user entry
    public Book(String userId, boolean isTextbook, String bookName, String bookDescription, int gradeNumber, int boardNumber,int bookPrice, String bookAddress,String bookPhoto,String bookTime,Boolean isBookSold,double lat,double lng){

        this.userId = userId;
        this.bookName = bookName;
        this.bookDescription = bookDescription;
        this.bookAddress = bookAddress;
        this.gradeNumber = gradeNumber;
        this.boardNumber = boardNumber;
        this.bookPrice = bookPrice;
        this.isTextbook = isTextbook;
        this.bookPhoto = bookPhoto;
        this.bookTime = bookTime;
        this.isBookSold = isBookSold;
        this.lat = lat;
        this.lng = lng;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isBookSold() {
        return isBookSold;
    }

    public void setBookSold(boolean bookSold) {
        isBookSold = bookSold;
    }

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

    public int getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(int bookPrice) {
        this.bookPrice = bookPrice;
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

    public String getBookPhoto() {
        return bookPhoto;
    }

    public void setBookPhoto(String bookPhoto) {
        this.bookPhoto = bookPhoto;
    }

    public boolean isGeneral() {
        return general;
    }

    public void setGeneral(boolean general) {
        this.general = general;
    }
}
