package com.booksyndy.academics.android;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.concurrent.TimeUnit;

public class EnterOTPActivity extends AppCompatActivity {
    String userPhoneNumber;

    private int autoResendCount=0, resumeCount=0;
    private final String ctryCode = "+91";
    private String verificationId;
    private FirebaseAuth mAuth;
    private TextView resendOtp, enterOtpMessage, timerTV;
    private ProgressDialog progressDialog;
    private EditText otpField;
    private boolean firstSend=true,timerOn=true;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        mAuth = FirebaseAuth.getInstance();
        otpField = findViewById(R.id.editTextOtp);
        resendOtp =  findViewById(R.id.resendOTPButton);
        timerTV = findViewById(R.id.resendTimer);
        userPhoneNumber = getIntent().getStringExtra("USER_MOB").trim();
        sendVerificationCode(userPhoneNumber);
        startResendTimer(15);
        getSupportActionBar().setTitle("Verification");
        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!timerOn) {
                    startResendTimer(30);
                    firstSend = false;
                    sendVerificationCode(userPhoneNumber);
                }
            }
        });
        FloatingActionButton next = findViewById(R.id.fab2);
        enterOtpMessage =  findViewById(R.id.aboutotpverif);
        enterOtpMessage.setText(enterOtpMessage.getText().toString() + " " + ctryCode + userPhoneNumber);
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
                    if(!progressDialog.isShowing())
                    progressDialog.show();
                } else {
                    showSnackbar("A valid verification code has 6 digits");
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
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            }, 100);
            progressDialog.setCancelable(true);
            showSnackbar(getString(R.string.incorrect_code_t1));
            View parentLayout = findViewById(android.R.id.content);
            if(progressDialog.isShowing())
            progressDialog.dismiss();
        }
    }

    private void signInwithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    isRegisteredUser();
                }
                else {
                    if(progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    showSnackbar("Incorrect verification code");
                    if(progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                }
            }
        });

    }

    private void sendVerificationCode(String number) {
        final String phoneNumber = ctryCode + number;
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                90,                 // Timeout duration
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
                otpField.setText(code);
                if(!progressDialog.isShowing())
                progressDialog.show();
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void isRegisteredUser() {

        final String userId = ctryCode + userPhoneNumber;
        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("phone", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        try{
                        if (task.isSuccessful()) {

                            saveToken(userId);

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
                            showSnackbar("Database error: please try again");
                        }
                    }
                        catch(Exception e){
                            showSnackbar("Database error: please try again");
                        }
                    }

                });
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


    @Override
    protected void onResume() {
        super.onResume();
        if (autoResendCount<2 && resumeCount>0) {
            sendVerificationCode(userPhoneNumber);
            autoResendCount=autoResendCount+1;
            resumeCount=resumeCount+1;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        progressDialog.dismiss();
    }

    public void startResendTimer(int seconds) {
        timerTV.setVisibility(View.VISIBLE);
        resendOtp.setEnabled(false);

        new CountDownTimer(seconds*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                String secondsString = Long.toString(millisUntilFinished/1000);
                if (millisUntilFinished<10000) {
                    secondsString = "0"+secondsString;
                }
                timerTV.setText(" (0:"+ secondsString+")");
            }

            public void onFinish() {
                resendOtp.setEnabled(true);
                timerTV.setVisibility(View.GONE);
                timerOn=false;
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to go back?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EnterOTPActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {   }
        });
        builder.show();
    }


    public void showSnackbar(String message) {
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
