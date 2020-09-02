package com.booksyndy.academics.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ApproveVolunteerActivity extends AppCompatActivity {

    // for admin part
    private TextView nameView,phoneView,dateView,hnbnView,streetView,pincodeView;
    private Button approveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_volunteer);

        getSupportActionBar().setTitle("Volunteer approval");

        nameView = findViewById(R.id.volunteerName);
        phoneView = findViewById(R.id.volunteerPhone);
        dateView = findViewById(R.id.volunteerDate);
        hnbnView = findViewById(R.id.volunteerHNBN);
        streetView = findViewById(R.id.volunteerStreet);
        pincodeView = findViewById(R.id.volunteerPincode);
        approveBtn = findViewById(R.id.approveButton);

    }
}
