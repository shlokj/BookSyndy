package com.booksyndy.academics.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.booksyndy.academics.android.Data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class GetJoinPurposeActivity extends AppCompatActivity {

    //public static final String default_pic_url = "https://firebasestorage.googleapis.com/v0/b/booksyndy-e8ef6.appspot.com/o/default_photos%2Fdefault_profile_image.png?alt=media&token=3dc5c2c2-2b7b-4ae7-8f50-23ea58874fb9";
    private static final String default_pic_url = "https://firebasestorage.googleapis.com/v0/b/booksyndy-e8ef6.appspot.com/o/default_photos%2Fdefault_user_dp.png?alt=media&token=23b43df7-8143-4ad7-bb87-51e49da095c6";

    boolean isParent, toSell, competitiveExam;
    TextView reasonsQuestion;
    RadioGroup reasons;
    String firstName, lastName, username, phoneNumber;
    int gradeNumber, reason, boardNumber, yearNumber;
    Intent startMainActivity;
    User curFirebaseUser;
    FirebaseFirestore db;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_join_purpose);
        getSupportActionBar().setTitle("Sign up");
        reasonsQuestion = findViewById(R.id.reasonQuestionTV);
        reasons = findViewById(R.id.reasonsButtonList);

        registerUser();
//        startMainActivity.putExtra("IS_PARENT", isParent);
//        startMainActivity.putExtra("FIRST_NAME",firstName);
//        startMainActivity.putExtra("LAST_NAME",lastName);
//        startMainActivity.putExtra("GRADE_NUMBER",gradeNumber);
/*        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab7);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // This is the last activity booksyndy which we ask the user for information.
                // After this, on clicking the next button, the user account creation is completed and the user is taken
                // to the main activity.
                // Firebase implementation is required here.
                reason = reasons.getCheckedRadioButtonId();
                if (reason == -1) {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Please select an option", Snackbar.LENGTH_SHORT)
                            .setAction("OKAY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                } else if (reason == R.id.toSell) {
                    toSell = true;
                } else if (reason == R.id.toBuy) {
                    toSell = false;
                }
//                 put firebase-related code here
                registerUser();
            }
        });*/
    }

    private void registerUser() {
        try {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Finishing up");
            progressDialog.setTitle("Just a sec...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            competitiveExam = getIntent().getBooleanExtra("COMPETITIVE_EXAM", false);
            isParent = getIntent().getBooleanExtra("IS_PARENT", false);
            firstName = getIntent().getStringExtra("FIRST_NAME");
            lastName = getIntent().getStringExtra("LAST_NAME");
            gradeNumber = getIntent().getIntExtra("GRADE_NUMBER", 4);
            boardNumber = getIntent().getIntExtra("BOARD_NUMBER", 6);
            boardNumber = getIntent().getIntExtra("DEGREE_NUMBER", boardNumber);
            yearNumber = getIntent().getIntExtra("YEAR_NUMBER", 0);
            username = getIntent().getStringExtra("USERNAME");
            if (gradeNumber<3 || gradeNumber>6) {
                competitiveExam=false;
            }
            phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            curFirebaseUser = new User(firstName, lastName, phoneNumber, isParent, gradeNumber, boardNumber,competitiveExam, username, default_pic_url);
            curFirebaseUser.setYear(yearNumber);
            db = FirebaseFirestore.getInstance();

            // Add a new document with a generated ID
            db.collection("users").document(phoneNumber)
                    .set(curFirebaseUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
//                    Toast.makeText(getApplicationContext(), "User Registered Successfully " + phoneNumber, Toast.LENGTH_LONG).show();
                    // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    saveToken(curFirebaseUser.getPhone());
                    startMain();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    //Log.w(TAG, "Error adding document", e);
                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "User Register Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveToken(final String userId) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        FirebaseFirestore.getInstance().collection("users").document(userId).update("token",token);
                    }
                });
    }


    private void startMain(){
        startMainActivity = new Intent(GetJoinPurposeActivity.this, MainActivity.class);
        startMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startMainActivity.putExtra("SNACKBAR_MSG","Hey there! Thanks for signing up!");
        startActivity(startMainActivity);
        finish();
    }
}