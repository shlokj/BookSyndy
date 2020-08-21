package com.booksyndy.academics.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.booksyndy.academics.android.Data.Donation;
import com.booksyndy.academics.android.Data.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Date;

public class DonationDetailsAcceptActivity extends AppCompatActivity  {

    private static final String TAG = "DONATION_DETAILS_ACCEPT";

    private String don_id, donTitle, donDesc, donPic,curUserName,curUserPhone;
    private int donWeight, donStatus;
    private Donation selectedDonation;
    private User donationOwner;
    private SharedPreferences userPref;
    private ListenerRegistration mDonationRegistration;
    private FirebaseFirestore mFirestore;
    private DocumentReference donationRef;
    private Button acceptBtn,completeBtn,rejectBtn;

    private Menu menu;
    private final int MENU_CANCEL = 799;
    // view related vars
    private TextView titleView, descView, weightView, statusView,donDateView,distanceView;
    private ImageView donPicView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_details_accept);

        getSupportActionBar().setTitle("donation details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        don_id = getIntent().getStringExtra("DON_DOC_NAME");
        donTitle = getIntent().getStringExtra("DON_TITLE");
        donDesc = getIntent().getStringExtra("DON_DESC");
        donPic = getIntent().getStringExtra("DON_PIC");
        donWeight = getIntent().getIntExtra("DON_WEIGHT",0);
        donStatus = getIntent().getIntExtra("DON_STATUS",0);
        userPref = this.getSharedPreferences(getString(R.string.UserPref), 0);
        initFireStore();



        titleView = findViewById(R.id.donTitle_v);
        descView = findViewById(R.id.donDesc_v);
        weightView = findViewById(R.id.donWeight_v);
        donPicView = findViewById(R.id.don_image_v);
        donDateView = findViewById(R.id.donDate_v);
        distanceView = findViewById(R.id.donDist);

        Glide.with(donPicView.getContext())
                .load(donPic)
                .into(donPicView);

        titleView.setText(donTitle);
        descView.setText(donDesc);


        if (donWeight>0) {
            weightView.setText(donWeight + " kgs");
        }
        else {
            weightView.setVisibility(View.INVISIBLE);
        }

        acceptBtn = findViewById(R.id.acceptDonReqBtn);
        completeBtn = findViewById(R.id.logDonBtn);
        rejectBtn = findViewById(R.id.rejectDonBtn);



         if (donStatus==1) {
            acceptBtn.setVisibility(View.VISIBLE);
//            menu.add(0, MENU_CANCEL, Menu.FIRST, "cancel").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }
        else if (donStatus==2) {
             completeBtn.setVisibility(View.VISIBLE);rejectBtn.setVisibility(View.VISIBLE);
        }


        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                 accept the donation here
               // Toast.makeText(getApplicationContext(),"Donation Accepted",Toast.LENGTH_LONG).show();
                final AlertDialog.Builder cBuilder = new AlertDialog.Builder(DonationDetailsAcceptActivity.this);
                cBuilder.setTitle("Confirm Accept");
                cBuilder.setMessage("By clicking Confirm, you agree that you will be picking up the Donations. ");
                cBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        final ProgressDialog sProgressDialog = new ProgressDialog(DonationDetailsAcceptActivity.this);
                        sProgressDialog.setMessage("Just a moment...");
                        sProgressDialog.setTitle("Processing");
                        sProgressDialog.setCancelable(false);
                        sProgressDialog.show();

                        donationRef.update( "acceptedByName", curUserName, "acceptedByPhone", curUserPhone, "status", 2)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(new Intent(DonationDetailsAcceptActivity.this, VolunteerDashboardActivity.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                sProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Failed to submit request. Please try again after some time.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                cBuilder.show();
            }
        });

        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                 accept the donation here
                // Toast.makeText(getApplicationContext(),"Donation Accepted",Toast.LENGTH_LONG).show();
                final AlertDialog.Builder cBuilder = new AlertDialog.Builder(DonationDetailsAcceptActivity.this);
                cBuilder.setTitle("Confirm Complete");
                cBuilder.setMessage("By clicking Confirm, you agree that the donation is received and shall be verified by us.");
                cBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        final ProgressDialog sProgressDialog = new ProgressDialog(DonationDetailsAcceptActivity.this);
                        sProgressDialog.setMessage("Just a moment...");
                        sProgressDialog.setTitle("Processing");
                        sProgressDialog.setCancelable(false);
                        sProgressDialog.show();

                        donationRef.update( "acceptedByName", curUserName, "acceptedByPhone", curUserPhone, "status", 3)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(new Intent(DonationDetailsAcceptActivity.this, VolunteerDashboardActivity.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                sProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Failed to submit request. Please try again after some time.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                cBuilder.show();
            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                 accept the donation here
                // Toast.makeText(getApplicationContext(),"Donation Accepted",Toast.LENGTH_LONG).show();
                final AlertDialog.Builder cBuilder = new AlertDialog.Builder(DonationDetailsAcceptActivity.this);
                cBuilder.setTitle("Confirm Reject");
                cBuilder.setMessage("By clicking Confirm, you agree that you will not be responsible for this donation. ");
                cBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        final ProgressDialog sProgressDialog = new ProgressDialog(DonationDetailsAcceptActivity.this);
                        sProgressDialog.setMessage("Just a moment...");
                        sProgressDialog.setTitle("Processing");
                        sProgressDialog.setCancelable(false);
                        sProgressDialog.show();

                        donationRef.update( "acceptedByName", null, "acceptedByPhone", null, "status", 1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(new Intent(DonationDetailsAcceptActivity.this, VolunteerDashboardActivity.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                sProgressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Failed to submit request. Please try again after some time.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                cBuilder.show();
            }
        });



    }


    private void initFireStore() {
        mFirestore = FirebaseFirestore.getInstance();

        try {
            donationRef = mFirestore.collection("donations").document(don_id);
            curUserPhone = userPref.getString(getString(R.string.p_userphone), "");
            curUserName = userPref.getString(getString(R.string.p_firstname), "");
            String lastName  = userPref.getString(getString(R.string.p_lastname), null);
            if(lastName != null){
                curUserName += " "+lastName;
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
