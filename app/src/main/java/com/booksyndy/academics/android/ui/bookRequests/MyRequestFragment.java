package com.booksyndy.academics.android.ui.bookRequests;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.booksyndy.academics.android.Adapters.RequestAdapter;
import com.booksyndy.academics.android.Data.BookRequest;
import com.booksyndy.academics.android.R;
import com.booksyndy.academics.android.RequestDetailsActivity;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyRequestFragment extends Fragment implements EventListener<QuerySnapshot> , RequestAdapter.OnRequestSelectedListener  {

    public static String TAG = "MYREQUEST";

    private RecyclerView recyclerView;
    private ViewGroup mEmptyView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private ListView devicesListView;
    private RequestAdapter mAdapter;
    private List<BookRequest> bookRequests,bookRequestsFull;
    private ListenerRegistration requestRegistration;
    private String curUserId;
    private SharedPreferences userPref;

    public MyRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_pending_request, container, false);
        userPref = getContext().getSharedPreferences(getContext().getString(R.string.UserPref),0);
        rootView.findViewById(R.id.fab_request).setVisibility(View.GONE);
        recyclerView = rootView.findViewById(R.id.request_recycler_view);
        mEmptyView = rootView.findViewById(R.id.view_empty_r);
        bookRequests = new ArrayList<>();
        bookRequestsFull = new ArrayList<>();
        initFireStore();
        mAdapter = new RequestAdapter(getContext(),bookRequests,this,"MRF") {

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
        };
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (requestRegistration == null) {
            bookRequests.clear();
            bookRequestsFull.clear();
            requestRegistration = mQuery.addSnapshotListener(this);
        } else {
            bookRequests.clear();
            bookRequestsFull.clear();
            requestRegistration.remove();
            requestRegistration = null;
            requestRegistration = mQuery.addSnapshotListener(this);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.onDataChanged();
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (requestRegistration != null) {
            requestRegistration.remove();
            requestRegistration = null;
            mAdapter.notifyDataSetChanged();
            mAdapter.onDataChanged();

        }


    }

    private void initFireStore() {
        try {
            /* firestore */
            mFirestore = FirebaseFirestore.getInstance();
            curUserId = userPref.getString(getString(R.string.p_userphone), "");
            mQuery = mFirestore.collection("bookRequest").whereEqualTo("complete",false);
            if(curUserId != null){
                mQuery = mQuery.whereEqualTo("phone", curUserId);
            }
            requestRegistration = mQuery.addSnapshotListener(this);
        } catch (Exception e) {
            Log.e(TAG, "initFireStore: ", e);
        }

    }

    private void populateRequestAdapter(QuerySnapshot queryDocumentSnapshots) {

        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
            bookRequests.clear();
            for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                BookRequest bookRequest = snapshot.toObject(BookRequest.class);
                bookRequests.add(bookRequest);
            }

            mAdapter.setRequestList(bookRequests);
            bookRequestsFull.clear();
            bookRequestsFull.addAll(bookRequests);
        }
    }



    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.d(TAG, "onEvent: chats error", e);
            return;
        }
        populateRequestAdapter(queryDocumentSnapshots);
    }

    @Override
    public void onRequestSelected(BookRequest request) {
        if(request != null){

            Intent requestDetails = new Intent(getActivity(), RequestDetailsActivity.class);
            requestDetails.putExtra("bookid",request.getDocumentId());
            requestDetails.putExtra("REQ_TITLE",request.getTitle());
            requestDetails.putExtra("REQ_DESC",request.getDescription());
            requestDetails.putExtra("REQ_ISTB",request.isText());
            requestDetails.putExtra("REQ_GRADENUMBER",request.getGrade());
            requestDetails.putExtra("REQ_BOARDNUMBER",request.getBoard());
            requestDetails.putExtra("REQ_YEAR",request.getBookYear());
            requestDetails.putExtra("REQ_ADDRESS",request.getBookAddress());
            requestDetails.putExtra("byme",true);
            requestDetails.putExtra("REQ_PHONE", request.getPhone());
            requestDetails.putExtra("REQ_LAT", request.getLat());
            requestDetails.putExtra("REQ_LNG", request.getLng());
            startActivity(requestDetails);

        }
    }

}
