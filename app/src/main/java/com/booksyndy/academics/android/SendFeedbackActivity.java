package com.booksyndy.academics.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.booksyndy.academics.android.Data.Feedback;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class SendFeedbackActivity extends AppCompatActivity {

    private static String TAG = "SENDFEEDBACK";
    private EditText feedbackField;
    private Button sendButton;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String fMessage, date, userId;
    private FirebaseFirestore mFireStore;
    private FirebaseStorage mFirebaseStorage;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);

        feedbackField = findViewById(R.id.feedback_field);
        sendButton = findViewById(R.id.send_fb_button);

        initFirebase();

        getSupportActionBar().setTitle("Send feedback");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fMessage = feedbackField.getText().toString();
                if (fMessage.length()<10) {
                    Snackbar.make(findViewById(android.R.id.content), "Please enter at least 10 characters", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    // send the feedback

                    progressDialog = new ProgressDialog(SendFeedbackActivity.this);
                    progressDialog.setMessage("Sending feedback");
                    progressDialog.setTitle("Just a moment");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    try {

                        calendar = Calendar.getInstance();
                        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        date = dateFormat.format(calendar.getTime());
                        Feedback mFeedback = new Feedback(fMessage, date, userId);

                        CollectionReference feedbackMessages = mFireStore.collection("feedback");

                        feedbackMessages.add(mFeedback).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: sent feedback");
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Feedback sent!", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed to send feedback. Please contact the developer.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Send feedback failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                }
            }
        });


    }

    private void initFirebase() {
        try {
            mFireStore = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            userId = mAuth.getCurrentUser().getPhoneNumber();
            mFirebaseStorage = FirebaseStorage.getInstance();
            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), BuildConfig.GOOGLE_API_KEY);
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
}

