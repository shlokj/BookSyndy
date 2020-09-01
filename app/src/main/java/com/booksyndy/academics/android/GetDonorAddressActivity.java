package com.booksyndy.academics.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GetDonorAddressActivity extends AppCompatActivity {

    private EditText locField, hnbnField, streetField, pincodeField;
    private TextInputLayout hnbnTIL, streetTIL, pincodeTIL;
    private Button confirmDonation;
    private LinearLayout mapsSearchLL;
    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 108;


    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference bookPhotosStorageReference;

    private String docId, userId, picUrl, docName, mapsLoc, hnbn, street, pinCode;
    private boolean valid;
    private double book_lat,book_lng;

    private static String TAG = "GETDONORADDRESS";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 103;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_donor_address);

        getSupportActionBar().setTitle("Set pickup address");
        initFirebase();
        docId = getIntent().getStringExtra("DON_DOC_ID");
        docName = getIntent().getStringExtra("DON_DOC_NAME");

//        Toast.makeText(this, "doc id: "+docId, Toast.LENGTH_SHORT).show();
        picUrl = getIntent().getStringExtra("PIC_URL");

        locField = findViewById(R.id.locField2_d);
        hnbnField = findViewById(R.id.hnbnET);
        streetField = findViewById(R.id.streetNameET);
        pincodeField = findViewById(R.id.pincodeET);

        mapsSearchLL = findViewById(R.id.locLL_d);

        //mapsSearchLL.setVisibility(View.GONE);

        hnbnTIL = findViewById(R.id.hnbnTIL);
        streetTIL = findViewById(R.id.streetNameTIL);
        pincodeTIL = findViewById(R.id.pincodeTIL);

        confirmDonation = findViewById(R.id.confirmDonationBtn);

        userPref = this.getSharedPreferences(getString(R.string.UserPref),0);

        boolean hasAddr = userPref.getBoolean(getString(R.string.p_userhasaddr), false);

        if (hasAddr) {
            hnbnField.setText(userPref.getString(getString(R.string.p_userhnbn),""));
            streetField.setText(userPref.getString(getString(R.string.p_userstreet),""));
            pincodeField.setText(userPref.getString(getString(R.string.p_userpincode),""));
        }

        editor = userPref.edit();




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

//                Toast.makeText(GetDonorAddressActivity.this, "valid is" + valid, Toast.LENGTH_SHORT).show();

                valid = true;
                mapsLoc = locField.getText().toString().trim();
                hnbn = hnbnField.getText().toString().trim();
                street = streetField.getText().toString().trim();
                pinCode = pincodeField.getText().toString().trim();

/*                        if (mapsLoc.isEmpty()) {
                            Toast.makeText(GetDonorAddressActivity.this, "Couldn't get your location. Please search for it manually.", Toast.LENGTH_SHORT).show();
                        }*/

                if (book_lat == 0.0 || book_lng == 0.0) {
                    locField.setError("Please fill in this field");
                    valid = false;
                }

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


                final AlertDialog.Builder cBuilder = new AlertDialog.Builder(GetDonorAddressActivity.this); // TODO: change to create bundle
                cBuilder.setTitle("Confirm donation");
                cBuilder.setMessage("By clicking confirm, you agree that the details you provided will be shared with a representative of the Foundation and you may be contacted for pickup-related issues.");
                cBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        editor.putBoolean(getString(R.string.p_userhasaddr),true);
                        editor.putString(getString(R.string.p_userhnbn), hnbn);
                        editor.putString(getString(R.string.p_userstreet),street);
                        editor.putString(getString(R.string.p_userpincode),pinCode);
                        editor.apply();

                        final ProgressDialog sProgressDialog = new ProgressDialog(GetDonorAddressActivity.this);
                        sProgressDialog.setMessage("Just a moment...");
                        sProgressDialog.setTitle("Submitting");
                        sProgressDialog.setCancelable(false);
                        sProgressDialog.show();

                        String pickupAddress = hnbn + ", " + street + ", " + pinCode;

/*                        if (!streetField.getText().toString().trim().isEmpty()) {
                            pickupAddress = pickupAddress + "Street name and other details: " + streetField.getText().toString().trim();
                        }*/


                        DocumentReference donReference = mFirestore.collection("donations").document(docName);

                        donReference.update("lat", book_lat, "lng", book_lng, "address", pickupAddress, "status", 1, "mapsAddress", mapsLoc)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(new Intent(GetDonorAddressActivity.this, MyDonationsActivity.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                sProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Failed to submit request. Please try again after some time.", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });
                if (valid) {
                    cBuilder.show();
                }

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateUserLocation();

        findViewById(R.id.btn_search_listing_d).setOnClickListener(new View.OnClickListener() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(GetDonorAddressActivity.this);
        builder.setTitle("Discard your donation request?");
        builder.setMessage("You have unsaved changes. Would you like to stay or discard them?");
        builder.setPositiveButton("Stay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                            Intent homeIntent = new Intent(UserProfileActivity.this, HomeActivity.class);
//                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            homeIntent.putExtra("SNACKBAR_MSG", "Your profile has been saved");
//                            startActivity(homeIntent);

            }
        });
        builder.setNegativeButton("Exit without saving", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteDoc();
//                String picUrl = getIntent().getStringExtra("PIC_URL");
//                Toast.makeText(getApplicationContext(), picUrl, Toast.LENGTH_SHORT).show();

            }
        });
        builder.show();
    }
/*

    private void deleteDocUnsafe() {
//        Toast.makeText(this, picUrl, Toast.LENGTH_SHORT).show();

//        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(picUrl);
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(picUrl);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("firebasestorage", "onSuccess: deleted photo");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebasestorage", "onFailure: did not delete photo");
            }
        });

        final ProgressDialog cProgressDialog = new ProgressDialog(this);
        cProgressDialog.setTitle("Removing...");
        cProgressDialog.setMessage("Cancelling your donation request, just a moment");
        cProgressDialog.setCancelable(false);
        cProgressDialog.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("donations").document(docId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        cProgressDialog.dismiss();
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        cProgressDialog.dismiss();
                        Log.w(TAG, "Error deleting document", e);
                    }
                });


    }
*/

    private void deleteDoc() {
//        Toast.makeText(this, picUrl, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Doc na", Toast.LENGTH_SHORT).show();
        try {
//            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(picUrl);
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(picUrl);
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("firebasestorage", "onSuccess: deleted photo");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e("firebasestorage", "onFailure: did not delete photo");
                }
            });

            final ProgressDialog cProgressDialog = new ProgressDialog(this);
            cProgressDialog.setTitle("Removing...");
            cProgressDialog.setMessage("Cancelling your donation request, just a moment");
            cProgressDialog.setCancelable(false);
            cProgressDialog.show();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("donations").document(docName)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            cProgressDialog.dismiss();
//                            Toast.makeText(GetDonorAddressActivity.this, "Donation doc removed successfully", Toast.LENGTH_SHORT).show();
                            Intent homeActivity = new Intent(GetDonorAddressActivity.this, HomeActivity.class);
                            startActivity(homeActivity);
                            finish();
                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            cProgressDialog.dismiss();
                            Log.w(TAG, "Error deleting document", e);
                        }
                    });

        }

        catch (Exception storageExc) {
            Toast.makeText(this, "Oops, ran into an error", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "URL: "+picUrl, Toast.LENGTH_SHORT).show();
        }

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
//
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
                    Toast.makeText(GetDonorAddressActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
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

}
