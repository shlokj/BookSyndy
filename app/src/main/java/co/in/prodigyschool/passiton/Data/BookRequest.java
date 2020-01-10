package co.in.prodigyschool.passiton.Data;

public class BookRequest {
    private String title,description,bookAddress, phone,userId,time;
    private int grade,board;
    private double lat,lng;
    private boolean isCompetitive,isComplete,isText;


    public BookRequest() {
    }

    public BookRequest(String title, String description, String bookAddress, String phone, String userId, int grade, int board, double lat, double lng, boolean isCompetitive, boolean isComplete, boolean isText) {
        this.title = title;
        this.description = description;
        this.bookAddress = bookAddress;
        this.phone = phone;
        this.userId = userId;
        this.grade = grade;
        this.board = board;
        this.lat = lat;
        this.lng = lng;
        this.isCompetitive = isCompetitive;
        this.isComplete = isComplete;
        this.isText = isText;
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
