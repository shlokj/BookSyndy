package co.in.prodigyschool.passiton.Data;

public class User {

    private String firstName,lastName,phone,userId,imageUrl;
    private boolean isParent,toSell,competitiveExam;
    private int gradeNumber,boardNumber;



    public User() {
    }



    public User(String firstName, String lastName, String phone, boolean isParent, boolean toSell, int gradeNumber, int boardNumber,boolean competitiveExam,String userId,String imageUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.isParent = isParent;
        this.toSell = toSell;
        this.gradeNumber = gradeNumber;
        this.boardNumber = boardNumber;
        this.competitiveExam = competitiveExam;
        this.userId = userId;
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isParent() {
        return isParent;
    }

    public void setParent(boolean parent) {
        isParent = parent;
    }

    public boolean isToSell() {
        return toSell;
    }

    public void setToSell(boolean toSell) {
        this.toSell = toSell;
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
    public boolean isCompetitiveExam() {
        return competitiveExam;
    }

    public void setCompetitiveExam(boolean competitiveExam) {
        this.competitiveExam = competitiveExam;
    }

    public String getFullName() {
        return this.getFirstName() + " " + this.getLastName();
    }

}
