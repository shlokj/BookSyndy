package com.booksyndy.academics.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MyDonationsActivity extends AppCompatActivity {

    private FloatingActionButton donateFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_donations);

        getSupportActionBar().setTitle("Your donations");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        donateFab = findViewById(R.id.fab_donate);

        donateFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyDonationsActivity.this, CreateBundleListingActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        setContentView(R.layout.fragment_donate_loading);
        Intent goHome = new Intent(MyDonationsActivity.this, HomeActivity.class);
        goHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goHome);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
