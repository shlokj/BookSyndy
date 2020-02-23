package com.booksyndy.academics.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText oldPWField, newPWField, confirmNewPWField;
    private FloatingActionButton fabSavePW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        getSupportActionBar().setTitle("Reset your password");

        oldPWField = findViewById(R.id.oldPasswordField);
        newPWField = findViewById(R.id.newPasswordField);
        confirmNewPWField = findViewById(R.id.confirmNewPasswordField);

        fabSavePW = findViewById(R.id.fab_savenewpw);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
