package com.booksyndy.academics.android;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GetVolunteerAddressActivity extends AppCompatActivity {

    private EditText locField, hnbnField, streetField, pincodeField;
    private TextInputLayout hnbnTIL, streetTIL, pincodeTIL;
    private Button confirmDonation;
    private LinearLayout mapsSearchLL;
    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;

    //    private TextWatcher clearErr;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 108;


    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference bookPhotosStorageReference;

    private String docId, userId, picUrl, docName, mapsLoc, hnbn, street, pinCode;
    private boolean valid, hasAddr;
    private double book_lat,book_lng;

    private static String TAG = "GETDONORADDRESS";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 103;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_volunteer_address);

        getSupportActionBar().setTitle("Enter your address");


        locField = findViewById(R.id.locField2_v);
        hnbnField = findViewById(R.id.hnbnET_v);
        streetField = findViewById(R.id.streetNameET_v);
        pincodeField = findViewById(R.id.pincodeET_v);

        mapsSearchLL = findViewById(R.id.locLL_v);

//        mapsSearchLL.setVisibility(View.GONE);

        hnbnTIL = findViewById(R.id.hnbnTIL_v);
        streetTIL = findViewById(R.id.streetNameTIL_v);
        pincodeTIL = findViewById(R.id.pincodeTIL_v);

        confirmDonation = findViewById(R.id.continueVolSignUp);

        userPref = this.getSharedPreferences(getString(R.string.UserPref),0);

        hasAddr = userPref.getBoolean(getString(R.string.p_userhasaddr), false);

        if (hasAddr) {
            hnbnField.setText(userPref.getString(getString(R.string.p_userhnbn),""));
            streetField.setText(userPref.getString(getString(R.string.p_userstreet),""));
            pincodeField.setText(userPref.getString(getString(R.string.p_userpincode),""));
        }

        editor = userPref.edit();

        initFirebase();


        hnbnField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hnbnTIL.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        streetField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                streetTIL.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pincodeField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pincodeTIL.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Toast.makeText(GetVolunteerAddressActivity.this, "valid is" + valid, Toast.LENGTH_SHORT).show();

                valid = true;
                mapsLoc = locField.getText().toString().trim();
                hnbn = hnbnField.getText().toString().trim();
                street = streetField.getText().toString().trim();
                pinCode = pincodeField.getText().toString().trim();

                if (hnbn.isEmpty()) {
                    hnbnTIL.setError("Please fill in this field");
                    valid = false;
                }

                if (street.isEmpty()) {
                    streetTIL.setError("Please fill in this field");
                    valid = false;
                }

                if (pinCode.isEmpty()) {
                    pincodeTIL.setError("Please fill in this field");
                    valid = false;
                }

                else if (pinCode.length()<6 || pinCode.substring(0,1).equals("0")) {
                    pincodeTIL.setError("Invalid pincode");
                    valid = false;
                }

                if (valid) {
                    editor.putBoolean(getString(R.string.p_userhasaddr),true);
                    editor.putString(getString(R.string.p_userhnbn), hnbn);
                    editor.putString(getString(R.string.p_userstreet),street);
                    editor.putString(getString(R.string.p_userpincode),pinCode);
                    editor.apply();

                    Intent confirmVolunteer = new Intent(GetVolunteerAddressActivity.this,ConfirmVolunteeringSignUpActivity.class);
                    confirmVolunteer.putExtra("VOL_NAME",userPref.getString(getString(R.string.p_firstname),"") + " " + userPref.getString(getString(R.string.p_lastname),""));
                    confirmVolunteer.putExtra("VOL_PHONE",userPref.getString(getString(R.string.p_userphone),""));
                    confirmVolunteer.putExtra("VOL_HNBN",hnbn);
                    confirmVolunteer.putExtra("VOL_STREET",street);
                    confirmVolunteer.putExtra("VOL_PINCODE",pinCode);
                    confirmVolunteer.putExtra("VOL_LAT",book_lat);
                    confirmVolunteer.putExtra("VOL_LNG",book_lng);

                    startActivity(confirmVolunteer);

                }
/*                else {
                    showSnackbar("Please check your inputs");
                }*/

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateUserLocation();

        findViewById(R.id.btn_search_listing_v).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchCalled();
            }
        });

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

    }


    private void initFirebase() {
        try {
            mFirestore = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            userId = mAuth.getCurrentUser().getPhoneNumber();
            mFirebaseStorage = FirebaseStorage.getInstance();
            bookPhotosStorageReference = mFirebaseStorage.getReference().child("book_photos");

            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), getString(R.string.places_api_key));
            }
//            PlacesClient placesClient = Places.createClient(this);
        }
        catch (NullPointerException e){
            Log.e(TAG, "initFireBase: getCurrentUser error", e);
        }
    }

    private void populateUserLocation() {
        Geocoder geocoder;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(this, Locale.getDefault());

        book_lat = userPref.getFloat(getString(R.string.p_lat),0.0f);
        book_lng = userPref.getFloat(getString(R.string.p_lng),0.0f);

        String address = "";
        try {
            addresses = geocoder.getFromLocation(book_lat, book_lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        }
        catch (Exception e) {

        }
//        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//        String city = addresses.get(0).getLocality();
//        String state = addresses.get(0).getAdminArea();
//        String country = addresses.get(0).getCountryName();
//        String postalCode = addresses.get(0).getPostalCode();
//        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
        locField.setText(address);
    }

    public void onSearchCalled() {
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent mapsAC = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("IN")//.setInitialQuery("saket c")
//                .setTypeFilter(TypeFilter.ADDRESS)
                .build(this);
        startActivityForResult(mapsAC, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        Intent homeActivity = new Intent(GetVolunteerAddressActivity.this, MyDonationsActivity.class);
//        homeActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeActivity);
        finish();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

//        autofillBoardTemp();

        switch (requestCode) {
            case AUTOCOMPLETE_REQUEST_CODE: // for places search
                if (resultCode == RESULT_OK) {
//                    Toast.makeText(this, "RESULT_OK", Toast.LENGTH_SHORT).show();
                    Place place = Autocomplete.getPlaceFromIntent(imageReturnedIntent);
                    //Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                    //Toast.makeText(CreateListingActivity.this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                    try {
                        book_lat = place.getLatLng().latitude;
                        book_lng = place.getLatLng().longitude;
                    }
                    catch (Exception e) {

                    }
                    //returns list of addresses, take first one and send info to result receiver
                    Geocoder geocoder;
                    List<Address> addresses = new ArrayList<>();
                    geocoder = new Geocoder(this, Locale.getDefault());

//                    book_lat = userPref.getFloat(getString(R.string.p_lat),0.0f);
//                    book_lng = userPref.getFloat(getString(R.string.p_lng),0.0f);

                    try {
                        addresses = geocoder.getFromLocation(book_lat, book_lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    }
                    catch (Exception e) {

                    }
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    locField.setText(address);

                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    Status status = Autocomplete.getStatusFromIntent(imageReturnedIntent);
                    Toast.makeText(GetVolunteerAddressActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                    Log.i(TAG, status.getStatusMessage());
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
                break;

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT)
                .setAction("OKAY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

}