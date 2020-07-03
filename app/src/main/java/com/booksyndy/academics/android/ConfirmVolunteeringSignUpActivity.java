package com.booksyndy.academics.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ConfirmVolunteeringSignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_volunteering_sign_up);

        getSupportActionBar().setTitle("Confirm your details");
    }
}
