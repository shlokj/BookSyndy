package co.in.prodigyschool.passiton.ui.bookRequests;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import co.in.prodigyschool.passiton.Adapters.HomeAdapter;
import co.in.prodigyschool.passiton.Adapters.RequestAdapter;
import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.Data.BookRequest;
import co.in.prodigyschool.passiton.R;
import co.in.prodigyschool.passiton.RequestBookActivity;
import co.in.prodigyschool.passiton.RequestDetailsActivity;
import co.in.prodigyschool.passiton.ui.home.FilterDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingRequestFragment extends Fragment implements View.OnClickListener, EventListener<QuerySnapshot> , RequestAdapter.OnRequestSelectedListener {

    public static String TAG = "PENDINGREQUEST";

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private ViewGroup mEmptyView;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private ListView devicesListView;
    private FilterDialogFragment mFilterDialog;
    private RequestAdapter mAdapter;
    private List<BookRequest> bookRequests,bookRequestsFull;
    private ListenerRegistration requestRegistration;
    private String curUserId;
    private SharedPreferences userPref;


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
        View rootView =  inflater.inflate(R.layout.fragment_pending_request, container, false);
        userPref = getContext().getSharedPreferences(getContext().getString(R.string.UserPref),0);
        fab = rootView.findViewById(R.id.fab_request);
        setHasOptionsMenu(true);
        mFilterDialog = new FilterDialogFragment(userPref.getInt(getString(R.string.p_grade),4));
        recyclerView = rootView.findViewById(R.id.request_recycler_view);
        mEmptyView = rootView.findViewById(R.id.view_empty_r);
        bookRequests = new ArrayList<>();
        bookRequestsFull = new ArrayList<>();
        initFireStore();
        fab.setOnClickListener(this);
        mAdapter = new RequestAdapter(getContext(),bookRequests,this) {

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
            curUserId = userPref.getString(getString(R.string.p_userid),"");
            mQuery = mFirestore.collection("bookRequest").whereEqualTo("complete",false);
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
                Log.d(TAG, "populateRequestAdapter: "+bookRequest.getTitle());
                if(bookRequest.getUserId() != null && !bookRequest.getUserId().equalsIgnoreCase(curUserId)){
                    bookRequests.add(bookRequest);
                }
            }

            mAdapter.setRequestList(bookRequests);
            bookRequestsFull.clear();
            bookRequestsFull.addAll(bookRequests);
            mAdapter.onDataChanged();
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
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                filterItem.setVisible(false);
                return true;
            }
        });

        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(!mFilterDialog.isAdded())
                    mFilterDialog.show(getChildFragmentManager(), FilterDialogFragment.TAG);
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

        super.onCreateOptionsMenu(menu, inflater);
    }


}
