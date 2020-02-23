package com.booksyndy.academics.android;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class AuthActivity extends AppCompatActivity {

    private EditText passwordField;
    private LinearLayout otpLL;
    private String userPhoneNumber, enteredPassword;
    private FloatingActionButton fabLogin;
    private int numTries;
    private boolean textChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        getSupportActionBar().setTitle("Sign in");

        userPhoneNumber = getIntent().getStringExtra("USER_MOB").trim();

        fabLogin = findViewById(R.id.fab_login);
        passwordField = findViewById(R.id.loginPasswordField);
        otpLL = findViewById(R.id.requestOTPLL);

        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textChanged=true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otpLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AuthActivity.this);
                builder.setTitle("Request verification code");
                builder.setMessage("Request a verification code to log in? This will be sent via SMS to your phone number, "+userPhoneNumber+".");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent takeOTP = new Intent(AuthActivity.this, EnterOTPActivity.class);
                        takeOTP.putExtra("USER_MOB", userPhoneNumber);
                        takeOTP.putExtra("FROM_LOGIN",true);
                        startActivity(takeOTP);

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
            }
        });

        fabLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enteredPassword = passwordField.getText().toString();
                boolean correct=true; // TODO: check password and assign here
                if (textChanged) {
                    numTries = numTries + 1;
                }
                textChanged=false;

                if (correct) {
                    //TODO: log user in
                }
                else {

                    showSnackbar("The password you entered is incorrect.");
                    if (numTries>=10) {
                        showSnackbar("You've exceeded the number allowed tries. Please request an OTP to log in.");
                    }
                    else if (numTries>6) {
                        showSnackbar("The password you entered is incorrect. You have "+(10-numTries)+" tries left.");
                    }
                }
            }
        });

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
