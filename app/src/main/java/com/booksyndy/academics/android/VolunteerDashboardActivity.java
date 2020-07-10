package com.booksyndy.academics.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class VolunteerDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_dashboard);

        getSupportActionBar().setTitle("Volunteer Dashboard");

    }
    @Override
    public void onBackPressed() {
        Intent homeActivity = new Intent(VolunteerDashboardActivity.this, MyDonationsActivity.class);
//        homeActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeActivity);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_volunteer_dashboard, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setRadLoc:
                // TODO: show dialog with different radii, neutral button will be to change the location
                break;
            case R.id.stopVolunteering:
//                Toast.makeText(this, "stop clicked", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(VolunteerDashboardActivity.this,StopVolunteeringActivity.class));
                break;
        }

        return true;
    }
}
