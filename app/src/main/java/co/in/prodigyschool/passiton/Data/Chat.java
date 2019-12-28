package co.in.prodigyschool.passiton.Data;

import android.text.TextUtils;

import org.w3c.dom.Text;

public class Chat {
    private String imageUrl;
    private String userName,userStatus,userId;

    public Chat(){

    }
    public Chat(String imageUrl, String userName, String userStatus,String userId) {
        this.imageUrl = imageUrl;
        this.userName = userName;
        this.userStatus = userStatus;
        this.userId = userId;
    }

    public boolean hasAllFields(){
        if(TextUtils.isEmpty(this.userId) || TextUtils.isEmpty(this.userName) || TextUtils.isEmpty(this.imageUrl) || TextUtils.isEmpty(this.userStatus)){
            return false;
        }
        return true;
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
