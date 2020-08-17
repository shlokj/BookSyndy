package com.booksyndy.academics.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.booksyndy.academics.android.Data.User;
import com.booksyndy.academics.android.Data.Volunteer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ConfirmVolunteeringSignUpActivity extends AppCompatActivity {



    private TextView volName, volPhone, volAddress;
    private String name, phone, hnbn, street, pincode;
    private double lat,lng;
    private Button confirmButton;
    boolean ts1 = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_volunteering_sign_up);

        getSupportActionBar().setTitle("Confirm your details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        volName = findViewById(R.id.volNameFinal);
        volPhone = findViewById(R.id.volPhoneFinal);
        volAddress = findViewById(R.id.volAddressFinal);
        confirmButton = findViewById(R.id.confirmVolReg);

        name = getIntent().getStringExtra("VOL_NAME");
        phone = getIntent().getStringExtra("VOL_PHONE");
        hnbn = getIntent().getStringExtra("VOL_HNBN");
        street = getIntent().getStringExtra("VOL_STREET");
        pincode = getIntent().getStringExtra("VOL_PINCODE");
        lat = getIntent().getDoubleExtra("VOL_LAT",0);
        lng = getIntent().getDoubleExtra("VOL_LNG",0);

        Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        volName.setText(name);
        volPhone.setText(phone.substring(0,3) + " " + phone.substring(3));
        volAddress.setText(hnbn + ", " + street + ", " + pincode);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkConnection(getApplicationContext())) {
                    registerVolunteer();
                }
                else {
                    showSnackbar("Please check your internet connection");
                }
            }
        });

    }


    private void registerVolunteer() {
        try {
            setContentView(R.layout.screen_loading);
            getSupportActionBar().hide();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String date = dateFormat.format(calendar.getTime());

            Volunteer curVolunteer = new Volunteer(name,phone,hnbn,street,pincode,lat, lng, new Date().getTime());
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Add a new document with a generated ID
            db.collection("volunteers").document(phone)
                    .set(curVolunteer).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
//                    startActivity(new Intent(ConfirmVolunteeringSignUpActivity.this,VolunteerDashboardActivity.class));
                    ts1 = true;
                    saveToken(phone);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    //Log.w(TAG, "Error adding document", e);
                    Toast.makeText(ConfirmVolunteeringSignUpActivity.this, "Oops, ran into an error. Please try again later.", Toast.LENGTH_SHORT).show();
                    setContentView(R.layout.activity_confirm_volunteering_sign_up);
                }
            });
            DocumentReference userReference = db.collection("users").document(phone);
            userReference.update("volunteerStatus", 1).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
//                    SharedPreferences userPref = getSharedPreferences(getString(R.string.UserPref),0);
                    SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.UserPref),0).edit();
                    editor.putInt(getString(R.string.p_uservolstatus),1);
                    editor.apply();
                    if (ts1) {
                        startActivity(new Intent(ConfirmVolunteeringSignUpActivity.this,VolunteerDashboardActivity.class));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ConfirmVolunteeringSignUpActivity.this, "Oops, ran into an error. Please try again later.", Toast.LENGTH_SHORT).show();
                    setContentView(R.layout.activity_confirm_volunteering_sign_up);
                }
            });


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "User register failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_confirm_volunteering_sign_up);
        }
    }


    private void saveToken(final String userId) {
        try {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            FirebaseFirestore.getInstance().collection("volunteers").document(userId).update("token", token);
                        }
                    });
        } catch (Exception e) {
            //Log.e(TAG, "saveToken: ", e);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) { // connected to the internet
                // connected to the mobile provider's data plan
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }

    private void showSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT)
                .setAction("OKAY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

}
