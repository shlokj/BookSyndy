package com.booksyndy.academics.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class MyDonationDetailsActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    private static final String TAG = "DONATION_DETAILS";

    private String don_id, donTitle, donDesc, donPic;
    private int donWeight, donStatus;
    private Donation selectedDonation;
    private User donationOwner;
    private ListenerRegistration mDonationRegistration;
    private FirebaseFirestore mFirestore;
    private DocumentReference donationRef;

    private Menu menu;
    private final int MENU_CANCEL = 789;
    // view related vars
    private TextView titleView, descView, weightView, statusView;
    private ImageView donPicView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_donation_details);

        getSupportActionBar().setTitle("Your donation details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        don_id = getIntent().getStringExtra("DON_DOC_NAME");
        donTitle = getIntent().getStringExtra("DON_TITLE");
        donDesc = getIntent().getStringExtra("DON_DESC");
        donPic = getIntent().getStringExtra("DON_PIC");
        donWeight = getIntent().getIntExtra("DON_WEIGHT",0);
        donStatus = getIntent().getIntExtra("DON_STATUS",0);

        initFireStore();

        titleView = findViewById(R.id.donation_name);
        descView = findViewById(R.id.donDescriptionTV);
        weightView = findViewById(R.id.don_weight);
        statusView = findViewById(R.id.statusTV);
        donPicView = findViewById(R.id.don_image);

        statusView.setText("Status: ");

        Glide.with(donPicView.getContext())
                .load(donPic)
                .into(donPicView);

        titleView.setText(donTitle);
        descView.setText(donDesc);

        if (donStatus==0) {
            statusView.append("Incomplete/rejected");
//            menu.add(0, MENU_CANCEL, Menu.FIRST, "cancel").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        }
        else if (donStatus==1) {
            statusView.append("Submitted, waiting for acceptance");
//            menu.add(0, MENU_CANCEL, Menu.FIRST, "cancel").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }
        else if (donStatus==2) {
            statusView.append("Accepted by volunteer");
        }
        else if (donStatus==3) {
            statusView.append("Completed and received");
        }
        else {
            statusView.setText("");
        }

        if (donWeight>0) {
            weightView.setText(donWeight + " kgs");
        }
        else {
            weightView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (!(donStatus==0 || donStatus==1)) {
            menu.clear();
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_donation_details, menu);
        this.menu = menu;

        return true;
    }

    private void initFireStore() {
        mFirestore = FirebaseFirestore.getInstance();

        try {
            donationRef = mFirestore.collection("donations").document(don_id);
            mDonationRegistration = donationRef.addSnapshotListener(this);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Firebase error", Toast.LENGTH_SHORT).show();
        }
    }

    /*
                 mDonationRegistration
     */
    @Override
    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "donation:onEvent", e);
            return;
        }
        if (snapshot != null && snapshot.exists())
            populateDonationDetails(snapshot.toObject(Donation.class));
        else {
            Toast.makeText(getApplicationContext(), "Donation completed or unavailable", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    private void populateDonationDetails(Donation donation){
        if(donation == null){
            Log.d(TAG, "populateDonationDetails: param error");
            return;
        }
        titleView.setText(donation.getDonationName());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cancelDonation:
                AlertDialog.Builder cBuilder = new AlertDialog.Builder(MyDonationDetailsActivity.this);
                cBuilder.setTitle("Cancel donation request");
                cBuilder.setMessage("Are you sure you want to cancel this donation request?");
                cBuilder.setPositiveButton("No, don't cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                cBuilder.setNegativeButton("Yes, cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cancelDonationStatus0();
                    }
                });
                cBuilder.show();

        }

        return true;
    }

    public void cancelDonationStatus0() {

        final ProgressDialog sProgressDialog = new ProgressDialog(MyDonationDetailsActivity.this);
        sProgressDialog.setMessage("We hope to see you back soon!");
        sProgressDialog.setTitle("Cancelling");
        sProgressDialog.setCancelable(false);
        sProgressDialog.show();

        DocumentReference donReference = mFirestore.collection("donations").document(don_id);

        donReference.update("status", 0)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(MyDonationDetailsActivity.this, MyDonationsActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Failed to cancel. Please try again after some time.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
