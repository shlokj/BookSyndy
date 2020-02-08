package co.in.prodigyschool.passiton.ui.myListings;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import co.in.prodigyschool.passiton.Adapters.BookAdapter;
import co.in.prodigyschool.passiton.BookDetailsActivity;
import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookSoldFragment extends Fragment implements BookAdapter.OnBookSelectedListener, BookAdapter.OnBookLongSelectedListener {

    public static String TAG = "BOOK_SOLD_FRAGMENT";

    private RecyclerView recyclerView;
    private ViewGroup mEmptyView;
    private RecyclerView.LayoutManager layoutManager;
    private BookAdapter mAdapter;
    private static final int LIMIT = 50;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private GalleryViewModel galleryViewModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String book_id;
    private ListView optionsListView;
    private AlertDialog dialog;
    private ArrayAdapter<String> optionsList;
    private FirestoreRecyclerOptions<Book> options;


    public BookSoldFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        /* recycler view */
        recyclerView = root.findViewById(R.id.home_recycler_view);
        mEmptyView = root.findViewById(R.id.view_empty);
        root.findViewById(R.id.fab_home).setVisibility(View.GONE);
        initFireStore();

        optionsList = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1);

        mSwipeRefreshLayout = root.findViewById(R.id.swiperefreshhome);
//        mSwipeRefreshLayout.setRefreshing(false);
//        mSwipeRefreshLayout.setEnabled(true);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //code to reload with new books
                if (mAdapter != null && options != null) {
                    mAdapter.updateOptions(options);
                }
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }

        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 150);

        /* use a linear layout manager */
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null)
            mAdapter.startListening();


    }


    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null)
            mAdapter.onDataChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }

    }

    private void initFireStore() {
        try {
            /* firestore */
            mFirestore = FirebaseFirestore.getInstance();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            mQuery = mFirestore.collection("books").whereEqualTo("userId", userId).whereEqualTo("bookSold", true);
            populateBookAdapter();

        } catch (Exception e) {
            Log.e(TAG, "initFireStore: ", e);
        }

    }

    private void populateBookAdapter() {

        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }
        mQuery = mQuery.orderBy("bookTime", Query.Direction.DESCENDING);
        options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(mQuery, Book.class)
                .build();
        // specify an adapter
        mAdapter = new BookAdapter(options, this, this) {


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


    }

    @Override
    public void onBookSelected(DocumentSnapshot snapshot) {
        book_id = snapshot.getId();
        Intent bookDetails = new Intent(getActivity(), BookDetailsActivity.class);
        bookDetails.putExtra("bookid", book_id);
        bookDetails.putExtra("isHome",false);
        startActivity(bookDetails);
    }

    private void displayOptions(){
        optionsList.clear();
        optionsList.add("Mark as unsold");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.dialog_longpress_mylistings_options, null);
        alertDialog.setView(convertView);
//        alertDialog.setTitle("Select your device");
        alertDialog.setCancelable(true);
        optionsListView = (ListView) convertView.findViewById(R.id.optionsListView);
        optionsListView.setAdapter(optionsList);
        dialog = alertDialog.show();
        dialog.show();
    }

    @Override
    public void onBookLongSelected(final DocumentSnapshot snapshot) {
        book_id = snapshot.getId();
        displayOptions();
        optionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String opt = ((TextView) view).getText().toString();
                if (opt.equals("Mark as unsold")) {
                    // move the selected book to completed
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                    mBuilder.setTitle("Mark as unsold?");
                    mBuilder.setMessage("Your listing will be visible to others and will be moved to the 'Ongoing' tab.");
                    mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // markAsSold(false);
                            // mAdapter.markAsUnsold(snapshot);
                            markAsSold(snapshot);
                            mSwipeRefreshLayout.post(new Runnable() {
                                @Override
                                public void run() {
                                    mSwipeRefreshLayout.setRefreshing(true);
                                }
                            });
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                }
                            }, 150);
//                            Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Marked as unsold", Snackbar.LENGTH_LONG);
//                            snackbar.show();
                        }
                    });
                    mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    mBuilder.show();
                    dialog.dismiss();
                }
            }
        });
    }


    private void markAsSold(DocumentSnapshot snapshot) {

        mAdapter.markAsUnsold(snapshot);
        mAdapter.updateOptions(options);

    }
}
