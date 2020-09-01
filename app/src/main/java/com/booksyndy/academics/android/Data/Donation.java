package com.booksyndy.academics.android.Data;

import com.google.firebase.firestore.DocumentId;

public class Donation {


    private String donationName,donationDescription,donationPhoto,acceptedByPhone,acceptedByName,mapsAddress;
    private int status, approxWeight;
    private String userId,donationListingTime,donationAcceptedTime,address,donorName;
    private double lat,lng;
    private long createdAt,acceptedAt;
    @com.google.firebase.firestore.DocumentId
    private String DocumentId;


    public Donation(){

    }
    //for user entry
    public Donation(String userId, String donationName, String donationDescription, String donationPhoto,String donationListingTime,double lat,double lng, int status, int approxWeight, long createdAt, String donorName){

        this.userId = userId;
        this.donationName = donationName;
        this.donationDescription = donationDescription;
        this.donationPhoto = donationPhoto;
        this.donationListingTime = donationListingTime;
        this.lat = lat;
        this.lng = lng;
        this.status = status;
        this.approxWeight = approxWeight;
        this.acceptedByPhone = null;
        this.acceptedByName = null;
        this.createdAt = createdAt;
        this.donorName = donorName;
    }

    public String getAcceptedByPhone() {
        return acceptedByPhone;
    }

    public void setAcceptedByPhone(String acceptedByPhone) {
        this.acceptedByPhone = acceptedByPhone;
    }

    public String getAcceptedByName() {
        return acceptedByName;
    }

    public void setAcceptedByName(String acceptedByName) {
        this.acceptedByName = acceptedByName;
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

    public void setStatus(int status) { // 1: uploaded (received by us)1, 2: confirmed, 3: received/picked up by foundation
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setDonationAcceptedTime(String donationAcceptedTime) {
        this.donationAcceptedTime = donationAcceptedTime;
    }

    public String getDonationAcceptedTime() {
        return donationAcceptedTime;
    }

    public void setAcceptedAt(long acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public long getAcceptedAt() {
        return acceptedAt;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setMapsAddress(String mapsAddress) {
        this.mapsAddress = mapsAddress;
    }

    public String getMapsAddress() {
        return mapsAddress;
    }
}
