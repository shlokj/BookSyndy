package com.booksyndy.academics.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.booksyndy.academics.android.Data.Feedback;
import com.booksyndy.academics.android.Data.PickupRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RequestBookCollectionActivity extends AppCompatActivity {

    private static String TAG = "REQUESTDONATIONPICKUP";
    private EditText descField;
    private TextInputLayout descTIL;
    private Button sendButton;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String comments, date, userId;
    private FirebaseFirestore mFireStore;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_book_collection);


        descField = findViewById(R.id.pickupReqCommentsET);
        descTIL = findViewById(R.id.pickupReqCommentsTIL);
        sendButton = findViewById(R.id.submitPickupReq);

        descField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                descTIL.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        initFirebase();

        getSupportActionBar().setTitle("Request pickup");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comments = descField.getText().toString().trim();

                if (comments.length()<10) {
                    descTIL.setError("Please enter at least 10 characters.");
                }
                else {

                    progressDialog = new ProgressDialog(RequestBookCollectionActivity.this);
                    progressDialog.setMessage("Submitting request");
                    progressDialog.setTitle("Just a moment");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    try {

                        calendar = Calendar.getInstance();
                        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        date = dateFormat.format(calendar.getTime());
                        PickupRequest pickupRequest = new PickupRequest(comments, date, userId);

                        CollectionReference pickupReqRef = mFireStore.collection("pickupRequests");

                        pickupReqRef.add(pickupRequest).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: submitted pickup req");
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Submitted your request! We'll be in touch shortly.", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Failed to submit. Please contact us.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Submit pickup req failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        if (progressDialog.isShowing())
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
            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), getString(R.string.places_api_key));
            }
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
