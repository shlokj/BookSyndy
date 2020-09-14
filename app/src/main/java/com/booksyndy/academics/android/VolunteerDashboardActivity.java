package com.booksyndy.academics.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.booksyndy.academics.android.ui.volunteerDashboard.PageAdapter;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class VolunteerDashboardActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private static String TAG = "VOLUNTEER_DASHBOARD";
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabItem tab_unaccepted,tab_accepted;
    private com.booksyndy.academics.android.ui.volunteerDashboard.PageAdapter mPageAdapter;
    private RadioGroup rRadioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_dashboard);
        getSupportActionBar().setTitle("Volunteer Dashboard");

        /*
        view pager code here
         */
        mViewPager = findViewById(R.id.donations_viewpager);
        mTabLayout = findViewById(R.id.donation_listings_tab);
        tab_unaccepted = findViewById(R.id.tab_unaccepted);
        tab_accepted = findViewById(R.id.tab_accepted);
        mPageAdapter = new PageAdapter(getSupportFragmentManager(),mTabLayout.getTabCount());
        mViewPager.setAdapter(mPageAdapter);
        mTabLayout.addOnTabSelectedListener(this);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));


    }





    @Override
    public void onBackPressed() {
        Intent homeActivity = new Intent(VolunteerDashboardActivity.this, HomeActivity.class);
        homeActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                // TODO: show dialog with different radii, neutral button will be to change the location. currently takes user to change address class (for testing)
                AlertDialog.Builder builder = new AlertDialog.Builder(VolunteerDashboardActivity.this);
//                builder.setTitle("Change radius preference");
                builder.setView(R.layout.fragment_choose_radius);

                AlertDialog rad = builder.create();

                rRadioGroup = (RadioGroup) rad.findViewById(R.id.radiusButtonList);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int selRadOpt=0;
                        switch (selRadOpt) {
                            case R.id.km1:

                                break;
                            case R.id.km2:

                                break;
                            case R.id.km3:

                                break;
                            case R.id.km4:

                                break;
                            case R.id.km5:

                                break;
                            case R.id.km7:

                                break;
                            case R.id.km10:

                                break;
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setNeutralButton("Change Address", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(VolunteerDashboardActivity.this,ChangeVolunteerAddressActivity.class));
                    }
                });


                builder.show();
                rRadioGroup = rad.findViewById(R.id.radiusButtonList);


                break;
            case R.id.stopVolunteering:
//                Toast.makeText(this, "stop clicked", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder sBuilder = new AlertDialog.Builder(VolunteerDashboardActivity.this);
                sBuilder.setTitle("Terminate volunteering");
                sBuilder.setMessage("Are you sure you want to resign as a volunteer?");
                sBuilder.setPositiveButton("No, stay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                sBuilder.setNegativeButton("Yes, terminate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(VolunteerDashboardActivity.this,StopVolunteeringActivity.class));
                    }
                });
                sBuilder.show();
                break;
            case R.id.requestPickup:
                startActivity(new Intent(VolunteerDashboardActivity.this,RequestBookCollectionActivity.class));
                break;
        }

        return true;
    }


    /*
  tab selected callbacks
   */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());
    }
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }
    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
