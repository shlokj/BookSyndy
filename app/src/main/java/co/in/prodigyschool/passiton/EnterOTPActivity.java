package co.in.prodigyschool.passiton;

import android.app.ProgressDialog;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class EnterOTPActivity extends AppCompatActivity {
    String userPhoneNumber;

    private String ctryCode = "+91";
    private String verificationId;
    private FirebaseAuth mAuth;
    private TextView resendOtp, enterOtpMessage;
    private ProgressDialog progressDialog;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        final EditText otpField = (EditText) findViewById(R.id.editTextOtp);
        userPhoneNumber = getIntent().getStringExtra("USER_MOB");
        resendOtp =  findViewById(R.id.resendOTPButton);
        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode(userPhoneNumber);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        FloatingActionButton next = findViewById(R.id.fab2);
        sendVerificationCode(userPhoneNumber);
        enterOtpMessage =  findViewById(R.id.aboutotpverif);
        enterOtpMessage.setText(enterOtpMessage.getText().toString() + ctryCode + userPhoneNumber);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while we check your verification code");
        progressDialog.setTitle("Verification");
        progressDialog.setCancelable(false);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if otp is correct here
                if (otpField.getText().toString().length() == 6) {
                    verifyCode(otpField.getText().toString().trim());
                    progressDialog.show();
                } else {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "A valid verification code has 6 digits", Snackbar.LENGTH_SHORT)
                            .setAction("OKAY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                }
            }
        });
    }

    private void verifyCode(String code) {

        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInwithCredential(credential);

        }
        catch (Exception e) {
            progressDialog.dismiss();
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "Incorrect verification code", Snackbar.LENGTH_SHORT)
                    .setAction("OKAY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                    .show();
        }
    }

    private void signInwithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    isRegisteredUser();

                } else {
                    progressDialog.dismiss();
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Incorrect verification code", Snackbar.LENGTH_SHORT)
                            .setAction("OKAY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                }
            }
        });

    }

    private void sendVerificationCode(String number) {
        final String phoneNumber = ctryCode + number;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(EnterOTPActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void isRegisteredUser() {

        final String userId = ctryCode + userPhoneNumber;
        db = FirebaseFirestore.getInstance();
        db.collection("users")
                //.whereEqualTo("phone", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        try{
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.getId().equalsIgnoreCase(userId)) {

                                    Intent startmainact = new Intent(EnterOTPActivity.this, MainActivity.class);
                                    startmainact.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(startmainact);
                                    finish();
                                    return;
                                }
                                //Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                            Intent startposact = new Intent(EnterOTPActivity.this, ParOrStudActivity.class);

                            startposact.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(startposact);
                            finish();

                        } else {
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "DataBase Error: Try Again", Snackbar.LENGTH_SHORT)
                                    .setAction("OKAY", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                    .show();
                        }
                    }
                        catch(Exception e){
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "DataBase Error: Try Again", Snackbar.LENGTH_SHORT)
                                    .setAction("OKAY", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                    .show();
                        }
                    }

                });


    }


}
