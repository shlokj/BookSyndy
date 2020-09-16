package com.booksyndy.academics.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.booksyndy.academics.android.ui.volunteerDashboard.PageAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class VolunteerDashboardActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private static String TAG = "VOLUNTEER_DASHBOARD";
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabItem tab_unaccepted,tab_accepted;
    private com.booksyndy.academics.android.ui.volunteerDashboard.PageAdapter mPageAdapter;
    private RadioGroup rRadioGroup;
    private int newValue;


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
           builder.setTitle("You're comfortable collecting books within");
//                builder.setView(R.layout.fragment_choose_radius);
            String[] radiusList = {"1 Km","2 Km","3 Km","4 Km","5 Km","7 Km","10 Km"};

                int oldValue = 4; // cow

                builder.setSingleChoiceItems(radiusList, oldValue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // user checked an item
                        newValue = which;
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        switch (newValue) {
                            case 0:
                                updateRadiusPreference(1);
                                break;
                            case 1:
                                updateRadiusPreference(2);
                                break;
                            case 2:
                                updateRadiusPreference(3);
                                break;
                            case 3:
                                updateRadiusPreference(4);
                                break;
                            case 4:
                                updateRadiusPreference(5);
                                break;
                            case 5:
                                updateRadiusPreference(7);
                                break;
                            case 6:
                                updateRadiusPreference(10);
                                break;
                            default:break;
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

                AlertDialog rad = builder.create();
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

    private void updateRadiusPreference(int radiusPreference){
        final ProgressDialog sProgressDialog = new ProgressDialog(VolunteerDashboardActivity.this);
        sProgressDialog.setMessage("Just a moment...");
        sProgressDialog.setTitle("Processing");
        sProgressDialog.setCancelable(false);
        sProgressDialog.show();
        try {
            if(radiusPreference < 1){
                Toast.makeText(getApplicationContext(), "Invalid Radius Preference", Toast.LENGTH_SHORT).show();
                return;
            }


            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
            String curUserPhone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            mFirestore.collection("volunteers").document(curUserPhone).update("radiusPreference",radiusPreference)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if(sProgressDialog.isShowing()){
                                sProgressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), "Your Preference Updated", Toast.LENGTH_SHORT).show();

                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(sProgressDialog.isShowing()){
                                sProgressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), "Failed to Update Preference", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        catch (NullPointerException e){
            if(sProgressDialog.isShowing()){
                sProgressDialog.dismiss();
            }
            Toast.makeText(getApplicationContext(), "Server Error, Try Again", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "initFireBase: getCurrentUser error", e);
        }
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
