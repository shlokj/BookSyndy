package com.booksyndy.academics.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

public class SignIn2Activity extends AppCompatActivity {

    private TextView welcomeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in2);

        welcomeTV = findViewById(R.id.welcomeTV);

        welcomeTV.setMovementMethod(new ScrollingMovementMethod());

        SignInButton signInButton = findViewById(R.id.signInButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // temporary
                startActivity(new Intent(SignIn2Activity.this,SignInActivity.class));
            }
        });
    }
}
