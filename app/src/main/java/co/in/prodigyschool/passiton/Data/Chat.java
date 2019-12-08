package co.in.prodigyschool.passiton.Data;

public class Chat {
    private String imageUrl;
    private String userName,userStatus;

    public Chat(){

    }
    public Chat(String imageUrl, String userName, String userStatus) {
        this.imageUrl = imageUrl;
        this.userName = userName;
        this.userStatus = userStatus;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
