package com.booksyndy.academics.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.booksyndy.academics.android.Adapters.DonationAdapter;
import com.booksyndy.academics.android.Data.Donation;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MyDonationsActivity extends AppCompatActivity implements DonationAdapter.OnDonationSelectedListener, DonationAdapter.OnDonationLongSelectedListener {

    public static String TAG = "MY_DONATION_ACTIVITY";

    private RecyclerView recyclerView;
    private ViewGroup mEmptyView;
    private RecyclerView.LayoutManager layoutManager;
    private DonationAdapter mDonAdapter;
    private ArrayAdapter<String> optionsList;
    private FirestoreRecyclerOptions<Donation> options;

    private FloatingActionButton donateFab;

    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private int volStat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_donations);

        getSupportActionBar().setTitle("Your donations");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userPref = this.getSharedPreferences(getString(R.string.UserPref), 0);

        volStat = userPref.getInt(getString(R.string.p_uservolstatus),0);

        Toast.makeText(this, volStat+"", Toast.LENGTH_SHORT).show();
        recyclerView = findViewById(R.id.donation_recycler_view);
        mEmptyView = findViewById(R.id.view_empty_d);
        optionsList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        initFirestore();

        donateFab = findViewById(R.id.fab_donate);

        donateFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyDonationsActivity.this, CreateBundleListingActivity.class));
            }
        });

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mDonAdapter);
        try {
            mDonAdapter.startListening();
        }
        catch (Exception e) {
//            Toast.makeText(this, "mDonAdapter null", Toast.LENGTH_SHORT).show();
        }

        showcaseViews();

    }

    private void showcaseViews() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(200); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "Home showcase");

        sequence.setConfig(config);

        sequence.addSequenceItem(donateFab,
                "Welcome to the Donate section!\n\nTap here to list a bundle of books for donation.", "GOT IT");

        sequence.start();
    }

    @Override
    public void onBackPressed() {
        setContentView(R.layout.fragment_donate_loading);
        Intent goHome = new Intent(MyDonationsActivity.this, HomeActivity.class);
        goHome.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(goHome);
    }

    private void initFirestore() {
        try {
            /* firestore */
            mFirestore = FirebaseFirestore.getInstance();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            mQuery = mFirestore.collection("donations").whereEqualTo("userId", userId).whereGreaterThanOrEqualTo("status", 1);
            populateDonationAdapter();
/*            if (mDonAdapter==null) {
                Toast.makeText(this, "mDonAdapter null", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "mDonAdapter not null", Toast.LENGTH_SHORT).show();
            }*/

        } catch (Exception e) {
            Log.e(TAG, "initFireStore: ", e);
        }

    }


    private void populateDonationAdapter() {

        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }
        mQuery = mQuery.orderBy("status");
        mQuery = mQuery.orderBy("createdAt", Query.Direction.DESCENDING);

        options = new FirestoreRecyclerOptions.Builder<Donation>()
                .setQuery(mQuery, Donation.class)
                .build();
        // specify an adapter
        mDonAdapter = new DonationAdapter(options, this, this) {

            @Override
            public void onDataChanged() {
                super.onDataChanged();

                if (getItemCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(FirebaseFirestoreException e) {

                Log.e(TAG, "Error: check logs for info.");
            }
        };

/*        if (mDonAdapter==null) {
            Toast.makeText(this, "mDonAdapter null", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "mDonAdapter not null", Toast.LENGTH_SHORT).show();
        }*/


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onDonationSelected(DocumentSnapshot snapshot) {
       String don_id = snapshot.getId();

       Donation curDonation = snapshot.toObject(Donation.class);

       Intent donDetails = new Intent(MyDonationsActivity.this, MyDonationDetailsActivity.class);
       donDetails.putExtra("DON_DOC_NAME",don_id);
       try {
           donDetails.putExtra("DON_WEIGHT", curDonation.getApproxWeight());
       }
       catch (Exception exc) {
           donDetails.putExtra("DON_WEIGHT", 0);
       }

       donDetails.putExtra("DON_TITLE",curDonation.getDonationName());
       donDetails.putExtra("DON_DESC",curDonation.getDonationDescription());
       donDetails.putExtra("DON_PIC",curDonation.getDonationPhoto());
       donDetails.putExtra("DON_STATUS",curDonation.getStatus());

       startActivity(donDetails);

    }

    @Override
    public void onDonationLongSelected(final DocumentSnapshot snapshot) {

//        String don_id = snapshot.getId();

        final CharSequence[] options = {"Cancel donation"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MyDonationsActivity.this);
//                builder.setTitle("Select Pic Using...");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Cancel donation")) {
                    AlertDialog.Builder cBuilder = new AlertDialog.Builder(MyDonationsActivity.this);
                    cBuilder.setTitle("Cancel donation?");
                    cBuilder.setMessage("Are you sure you want to cancel your donation?");
                    cBuilder.setNegativeButton("Yes, cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //TODO: set don doc status to -1
                            DocumentReference donRef = mFirestore.collection("donations").document(snapshot.getId());

                            donRef.update("status",-1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MyDonationsActivity.this, "Your donation request has been cancelled. We hope to see you back with some book soon!", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MyDonationsActivity.this, "Oops, couldn't cancel your donation. Please try again later.", Toast.LENGTH_LONG).show();
                                }
                            });


                        }
                    });
                    cBuilder.setPositiveButton("No, don't cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    });
                }
            }
        });
        builder.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (volStat==1 || volStat==2) {
            getMenuInflater().inflate(R.menu.menu_donation_volreg, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.menu_donation, menu);
        }
//        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.becomeVolunteer:
                startActivity(new Intent(MyDonationsActivity.this,VolunteerWelcomeActivity.class));
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.openVolDash:
                startActivity(new Intent(MyDonationsActivity.this,VolunteerDashboardActivity.class));
                break;
        }
        return true;
    }

}
