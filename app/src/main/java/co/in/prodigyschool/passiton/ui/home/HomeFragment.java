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


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import co.in.prodigyschool.passiton.Data.User;
import co.in.prodigyschool.passiton.R;

public class HomeFragment extends Fragment implements BookAdapter.OnBookSelectedListener {

    private static String TAG = "HOME FRAGMENT";
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private ViewGroup mEmptyView;
    private BookAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static final int LIMIT = 50;
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        /* recycler view */
        recyclerView = root.findViewById(R.id.home_recycler_view);
        mEmptyView = root.findViewById(R.id.view_empty);

        initFireStore();

        /* use a linear layout manager */
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        return root;
    }

    private void initFireStore() {

        /* firestore */
        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection("books").orderBy("bookPrice", Query.Direction.ASCENDING).limit(LIMIT);
        populateBookAdapter();
        removeUserBooks();

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

    private void removeUserBooks() {
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
                Query query = mFirestore.collection("books").whereIn("userId", usersList).limit(LIMIT);
                mAdapter.setQuery(query);
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
    public void onBookSelected(DocumentSnapshot snapshot) {
        String book_id = snapshot.getId();
        Intent bookDetails = new Intent(getActivity(), BookDetailsActivity.class);
        bookDetails.putExtra("bookid", book_id);
        bookDetails.putExtra("isHome",true);
        startActivity(bookDetails);

    }
}