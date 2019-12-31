package co.in.prodigyschool.passiton.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import co.in.prodigyschool.passiton.Adapters.BookAdapter;
import co.in.prodigyschool.passiton.Adapters.HomeAdapter;
import co.in.prodigyschool.passiton.BookDetailsActivity;
import co.in.prodigyschool.passiton.CreateListingActivity;
import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.Data.Chat;
import co.in.prodigyschool.passiton.Data.User;
import co.in.prodigyschool.passiton.GetBookPictureActivity;
import co.in.prodigyschool.passiton.R;
import co.in.prodigyschool.passiton.util.Filters;

public class HomeFragment extends Fragment implements HomeAdapter.OnBookSelectedListener,FilterDialogFragment.OnFilterSelectionListener,EventListener<QuerySnapshot> {

    private static String TAG = "HOME_FRAGMENT";
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private ViewGroup mEmptyView;
    private HomeAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private static final int LIMIT = 50;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private User currentUser;
    private String curUserId;
    private int gradeNumber, boardNumber;
    private boolean preferGuidedMode=true;
    FilterDialogFragment mFilterDialog;
    FilterCollegeDialogFragment mCFdialog;
    private List<Book> bookList;
    private List<Book> bookListFull;
    private ListenerRegistration booksRegistration;
    private SwipeRefreshLayout mSwipeRefreshLayout;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserDetails();
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
    }

    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //homeViewModel =
        //        ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mFilterDialog = new FilterDialogFragment();
        mCFdialog = new FilterCollegeDialogFragment();
        setHasOptionsMenu(true);
        /* recycler view */
        recyclerView = root.findViewById(R.id.home_recycler_view);
        mEmptyView = root.findViewById(R.id.view_empty);
        FloatingActionButton fab = root.findViewById(R.id.fab_home);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startBookPub;
                if (preferGuidedMode) {
                    startBookPub = new Intent(getActivity(), GetBookPictureActivity.class);
                }
                else {
                    startBookPub = new Intent(getActivity(), CreateListingActivity.class);
                }
                startBookPub.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startBookPub.putExtra("GRADE_NUMBER", gradeNumber);
                startBookPub.putExtra("BOARD_NUMBER", boardNumber);
                startActivity(startBookPub);
            }
        });
        initFireStore();
        bookList = new ArrayList<>();
        bookListFull = new ArrayList<>();
        // specify an adapter
        mAdapter = new HomeAdapter(getContext(),bookList, this) {

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

        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swiperefreshhome);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //code to reload with new books
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);


        /* use a linear layout manager */
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        return root;
    }


    private void initFireStore() {

        /* firestore */
        mFirestore = FirebaseFirestore.getInstance();
        curUserId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        mQuery = mFirestore.collection("books").limit(LIMIT);
        booksRegistration = mQuery.addSnapshotListener(this);
        //removeUserBooks(mQuery);
    }


    private void populateBookAdapter(QuerySnapshot queryDocumentSnapshots) {

        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }
        if(!queryDocumentSnapshots.isEmpty()) {
            bookList.clear();
            for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                Book book = snapshot.toObject(Book.class);
                if(!book.getUserId().equalsIgnoreCase(curUserId)){
                    bookList.add(book);
                }
            }
            //bookList.addAll(queryDocumentSnapshots.toObjects(Book.class));
            mAdapter.setBookList(bookList);
            bookListFull = new ArrayList<>(bookList);
            mAdapter.onDataChanged();

        }

        onFilter(homeViewModel.getFilters());

    }

    @Override
    public void onStart() {
        super.onStart();
        if (booksRegistration == null) {
            booksRegistration = mQuery.addSnapshotListener(this);
            mAdapter.onDataChanged();
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
        if (booksRegistration != null) {
            booksRegistration.remove();
            booksRegistration = null;
            mAdapter.onDataChanged();
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
                List<Book> filteredList = new ArrayList<>();
                if(newText == null || newText.trim().isEmpty())
                {
                    filteredList = bookListFull;
                }
                else{
                    String filterPattern = newText.toLowerCase().trim();
                    for (Book book : bookListFull) {
                        if (book.getBookName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(book);
                        }
                    }
                }
                mAdapter.setBookList(filteredList);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
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

            if(!mFilterDialog.isAdded())
            mFilterDialog.show(getChildFragmentManager(), FilterDialogFragment.TAG);
    }



    @Override
    public void onBookSelected(Book book) {
        String book_id = book.getDocumentId();
        Intent bookDetails = new Intent(getActivity(), BookDetailsActivity.class);
        bookDetails.putExtra("bookid", book_id);
        bookDetails.putExtra("isHome",true);
        startActivity(bookDetails);
    }

    @Override
    public void onFilter(Filters filters) {
        Log.d(TAG, "onFilter: entered :price"+filters.hasPrice());
        List<Book> filteredList = new ArrayList<>();
        boolean noFilter = true;
        if(filters.hasPrice()){
            for(Book book:bookListFull){
                if(book.getBookPrice() == 0)
                    filteredList.add(book);
            }
            noFilter = false;
        }

        if(filters.IsText()){
            for(Book book:bookListFull){
                if(book.isTextbook() && !filteredList.contains(book))
                    filteredList.add(book);
            }
            noFilter = false;
        }

        if(filters.IsNotes()){
            for(Book book:bookListFull){
                if(!book.isTextbook() && !filteredList.contains(book))
                    filteredList.add(book);
            }
            noFilter = false;
        }



        //filter: board
        if(filters.hasBookBoard()){
            for(Book book:bookListFull){
                for(Integer i:filters.getBookBoard()){
                    if(book.getBoardNumber() == i && !filteredList.contains(book)){
                        filteredList.add(book);
                    }
                }
            }

            noFilter = false;
        }

        //filter: grade
        if(filters.hasBookGrade()){
            for(Book book:bookListFull){
                for(Integer i:filters.getBookGrade()){
                    if(book.getGradeNumber() == i && !filteredList.contains(book)){
                        filteredList.add(book);
                    }
                }
            }

            noFilter = false;
        }


        if(filteredList == null || filteredList.isEmpty()){
            if(noFilter){
            Log.d(TAG, "onFilter: is empty");
            filteredList.addAll(bookList);
            bookListFull.clear();
            bookListFull.addAll(bookList);
            }
            else{
                bookListFull.clear();
                bookListFull.addAll(bookList);
            }
        }
        mAdapter.setBookList(filteredList);
        homeViewModel.setFilters(filters);

//        mQuery = query;
//        //mAdapter.setQuery(query);
//        //removeUserBooks(query);
    }


//    private void setDefaultFilters() {
//        final String curUserId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
//        mFirestore.collection("users").document(curUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
//           if(e != null){
//               Log.e(TAG, "onEvent: listener error",e );
//               return;
//           }
//           currentUser = snapshot.toObject(User.class);
//                Log.d(TAG, "setDefaultFilters: success");
//                Filters defaultFilters = new Filters();
//                defaultFilters.setBookGrade(currentUser.getGradeNumber());
//                defaultFilters.setBookBoard(currentUser.getBoardNumber());
//                defaultFilters.setIsText(true);
//                homeViewModel.setFilters(defaultFilters);
//                onFilter(homeViewModel.getFilters());
//            }
//        });
//    }



    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) { // connected to the internet
                // connected to the mobile provider's data plan
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }


    private void getUserDetails() {
        if (!checkConnection(getActivity())) {
            Toast.makeText(getActivity(),"Internet Required",Toast.LENGTH_LONG).show();
            return;
        }
        mFirestore = FirebaseFirestore.getInstance();
        try{
            curUserId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            DocumentReference userReference =  mFirestore.collection("users").document(curUserId);
            userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    User user = snapshot.toObject(User.class);
                    if(user != null) {
                        gradeNumber=user.getGradeNumber();
                        boardNumber=user.getBoardNumber();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: ",e );
                }
            });

        }
        catch(Exception e){
            Log.e(TAG, "PopulateUserDetails method failed with  ",e);
        }
    }


    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.d(TAG, "onEvent: chats error", e);
            return;
        }
        populateBookAdapter(queryDocumentSnapshots);

    }


}