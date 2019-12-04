package co.in.prodigyschool.passiton.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import co.in.prodigyschool.passiton.Adapters.BookAdapter;
import co.in.prodigyschool.passiton.BookDetailsActivity;
import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.R;

public class HomeFragment extends Fragment implements BookAdapter.OnBookSelectedListener {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private ViewGroup  mEmptyView ;
    private BookAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static final  int LIMIT = 50;
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        /* recycler view */
        recyclerView =  root.findViewById(R.id.home_recycler_view);
        mEmptyView = root.findViewById(R.id.view_empty);


        initFireStore();

        if (mQuery == null) {
            Log.w("recycler View", "No query, not initializing RecyclerView");
        }
        // specify an adapter
        mAdapter = new BookAdapter(mQuery, this) {


            @Override
            protected void onDataChanged() {
                super.onDataChanged();
                if(getItemCount() ==0){
                    recyclerView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                }
                else{
                    recyclerView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {

                Log.e("recycler View", "Error: check logs for info.");
            }
        };

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        return root;
    }

    private void initFireStore() {

        /* firestore */
        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection("books").orderBy("bookPrice", Query.Direction.ASCENDING).limit(LIMIT);

    }

    @Override
    public void onStart() {
        super.onStart();
        if(mAdapter != null)
            mAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAdapter != null){
            mAdapter.stopListening();
        }

    }


    @Override
    public void onBookSelected(DocumentSnapshot snapshot) {
        String book_id = snapshot.getId();
        Intent bookDetails = new Intent(getActivity(), BookDetailsActivity.class);
        bookDetails.putExtra("bookid",book_id);
        startActivity(bookDetails);

    }
}