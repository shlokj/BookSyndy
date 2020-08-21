package com.booksyndy.academics.android.ui.volunteerDashboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.booksyndy.academics.android.Adapters.DonationAdapter;
import com.booksyndy.academics.android.Adapters.RequestAdapter;
import com.booksyndy.academics.android.Data.Donation;
import com.booksyndy.academics.android.Data.OnFilterSelectionListener;
import com.booksyndy.academics.android.DonationDetailsAcceptActivity;
import com.booksyndy.academics.android.MyDonationDetailsActivity;
import com.booksyndy.academics.android.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingFragment extends Fragment implements  DonationAdapter.OnDonationSelectedListener, DonationAdapter.OnDonationLongSelectedListener {



    private static String TAG = "VOLUNTEER_DASHBOARD_PENDING";

    private RecyclerView recyclerView;
    private ViewGroup mEmptyView;
    private RecyclerView.LayoutManager layoutManager;
    private DonationAdapter mDonAdapter;
    private ArrayAdapter<String> optionsList;
    private FirestoreRecyclerOptions<Donation> options;

    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;
    private FirebaseFirestore mFirestore;
    private Query mQuery;


    public PendingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_volunteer, container, false);

        recyclerView = rootView.findViewById(R.id.volunteer_recycler_view);
        mEmptyView = rootView.findViewById(R.id.view_empty_v);
        initFirestore();
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mDonAdapter);
        try {
            mDonAdapter.startListening();
        }
        catch (Exception e) {
//            Toast.makeText(this, "mDonAdapter null", Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }


    private void initFirestore() {
        try {
            /* firestore */
            mFirestore = FirebaseFirestore.getInstance();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            mQuery = mFirestore.collection("donations").whereEqualTo("status", 1);
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
            return;
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
    public void onDonationSelected(DocumentSnapshot snapshot) {
        String don_id = snapshot.getId();

        Donation curDonation = snapshot.toObject(Donation.class);

        Intent donDetails = new Intent(getActivity(), DonationDetailsAcceptActivity.class);
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
    }
}
