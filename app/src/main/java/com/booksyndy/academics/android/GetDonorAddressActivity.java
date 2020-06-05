package com.booksyndy.academics.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class GetDonorAddressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_donor_address);

        getSupportActionBar().setTitle("Set pickup address");
    }
}
