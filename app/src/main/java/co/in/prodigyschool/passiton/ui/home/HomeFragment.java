package co.in.prodigyschool.passiton.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import co.in.prodigyschool.passiton.Adapters.BookAdapter;
import co.in.prodigyschool.passiton.BookDetailsActivity;
import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.Data.User;
import co.in.prodigyschool.passiton.GetBookPictureActivity;
import co.in.prodigyschool.passiton.R;
import co.in.prodigyschool.passiton.util.Filters;

public class HomeFragment extends Fragment implements BookAdapter.OnBookSelectedListener,FilterDialogFragment.OnFilterSelectionListener {

    private static String TAG = "HOME_FRAGMENT";
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private ViewGroup mEmptyView;
    private BookAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static final int LIMIT = 50;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    FilterDialogFragment mFilterDialog;


//TODO: come back home on pressing back in another activity
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mFilterDialog = new FilterDialogFragment();
        setHasOptionsMenu(true);
        /* recycler view */
        recyclerView = root.findViewById(R.id.home_recycler_view);
        mEmptyView = root.findViewById(R.id.view_empty);
        FloatingActionButton fab = root.findViewById(R.id.fab_home);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startBookPub = new Intent(getActivity(), GetBookPictureActivity.class);
                startBookPub.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startBookPub);
            }
        });
        initFireStore();

        /* use a linear layout manager */
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);


        return root;
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    private void initFireStore() {

        /* firestore */
        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection("books").limit(LIMIT);
        populateBookAdapter();
        removeUserBooks(mQuery);
    }


    private void populateBookAdapter() {

        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }
        // specify an adapter
        mAdapter = new BookAdapter(mQuery, this) {

            @Override
            protected void onDataChanged() {
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
            protected void onError(FirebaseFirestoreException e) {

                Log.e(TAG, "Error: check logs for info.");
            }
        };

    }

    private void removeUserBooks(final Query defaultQuery) {
        final String curUserId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        final ArrayList<String> usersList = new ArrayList<>();
        mFirestore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "onEvent: exception", e);
                    return;
                }
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    String otherUserId = snapshot.toObject(User.class).getPhone();
                    usersList.add(otherUserId);
                }
                usersList.remove(curUserId);
                if(!usersList.isEmpty()) {
                    Query query = defaultQuery.whereIn("userId", usersList).limit(LIMIT);
                    mAdapter.setQuery(query);
                }
                else{
                    //case when only current user has books and no other users present
                    mAdapter.setQuery(null);
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null)
            mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView  searchView = (SearchView)searchItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText == null || newText.trim().isEmpty())
                {
                    mAdapter.setQuery(mQuery);
                    removeUserBooks(mQuery);
                }

                if(!TextUtils.isEmpty(newText))
                    onSearchClicked(newText.toLowerCase());
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }



    private void onSearchClicked(final String queryText) {
        final String curUserId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        final ArrayList<String> booksNameList = new ArrayList<>();
        final Query query = mQuery;
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "onEvent: exception", e);
                    return;
                }
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    String otherUserId = snapshot.toObject(Book.class).getUserId();
                    String bookName = snapshot.toObject(Book.class).getBookName().toLowerCase();
                    if(bookName.contains(queryText) && !curUserId.equalsIgnoreCase(otherUserId)){
                        booksNameList.add(snapshot.toObject(Book.class).getBookName());
                        Log.d(TAG, "onEvent: search: "+bookName);
                    }
                }
                if(!booksNameList.isEmpty()) {
                    Query query1 = query.whereIn("bookName", booksNameList).limit(LIMIT);
                    mAdapter.setQuery(query1);
                }
                else{
                    Query query1 = query.whereEqualTo("bookName",queryText);
                    mAdapter.setQuery(query1);
                    removeUserBooks(query1);
                }


            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.filter) {
            //Intent openFilters = new Intent(HomeActivity.this,FilterActivity.class);
            //startActivity(openFilters);
            //open filter activity to apply and change filters
            onFilterClicked();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFilterClicked() {
        // Show the dialog containing filter options
        Log.d(TAG, "onFilterClicked: menu clicked");
        //mFilterDialog.show(getActivity().getSupportFragmentManager(), FilterDialogFragment.TAG);
        //getChildFragmentManager().beginTransaction().add(HomeFragment.this,FilterDialogFragment.TAG).commit();
        mFilterDialog.show(getChildFragmentManager(),FilterDialogFragment.TAG);
    }



    @Override
    public void onBookSelected(DocumentSnapshot snapshot) {
        String book_id = snapshot.getId();
        Intent bookDetails = new Intent(getActivity(), BookDetailsActivity.class);
        bookDetails.putExtra("bookid", book_id);
        bookDetails.putExtra("isHome",true);
        startActivity(bookDetails);

    }

    @Override
    public void onFilter(Filters filters) {

        Query query  = mFirestore.collection("books");
        if(filters.hasPrice()) {
            query = query.whereEqualTo("bookPrice", 0);
            //query.whereEqualTo("bookPrice",0);
            //query = query.whereEqualTo("textbook",false);
            //Log.d(TAG, "onFilter: home"+filters.getPrice());
        }
        mQuery = query;
        mAdapter.setQuery(query);
        removeUserBooks(query);
    }
}