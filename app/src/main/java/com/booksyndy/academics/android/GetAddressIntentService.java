package com.booksyndy.academics.android;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;


public class GetAddressIntentService extends IntentService {
    private static final String IDENTIFIER = "GetAddressIntentService";
    private ResultReceiver addressResultReceiver;
    private double lat,lng;

    public GetAddressIntentService() {
        super(IDENTIFIER);
    }

    //handle the address request
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String msg = "";
        String addr_1,addr_2,locality,county,state,country,post;
        //get result receiver from intent
        addressResultReceiver = intent.getParcelableExtra("add_receiver");

        if (addressResultReceiver == null) {
            Log.e("GetAddressIntentService",
                    "No receiver, not processing the request further");
            return;
        }

        Location location = intent.getParcelableExtra("add_location");

        //send no location error to results receiver
        if (location == null) {
            msg = "No location, can't go further without location";
            sendResultsToReceiver(0, msg,null,null,null,null,null,null,0.0,0.0);
            return;
        }
        //call GeoCoder getFromLocation to get address
        //returns list of addresses, take first one and send info to result receiver
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
        } catch (Exception ioException) {
            Log.e("", "Error in getting address for the location");
        }

        if (addresses == null || addresses.size()  == 0) {
            msg = "No address found for the location";
            sendResultsToReceiver(1, msg,null,null,null,null,null,null,0.0,0.0);
        } else {
            Address address = addresses.get(0);
            lat = address.getLatitude();
            lng = address.getLongitude();
            addr_1 = address.getFeatureName() + ", "+ address.getThoroughfare();
            addr_2 = address.getSubLocality();
            locality = address.getLocality();
            county = address.getSubAdminArea();
            state = address.getAdminArea();
            country = address.getCountryName();
            post = address.getPostalCode();

            sendResultsToReceiver(2,addr_1,addr_2,locality,county,state,country,post,lat,lng);
        }
    }

    //to send results to receiver booksyndy the source activity
    private void sendResultsToReceiver(int resultCode, String Addr1,String Addr2,String Locality,String County,String State,String Country,String Post,double latt,double lngg) {
        Bundle bundle = new Bundle();
        bundle.putString("addr1", Addr1);
        bundle.putString("addr2", Addr2);
        bundle.putString("locality", Locality);
        bundle.putString("county", County);
        bundle.putString("state", State);
        bundle.putString("country", Country);
        bundle.putString("post", Post);
        bundle.putDouble("lat",latt);
        bundle.putDouble("lng",lngg);
        addressResultReceiver.send(resultCode, bundle);
    }
}
