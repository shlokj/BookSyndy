package com.booksyndy.academics.android;

import android.content.Intent;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GetJoinPurposeActivity extends AppCompatActivity {

    private static final String default_pic_url = "https://firebasestorage.googleapis.com/v0/b/booksyndy-e8ef6.appspot.com/o/default_photos%2Fdefault_user_dp.png?alt=media&token=23b43df7-8143-4ad7-bb87-51e49da095c6";

    private User curFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_join_purpose);
        getSupportActionBar().setTitle("Sign up");
        getSupportActionBar().hide();

        registerUser();
    }

    private void registerUser() {
        try {

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String date = dateFormat.format(calendar.getTime());

            boolean competitiveExam = getIntent().getBooleanExtra("COMPETITIVE_EXAM", false);
            boolean isParent = getIntent().getBooleanExtra("IS_PARENT", false);
            String firstName = getIntent().getStringExtra("FIRST_NAME");
            String lastName = getIntent().getStringExtra("LAST_NAME");
            int gradeNumber = getIntent().getIntExtra("GRADE_NUMBER", 4);
            int boardNumber = getIntent().getIntExtra("BOARD_NUMBER", 6);
            boardNumber = getIntent().getIntExtra("DEGREE_NUMBER", boardNumber);
            int yearNumber = getIntent().getIntExtra("YEAR_NUMBER", 0);
            String username = getIntent().getStringExtra("USERNAME");
            int userType = getIntent().getIntExtra("USER_TYPE", 1);
            boolean phoneNumberPublic = getIntent().getBooleanExtra("PUBLIC_PHONE", true);
            boolean preferGeneral = getIntent().getBooleanExtra("PREF_GEN",false);

            if (gradeNumber <3 || gradeNumber >6) {
                competitiveExam =false;
            }
            String phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            curFirebaseUser = new User(firstName, lastName, phoneNumber, isParent, gradeNumber, boardNumber, competitiveExam, username, default_pic_url);
            curFirebaseUser.setYear(yearNumber);
            curFirebaseUser.setUserType(userType);
            curFirebaseUser.setCreationDate(date);
            curFirebaseUser.setPhoneNumberPublic(phoneNumberPublic);
            curFirebaseUser.setPreferGeneral(preferGeneral);
            FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        Intent startMainActivity = new Intent(GetJoinPurposeActivity.this, MainActivity.class);
        startMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        startMainActivity.putExtra("SNACKBAR_MSG","Hey there! Thanks for signing up!");
        startActivity(startMainActivity);
        finish();
    }
}
