package com.booksyndy.academics.android.Data;

public class Volunteer {

    private String name, phone, hnbn, street, pincode;
//    private int status;
    private double lat, lng;
    private long createdAt;

    public Volunteer(String name, String phone, /*int status,*/ String hnbn, String street, String pincode, double lat, double lng, long createdAt) {
        this.name = name;
        this.phone = phone;
//        this.status = status;
        this.hnbn = hnbn;
        this.street = street;
        this.pincode = pincode;
        this.lat = lat;
        this.lng = lng;
        this.createdAt = createdAt;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

/*    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }*/

    public void setHnbn(String hnbn) {
        this.hnbn = hnbn;
    }

    public String getHnbn() {
        return hnbn;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet() {
        return street;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPincode() {
        return pincode;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLat() {
        return lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLng() {
        return lng;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
