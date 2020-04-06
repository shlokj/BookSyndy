package com.booksyndy.academics.android.ui.bookRequests;


import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.booksyndy.academics.android.Adapters.RequestAdapter;
import com.booksyndy.academics.android.Data.BookRequest;
import com.booksyndy.academics.android.Data.OnFilterSelectionListener;
import com.booksyndy.academics.android.R;
import com.booksyndy.academics.android.RequestBookActivity;
import com.booksyndy.academics.android.RequestDetailsActivity;
import com.booksyndy.academics.android.util.Filters;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingRequestFragment extends Fragment implements View.OnClickListener, EventListener<QuerySnapshot> , RequestAdapter.OnRequestSelectedListener , OnFilterSelectionListener {

    public static String TAG = "PENDINGREQUEST";

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private ViewGroup mEmptyView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private ListView devicesListView;
    private FilterDialogFragmentRequest mFilterDialog;
    private RequestAdapter mAdapter;
    private List<BookRequest> bookRequests,bookRequestsFull;
    private ListenerRegistration requestRegistration;
    private String curUserId;
    private SharedPreferences userPref;
    private ReqViewModel reqViewModel;
    private double userLat,userLng;
    private SimpleDateFormat dateFormat;

    /* search *

     */

    private MenuItem filterItem;



    public PendingRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        reqViewModel = ViewModelProviders.of(this).get(ReqViewModel.class);
        dateFormat =  new SimpleDateFormat("dd MM yyyy HH", Locale.getDefault());
        View rootView =  inflater.inflate(R.layout.fragment_pending_request, container, false);
        userPref = getContext().getSharedPreferences(getContext().getString(R.string.UserPref),0);
        userLat = userPref.getFloat(getString(R.string.p_lat),0.0f);
        userLng = userPref.getFloat(getString(R.string.p_lng),0.0f);
        fab = rootView.findViewById(R.id.fab_request);
        setHasOptionsMenu(true);
        mFilterDialog = new FilterDialogFragmentRequest(userPref.getInt(getString(R.string.p_grade),4));
        recyclerView = rootView.findViewById(R.id.request_recycler_view);
        mEmptyView = rootView.findViewById(R.id.view_empty_r);
        bookRequests = new ArrayList<>();
        bookRequestsFull = new ArrayList<>();
        initFireStore();
        fab.setOnClickListener(this);
        mAdapter = new RequestAdapter(getContext(),bookRequests,this,"PRF") {

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
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (requestRegistration != null) {
            requestRegistration.remove();
            requestRegistration = null;
            mAdapter.onDataChanged();
        }


    }

    private void initFireStore() {
        try {
            /* firestore */
            mFirestore = FirebaseFirestore.getInstance();
            curUserId = userPref.getString(getString(R.string.p_userphone), "");
            mQuery = mFirestore.collection("bookRequest")
                    .whereEqualTo("complete",false)
                    .orderBy("createdAt", Query.Direction.DESCENDING);
            requestRegistration = mQuery.addSnapshotListener(this);
        } catch (Exception e) {
            Log.e(TAG, "initFireStore: ", e);
        }

    }

    private void populateRequestAdapter(QuerySnapshot queryDocumentSnapshots) {

        if(!queryDocumentSnapshots.isEmpty()) {
            bookRequests.clear();
            for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                BookRequest bookRequest = snapshot.toObject(BookRequest.class);
                Log.d(TAG, "populateRequestAdapter: "+bookRequest.getCreatedAt());
                if (bookRequest.getUserId() != null && !bookRequest.getPhone().equalsIgnoreCase(curUserId)) {
                    bookRequests.add(bookRequest);
                }
            }

            mAdapter.setRequestList(bookRequests);
            bookRequestsFull.clear();
            bookRequestsFull.addAll(bookRequests);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab_request){
            Intent newBookRequest = new Intent(getActivity(), RequestBookActivity.class);
            startActivity(newBookRequest);
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
            requestDetails.putExtra("REQ_BOARDNUMBER",request.getBoard());
            requestDetails.putExtra("REQ_YEAR",request.getBookYear());
            requestDetails.putExtra("REQ_GRADENUMBER",request.getGrade());
            requestDetails.putExtra("REQ_ADDRESS",request.getBookAddress());
            requestDetails.putExtra("REQ_PHONE", request.getPhone());
            requestDetails.putExtra("REQ_LAT", request.getLat());
            requestDetails.putExtra("REQ_LNG", request.getLng());
            startActivity(requestDetails);

        }
    }

    /*search

     */


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        filterItem = menu.findItem(R.id.filter);
//        menu.findItem(R.id.open_chats).setVisible(false);


        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(!mFilterDialog.isAdded())
                    mFilterDialog.show(getChildFragmentManager(), FilterDialogFragmentRequest.TAG);
                return true;
            }
        });
        final SearchView searchView = (SearchView)searchItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onQueryTextChange(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<BookRequest> filteredList1 = new ArrayList<>();
                if(newText == null || newText.trim().isEmpty())
                {
                    filteredList1.addAll(bookRequestsFull);
                }
                else{

                    String filterPattern = newText.toLowerCase().trim();
                    String[] strgs = filterPattern.split("\\W+");
                    for (BookRequest book : filteredList1) {
                        int foundIndex = book.getTitle().toLowerCase().indexOf(filterPattern);
//                        Toast.makeText(getActivity(),"Found at "+foundIndex,Toast.LENGTH_SHORT).show();
                        if (book.getTitle().toLowerCase().contains(filterPattern)) {
                            if (foundIndex != -1 && (foundIndex == 0 || book.getTitle().substring(foundIndex - 1, foundIndex).equals(" ")) && !filteredList1.contains(book)) {
                                filteredList1.add(book);
                            }
                        }
                    }
/*                    for (Book book : filteredList1) {
                        for (String s:strgs) {
                            int foundIndex = book.getBookName().toLowerCase().indexOf(s);
//                        Toast.makeText(getActivity(),"Found at "+foundIndex,Toast.LENGTH_SHORT).show();
                            if (book.getBookName().toLowerCase().contains(s)) {
                                if (foundIndex != -1 && (foundIndex == 0 || book.getBookName().substring(foundIndex - 1, foundIndex).equals(" ")) && !filteredList1.contains(book)) {
                                    filteredList1.add(book);
                                }
                            }
                        }
                    }*/
                    for (BookRequest book : bookRequestsFull) {
                        int foundIndex = book.getTitle().toLowerCase().indexOf(filterPattern);
                        if (book.getTitle().toLowerCase().contains(filterPattern)) {
                            if (foundIndex != -1 && (foundIndex == 0 || book.getTitle().substring(foundIndex - 1, foundIndex).equals(" ")) &&  !filteredList1.contains(book)) {
                                filteredList1.add(book);
                            }
                        }
                    }
/*                    for (String s:strgs) {
                        for (Book book : bookListFull) {
                            int foundIndex = book.getBookName().toLowerCase().indexOf(s);
                            if (book.getBookName().toLowerCase().contains(s)) {
                                if (foundIndex != -1 && (foundIndex == 0 || book.getBookName().substring(foundIndex - 1, foundIndex).equals(" ")) &&  !filteredList1.contains(book)) {
                                    filteredList1.add(book);
                                }
                            }
                        }
                    }*/
                }
                mAdapter.setRequestList(filteredList1);

                return false;
            }
        });

        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                searchView.onActionViewExpanded();
                searchView.requestFocus();
                return true;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onFilter(Filters filters) {

        List<BookRequest> filteredList = new ArrayList<>();
        List<BookRequest> toBeRemoved = new ArrayList<>();

        boolean noFilter = true;

        // add all books to filtered list to start with
        filteredList.addAll(bookRequestsFull);

        // then, remove books that don't satisfy the provided criteria


        if (!(filters.IsText() && filters.IsNotes())) {
            if (filters.IsText()) {
                for (BookRequest book : filteredList) {
                    if (!book.isText())
                        toBeRemoved.add(book);
                }
            }
            else if (filters.IsNotes()){
                for (BookRequest book : filteredList) {
                    if (book.isText())
                        toBeRemoved.add(book);
                }
            }
            noFilter = false;
        }


        for (BookRequest b:toBeRemoved) {
            filteredList.remove(b);
        }

        toBeRemoved.clear();

        List<Integer> unrequiredBoards = new ArrayList<>();

        if (filters.hasBookBoard()) {


            for (int i = 1; i <= 16; i++) {
                if (!filters.getBookBoard().contains(i)) {
                    unrequiredBoards.add(i);
                }
            }

            if (!filters.getBookBoard().contains(20)) {
                unrequiredBoards.add(20);
            }
            for (int a = 0; a < filteredList.size(); a++) {
                for (Integer i : unrequiredBoards) {
                    if (filteredList.get(a).getBoard() == i) {
                        toBeRemoved.add(filteredList.get(a));
                    }
                }
            }
            noFilter = false;
        }

        else {

            // remove competitive exam books from the list as we don't want them by default
            for (int a = 0; a < filteredList.size(); a++) {
                if (filteredList.get(a).getBoard() == 20) {
                    toBeRemoved.add(filteredList.get(a));
                }
            }
        }


        for (BookRequest b:toBeRemoved) {
            filteredList.remove(b);
        }

        toBeRemoved.clear();

        if (filters.hasBookGrade()) {

            List<Integer> unrequiredGrades = new ArrayList<>();

            for (int i = 1; i <= 7; i++) {
                if (!filters.getBookGrade().contains(i)) {
                    unrequiredGrades.add(i);
                }
            }


            for (int a = 0; a < filteredList.size(); a++) {
                for (Integer i : unrequiredGrades) {
                    if (filteredList.get(a).getGrade() == i) {
                        toBeRemoved.add(filteredList.get(a));
                    }
                }
            }
            noFilter = false;
        }

        for (BookRequest b:toBeRemoved) {
            filteredList.remove(b);
        }

        toBeRemoved.clear();


        if(filters.hasBookDistance()){
            if(userLat != 0.0 && userLng != 0.0) {
                for (BookRequest b : filteredList) {
                    if (!getBookUnderDistance(b, filters.getBookDistance(), userLat, userLng)) {
                        toBeRemoved.add(b);
                    }
                }
                noFilter = false;
            }
            else {
                filters.setBookDistance(20);
            }
        }

        for (BookRequest b:toBeRemoved) {
            filteredList.remove(b);
        }

        toBeRemoved.clear();


        if(filters.hasSortBy()){

            if(filters.getSortBy().equalsIgnoreCase("time")){
                //sort by book duration
                Collections.sort(filteredList, new Comparator<BookRequest>() {
                    @Override
                    public int compare(BookRequest o1, BookRequest o2) {
                        try {
                            Date d1 = dateFormat.parse(o1.getTime());
                            Date d2 = dateFormat.parse(o2.getTime());
                            if(d1.before(d2)){
                                return 1;
                            }
                            else{
                                return -1;
                            }

                        }
                        catch (Exception e){
                            return 0;
                        }
                    }
                });

            }
            else{
                //sort by book distance

                if(userLat != 0.0 && userLng != 0.0) {

                    Collections.sort(filteredList, new Comparator<BookRequest>() {
                        @Override
                        public int compare(BookRequest o1, BookRequest o2) {
                            Location userLocation = new Location("point A");
                            userLocation.setLatitude(userLat);
                            userLocation.setLongitude(userLng);

                            Location B1 = new Location("point B");
                            B1.setLongitude(o1.getLng());
                            B1.setLatitude(o1.getLat());

                            Location B2 = new Location("point C");
                            B2.setLongitude(o2.getLng());
                            B2.setLatitude(o2.getLat());


                            if(B1.distanceTo(userLocation) > B2.distanceTo(userLocation)){
                                return 1;
                            }
                            else{
                                return -1;
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getContext(),"Error Fetching Location",Toast.LENGTH_SHORT).show();
                    filters.setSortBy("Relevance");
                }

            }

        }



        if(filteredList == null || filteredList.isEmpty()){
            if(noFilter){
                Log.d(TAG, "onFilter: is empty");
                filteredList.addAll(bookRequests);
                bookRequestsFull.clear();
                bookRequests.addAll(bookRequests);
            }
            else{
                bookRequestsFull.clear();
                bookRequestsFull.addAll(bookRequests);
            }
        }
        mAdapter.setRequestList(filteredList);
        reqViewModel.setFilters(filters);
    }


    private  boolean getBookUnderDistance(BookRequest book,int distance, double latitude, double longitude){
        float res;
        if(book.getLat() != 0.0 && book.getLng() != 0.0 ) {
            Location locationA = new Location("point A");
            Location locationB = new Location("point B");

            locationA.setLatitude(book.getLat());
            locationA.setLongitude(book.getLng());
            locationB.setLatitude(latitude);
            locationB.setLongitude(longitude);
            res = (locationA.distanceTo(locationB) / 1000);
            return (res <= distance);
        }

        return false;
    }
}
