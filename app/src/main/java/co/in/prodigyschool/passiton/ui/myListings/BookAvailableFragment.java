package co.in.prodigyschool.passiton.ui.myListings;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import co.in.prodigyschool.passiton.Adapters.BookAdapter;
import co.in.prodigyschool.passiton.BookDetailsActivity;
import co.in.prodigyschool.passiton.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookAvailableFragment extends Fragment implements BookAdapter.OnBookSelectedListener,BookAdapter.OnBookLongSelectedListener{


    public static String TAG = "BOOK_AVAILABLE_FRAGMENT";

    private RecyclerView recyclerView;
    private ViewGroup mEmptyView;
    private RecyclerView.LayoutManager layoutManager;
    private BookAdapter mAdapter;
    private static final int LIMIT = 50;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private GalleryViewModel galleryViewModel;
    private ArrayAdapter<String> optionsList;


    public BookAvailableFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View root = inflater.inflate(R.layout.fragment_home, container, false);

        /* recycler view */
        recyclerView = root.findViewById(R.id.home_recycler_view);
        mEmptyView = root.findViewById(R.id.view_empty);
        root.findViewById(R.id.fab_home).setVisibility(View.GONE);
        initFireStore();

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
            mQuery = mFirestore.collection("books").whereEqualTo("userId", userId).whereEqualTo("bookSold",false).limit(LIMIT);
            populateBookAdapter();

        } catch (Exception e) {
            Log.e(TAG, "initFireStore: ", e);
        }

    }

    private void populateBookAdapter() {

        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }
        // specify an adapter
        mAdapter = new BookAdapter(mQuery, this,this) {


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

        @Override
        public void onBookSelected(DocumentSnapshot snapshot) {
            String book_id = snapshot.getId();
            Intent bookDetails = new Intent(getActivity(), BookDetailsActivity.class);
            bookDetails.putExtra("bookid", book_id);
            bookDetails.putExtra("isHome",false);
            startActivity(bookDetails);
        }


    private void displayOptions(){
        optionsList.clear();
        optionsList.add("Delete");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.dialog_longpress_mylistings_options, null);
        alertDialog.setView(convertView);
//        alertDialog.setTitle("Select your device");
        alertDialog.setCancelable(true);
        ListView devicesListView = (ListView) convertView.findViewById(R.id.optionsListView);
        devicesListView.setAdapter(optionsList);
        final AlertDialog dialog = alertDialog.show();

        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String opt = ((TextView) view).getText().toString();
                if (opt.equals("Delete")) {
                    // move the selected book to completed
                }
            }
        });
    }

    @Override
    public void onBookLongSelected(DocumentSnapshot snapshot) {
        Toast.makeText(getContext(),"clicked",Toast.LENGTH_SHORT).show();
    }
}
