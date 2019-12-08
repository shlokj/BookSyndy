package co.in.prodigyschool.passiton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import co.in.prodigyschool.passiton.Data.User;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class GetJoinPurposeActivity extends AppCompatActivity {

    boolean isParent, toSell, competitiveExam;
    TextView reasonsQuestion;
    RadioGroup reasons;
    String firstName, lastName, username, phoneNumber;
    int gradeNumber, reason, boardNumber;
    Intent startMainActivity;
    User curFirebaseUser;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_join_purpose);
        reasonsQuestion = (TextView) findViewById(R.id.reasonQuestionTV);
        reasons = (RadioGroup) findViewById(R.id.reasonsButtonList);
        startMainActivity = new Intent(GetJoinPurposeActivity.this, MainActivity.class);
//        startMainActivity.putExtra("IS_PARENT", isParent);
//        startMainActivity.putExtra("FIRST_NAME",firstName);
//        startMainActivity.putExtra("LAST_NAME",lastName);
//        startMainActivity.putExtra("GRADE_NUMBER",gradeNumber);
        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab7);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // This is the last activity in which we ask the user for information.
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
        });
    }
    private void registerUser() {
        try {
            competitiveExam = getIntent().getBooleanExtra("COMPETITIVE_EXAM", false);
            isParent = getIntent().getBooleanExtra("IS_PARENT", false);
            firstName = getIntent().getStringExtra("FIRST_NAME");
            lastName = getIntent().getStringExtra("LAST_NAME");
            gradeNumber = getIntent().getIntExtra("GRADE_NUMBER", 4);
            boardNumber = getIntent().getIntExtra("BOARD_NUMBER", 1);
            boardNumber = getIntent().getIntExtra("DEGREE_NUMBER", boardNumber);
            username = getIntent().getStringExtra("USERNAME");
            if (gradeNumber<3 || gradeNumber>6) {
                competitiveExam=false;
            }
            phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            curFirebaseUser = new User(firstName, lastName, phoneNumber, isParent, toSell, gradeNumber, boardNumber,competitiveExam, username, null);
            db = FirebaseFirestore.getInstance();

            // Add a new document with a generated ID
            db.collection("users").document(phoneNumber)
                    .set(curFirebaseUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "User Registered Successfully " + phoneNumber, Toast.LENGTH_LONG).show();
                    // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    startActivity(startMainActivity);
                    finish();
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
}
