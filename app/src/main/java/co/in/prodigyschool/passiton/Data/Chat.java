package co.in.prodigyschool.passiton.Data;

import android.text.TextUtils;

import com.google.firebase.firestore.DocumentId;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Chat {
    private String imageUrl;
    private String userName,userStatus,userId;
    private String timestamp;


    @DocumentId
    private String DocumentId;

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

    public String getTimestamp() {
        return timestamp;
    }

    public int getTimeDiff(){
try {
    SimpleDateFormat myFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
    String currentDate = myFormat.format(new Date());

    Date dateBefore = myFormat.parse(currentDate);
    Date dateAfter = myFormat.parse(timestamp);
    if(dateAfter == null || dateBefore == null){
        return -1;
    }
    return (int)(dateBefore.getTime() - dateAfter.getTime())/(1000 );
}
catch (Exception e){

    return -1;
}
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDocumentId() {
        return DocumentId;
    }

    public void setDocumentId(String documentId) {
        this.DocumentId = documentId;
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
