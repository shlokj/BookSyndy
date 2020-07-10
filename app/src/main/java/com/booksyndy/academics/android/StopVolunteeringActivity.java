package com.booksyndy.academics.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.booksyndy.academics.android.Data.Feedback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StopVolunteeringActivity extends AppCompatActivity {

    private static String TAG = "STOPVOLUNTEERING";
    private EditText feedbackField;
    private Button confirmButton;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String fMessage, date, userId;
    private FirebaseFirestore mFirestore;
    private FirebaseStorage mFirebaseStorage;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_volunteering);

        initFirebase();

        getSupportActionBar().setTitle("Confirm");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        confirmButton = findViewById(R.id.confirmStopVol);
        feedbackField = findViewById(R.id.volunteerFeedbackET);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkConnection(getApplicationContext())) {

                    fMessage = feedbackField.getText().toString().trim();

                    if (!fMessage.isEmpty()) {

                        try {
                            calendar = Calendar.getInstance();
                            dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            date = dateFormat.format(calendar.getTime());
                            Feedback mFeedback = new Feedback("[volunteer] \n" + fMessage, date, userId);


                            CollectionReference feedbackMessages = mFirestore.collection("feedback");


                            feedbackMessages.add(mFeedback).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: sent feedback");
                                        Toast.makeText(getApplicationContext(), "Feedback sent!", Toast.LENGTH_SHORT).show();
                                        onBackPressed();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed to send feedback. Please contact us.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Send feedback failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    DocumentReference userReference = mFirestore.collection("users").document(userId);
                    userReference.update("volunteerStatus", 3).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.UserPref),0).edit();
                            editor.putInt(getString(R.string.p_uservolstatus),3);
                            editor.apply();
                            Intent homeActivity = new Intent(StopVolunteeringActivity.this, HomeActivity.class);
                            homeActivity.putExtra("SNACKBAR_MSG","You have resigned as a volunteer. You can go back to the donate section to rejoin anytime.");
                            homeActivity.putExtra("SB_LONG",true);
                            startActivity(homeActivity);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(StopVolunteeringActivity.this, "Failed to submit request. Please try again later, or email us.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                else {
                    showSnackbar("Please check your internet connection");
                }
            }
        });



    }

    private void initFirebase() {
        try {
            mFirestore = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            userId = mAuth.getCurrentUser().getPhoneNumber();
            mFirebaseStorage = FirebaseStorage.getInstance();
            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), getString(R.string.places_api_key));
            }
//            PlacesClient placesClient = Places.createClient(this);
        }
        catch (NullPointerException e){
            Log.e(TAG, "initFireBase: getCurrentUser error", e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) { // connected to the internet
                // connected to the mobile provider's data plan
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
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
