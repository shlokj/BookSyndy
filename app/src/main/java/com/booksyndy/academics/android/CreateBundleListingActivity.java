package com.booksyndy.academics.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CreateBundleListingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bundle_listing);

        getSupportActionBar().setTitle("Donate a bundle");
    }
}
