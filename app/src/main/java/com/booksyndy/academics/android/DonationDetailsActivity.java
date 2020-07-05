package com.booksyndy.academics.android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.booksyndy.academics.android.Data.Book;
import com.booksyndy.academics.android.Data.Donation;
import com.booksyndy.academics.android.Data.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class DonationDetailsActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    private static final String TAG = "DONATION_DETAILS";

    private String don_id;
    private Donation selectedDonation;
    private User donationOwner;
    private ListenerRegistration mDonationRegistration;
    private FirebaseFirestore mFirestore;
    private DocumentReference donationRef;

    // view related vars
    private TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_details);
        don_id = getIntent().getStringExtra("DON_DOC_NAME");
        initFireStore();

        titleView = findViewById(R.id.tv_title_d);

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
            Toast.makeText(getApplicationContext(), "Donation Completed or unavailable", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    private void populateDonationDetails(Donation donation){
        if(donation == null ){
            Log.d(TAG, "populateDonationDetails: param error");
            return;
        }

        titleView.setText(donation.getDonationName());

    }
}
