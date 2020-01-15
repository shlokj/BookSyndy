package co.in.prodigyschool.passiton.ui.bookMarks;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import co.in.prodigyschool.passiton.Adapters.BookAdapter;
import co.in.prodigyschool.passiton.Adapters.BookMarkAdapter;
import co.in.prodigyschool.passiton.BookDetailsActivity;
import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.R;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class BookMarksFragment extends Fragment implements BookMarkAdapter.OnBookSelectedListener, BookMarkAdapter.OnBookLongSelectedListener {

    private static String TAG = "BOOKMARKS";

    private ToolsViewModel toolsViewModel;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private Query mQuery;
    private CollectionReference bookMarkRef;
    private String curUserId;
    private RecyclerView recyclerView;
    private ViewGroup mEmptyView;
    private BookMarkAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Snackbar snackbar;
    private boolean undone, dismissed=true;
    private ArrayAdapter<String> optionsList;
    private ListView optionsListView;
    private AlertDialog dialog;
    private String book_id;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.home_recycler_view);
        mEmptyView = root.findViewById(R.id.view_empty);

        initFireStore();
        optionsList = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1);

        /* use a linear layout manager */
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
        root.findViewById(R.id.fab_home).setVisibility(View.GONE);
        mSwipeRefreshLayout = root.findViewById(R.id.swiperefreshhome);
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setEnabled(false);
        return root;
    }

    private void initFireStore() {
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        curUserId = mAuth.getCurrentUser().getPhoneNumber();
        mQuery = mFirestore.collection("bookmarks").document(curUserId).collection("books");
        setupBookMarkAdapter();

    }

    private void setupBookMarkAdapter() {
        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(mQuery, Book.class)
                .build();

        mAdapter = new BookMarkAdapter(options,this,this){
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

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                final DocumentReference deletedBookReference = mAdapter.getSnapshots().getSnapshot(position).getReference();
                final Book deletedBook = mAdapter.getSnapshots().getSnapshot(position).toObject(Book.class);
                mAdapter.deleteItem(viewHolder.getAdapterPosition());
                snackbar =
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Removed from bookmarks", Snackbar.LENGTH_LONG)
                                .setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // undo is selected, restore the deleted item
                                        deletedBookReference.set(deletedBook);

                                    }
                                });

                snackbar.setActionTextColor(getResources().getColor(android.R.color.holo_orange_light));
                snackbar.show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,@NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(getActivity(), R.color.bookSwipeBg))
                        .addActionIcon(R.drawable.ic_delete_24px)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);
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
        Log.d(TAG, "onBookSelected: "+book_id);
        bookDetails.putExtra("bookid", book_id);
        bookDetails.putExtra("isHome",false);
        bookDetails.putExtra("isBookmarks",true);
        startActivity(bookDetails);
    }

    @Override
    public void onBookLongSelected(final DocumentSnapshot snapshot) {
        displayOptions();
        optionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String opt = ((TextView) view).getText().toString();
                if (opt.equals("Remove from bookmarks")) {
                    final DocumentReference deletedBookReference = snapshot.getReference();
                    final Book deletedBook = snapshot.toObject(Book.class);
                    mAdapter.deleteItem(snapshot);

                    snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "Removed from bookmarks", Snackbar.LENGTH_LONG)
                                    .setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            // undo is selected, restore the deleted item
                                            deletedBookReference.set(deletedBook);

                                        }
                                    });

                    dialog.dismiss();

                    snackbar.setActionTextColor(getResources().getColor(android.R.color.holo_orange_light));
                    snackbar.show();
                }
            }
        });
    }


    private void displayOptions(){
        optionsList.clear();
        optionsList.add("Remove from bookmarks");
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

}