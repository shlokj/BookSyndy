package com.booksyndy.academics.android.Data;

import com.google.firebase.firestore.DocumentId;

public class Donation {


    private String donationName,donationDescription,donationPhoto;
    private int status, approxWeight;
    private String userId,donationListingTime;
    private double lat,lng;
    private long createdAt;
    @com.google.firebase.firestore.DocumentId
    private String DocumentId;


    public Donation(){

    }
    //for user entry
    public Donation(String userId, String donationName, String donationDescription, String donationPhoto,String donationListingTime,double lat,double lng, int status, int approxWeight){

        this.userId = userId;
        this.donationName = donationName;
        this.donationDescription = donationDescription;
        this.donationPhoto = donationPhoto;
        this.donationListingTime = donationListingTime;
        this.lat = lat;
        this.lng = lng;
        this.status = status;
        this.approxWeight = approxWeight;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getDocumentId() {
        return DocumentId;
    }

    public void setDocumentId(String documentId) {
        this.DocumentId = documentId;
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

    public void setDonationDescription(String donationDescription) {
        this.donationDescription = donationDescription;
    }

    public String getDonationDescription() {
        return donationDescription;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setApproxWeight(int approxWeight) {
        this.approxWeight = approxWeight;
    }

    public int getApproxWeight() {
        return approxWeight;
    }

    public void setDonationListingTime(String donationListingTime) {
        this.donationListingTime = donationListingTime;
    }

    public String getDonationListingTime() {
        return donationListingTime;
    }

    public void setDonationName(String donationName) {
        this.donationName = donationName;
    }

    public String getDonationName() {
        return donationName;
    }

    public void setDonationPhoto(String donationPhoto) {
        this.donationPhoto = donationPhoto;
    }

    public String getDonationPhoto() {
        return donationPhoto;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
