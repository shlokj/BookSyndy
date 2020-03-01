package com.booksyndy.academics.android.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.booksyndy.academics.android.Adapters.HomeAdapter;
import com.booksyndy.academics.android.BookDetailsActivity;
import com.booksyndy.academics.android.CreateListingActivity;
import com.booksyndy.academics.android.Data.Book;
import com.booksyndy.academics.android.Data.OnFilterSelectionListener;
import com.booksyndy.academics.android.Data.User;
import com.booksyndy.academics.android.GetBookPictureActivity;
import com.booksyndy.academics.android.HomeActivity;
import com.booksyndy.academics.android.R;
import com.booksyndy.academics.android.util.Filters;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements HomeAdapter.OnBookSelectedListener, OnFilterSelectionListener,EventListener<QuerySnapshot> {

    private static String TAG = "HOME_FRAGMENT";
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private ViewGroup mEmptyView;
    private HomeAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private User currentUser;
    private String curUserId;
    private int gradeNumber, boardNumber, year, userType;
    private boolean preferGuidedMode;
    private FilterDialogFragment mFilterDialog;
    private List<Book> bookList;
    private List<Book> bookListFull;
    private ListenerRegistration booksRegistration;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View parentLayout;
    private SharedPreferences userPref;
    private MenuItem filterItem;
    private double userLat,userLng;
    private int userGrade;
    private SimpleDateFormat dateFormat;
    private List<Book> filteredList;
    private TextView nothingHereTV;
    private SearchView searchView;
    private NavController navController;
    private final int MENU_CHAT = 789;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserDetails();
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        dateFormat =  new SimpleDateFormat("dd MM yyyy HH",Locale.getDefault());

    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //homeViewModel =
        //        ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        parentLayout = root;
        userPref = getActivity().getSharedPreferences(getString(R.string.UserPref),0);
        userGrade = userPref.getInt(getString(R.string.p_grade),4);
        mFilterDialog = new FilterDialogFragment(userPref.getInt(getString(R.string.p_grade),4));
        setHasOptionsMenu(true);
        /* recycler view */
        recyclerView = root.findViewById(R.id.home_recycler_view);
        mEmptyView = root.findViewById(R.id.view_empty);
        nothingHereTV = root.findViewById(R.id.nothinghereTV);
        nothingHereTV.append(". Try searching or changing your filters.");

        preferGuidedMode = userPref.getBoolean(getString(R.string.preferGuidedMode),false);
        userLat = userPref.getFloat(getString(R.string.p_lat),0.0f);
        userLng = userPref.getFloat(getString(R.string.p_lng),0.0f);

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);


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
                startBookPub.putExtra("YEAR_NUMBER", year);
                startBookPub.putExtra("USER_TYPE",userType);
                startBookPub.putExtra("PHONE_NUMBER",currentUser.getPhone());
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
                if(checkConnection(getContext())) {
                    refreshHome();
                }
                else{
                    showSnackbar("Check your internet!");
                    if(mSwipeRefreshLayout.isRefreshing()){
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);


        /* use a linear layout manager */
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        // TODO: save filters and don't set them to default every time
        setDefaultFilters();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (booksRegistration == null) {
            booksRegistration = mQuery.addSnapshotListener(this);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.onDataChanged();
        }
//        gradeNumber = getActivity().getIntent().getIntExtra("GRADE_NUMBER",4);
//        boardNumber = getActivity().getIntent().getIntExtra("BOARD_NUMBER",1);
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

    private void refreshHome() {
        if (booksRegistration != null) {
            booksRegistration.remove();
            booksRegistration = null;
            booksRegistration = mQuery.addSnapshotListener(this);
        }
        // mSwipeRefreshLayout.setRefreshing(false);
    }


    private void initFireStore() {
        if(mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(true);

        /* firestore */
        mFirestore = FirebaseFirestore.getInstance();
        curUserId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        mQuery = mFirestore.collection("books").whereEqualTo("bookSold",false);
//        if(userGrade <= 5){
//           mQuery = mQuery.whereLessThan("gradeNumber",7);
//        }
//        else{
//            mQuery = mQuery.whereGreaterThan("gradeNumber",5);
//        }
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
        if(mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        menu.add(0, MENU_CHAT, Menu.FIRST, "Chats").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.getItem(0).setIcon(R.drawable.ic_chat_white_24px);

        filterItem = menu.findItem(R.id.filter);
        searchView = (SearchView)searchItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onQueryTextChange(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Book> filteredList1 = new ArrayList<>();
                if(newText == null || newText.trim().isEmpty())
                {
                    filteredList1.addAll(filteredList);
                    searchView.clearFocus();
                    mAdapter.setBookList(filteredList);
                    return true;
                }

                String filterPattern = newText.toLowerCase().trim();
                String[] qws = filterPattern.split("\\W+");
                for (Book book : filteredList) {
//                    int foundIndex = book.getBookName().toLowerCase().indexOf(filterPattern);
                    String[] tags = book.getBookName().toLowerCase().split("\\W+");
                    for (String tag : tags) {
                        for (String qw : qws) {
                            if (tag.indexOf(qw)==0) {
                                if (!filteredList1.contains(book)) {
                                    filteredList1.add(book);
                                }
//                                break;
                            }
                        }
                    }
//                        Toast.makeText(getActivity(),"Found at "+foundIndex,Toast.LENGTH_SHORT).show();
                    /*if (book.getBookName().toLowerCase().contains(filterPattern)) {
                        if (foundIndex != -1) {
                            continue;
                        }
                        if (foundIndex == 0) {
                            filteredList1.add(book);
                            continue;
                        }
                        if ((book.getBookName().toLowerCase().substring(foundIndex - 1, foundIndex).equals(" ")) && !filteredList1.contains(book)) {
                            filteredList1.add(book);
                        }
                    }*/
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
                for (Book book : bookListFull) {
                    /*int foundIndex = book.getBookName().toLowerCase().indexOf(filterPattern);
                    if (book.getBookName().toLowerCase().contains(filterPattern)) {
                        if (foundIndex != -1 && (foundIndex == 0 || book.getBookName().toLowerCase().substring(foundIndex - 1, foundIndex).equals(" ")) &&  !filteredList1.contains(book)) {
                            filteredList1.add(book);
                        }
                    }*/
                    String[] tags = book.getBookName().toLowerCase().split(" ");
                    for (String tag : tags) {
                        for (String qw : qws) {
                            if (tag.indexOf(qw)==0) {
                                if (!filteredList1.contains(book)) {
                                    filteredList1.add(book);
                                }
                                break;
                            }
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

                mAdapter.setBookList(filteredList1);

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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.filter) {
            //Intent openFilters = new Intent(HomeActivity.this,FilterActivity.class);
            //startActivity(openFilters);
            //open filter activity to apply and change filters
            onFilterClicked();
        }
        else if (id == R.id.action_search) {
            filterItem.setVisible(false);
        }
        else if (id == MENU_CHAT) {
            navController.navigate(R.id.nav_chats);
        }
        return super.onOptionsItemSelected(item);
    }
    public void onFilterClicked() {
        // Show the dialog containing filter options

        if(!mFilterDialog.isAdded())
            mFilterDialog.show(getChildFragmentManager(), FilterDialogFragment.TAG);
    }

    @Override
    public void onFilter(Filters filters) {
        Log.d(TAG, "onFilter: entered :price"+filters.hasPrice());
        filteredList = new ArrayList<>();
        List<Book> toBeRemoved = new ArrayList<>();

        boolean noFilter = true;

        // add all books to filtered list to start with
        filteredList.addAll(bookListFull);

        // then, remove books that don't satisfy the provided criteria

        if(filters.hasPrice()){
            for(Book book:filteredList){
                if(book.getBookPrice() != 0)
                    toBeRemoved.add(book);
            }
            noFilter = false;
        }

        for (Book b:toBeRemoved) {
            filteredList.remove(b);
        }

        toBeRemoved.clear();

        if (!(filters.IsText() && filters.IsNotes())) {
            if (filters.IsText()) {
                for (Book book : filteredList) {
                    if (!book.isTextbook())
                        toBeRemoved.add(book);
                }
            }
            else if (filters.IsNotes()){
                for (Book book : filteredList) {
                    if (book.isTextbook())
                        toBeRemoved.add(book);
                }
            }
            noFilter = false;
        }


        for (Book b:toBeRemoved) {
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
                    if (filteredList.get(a).getBoardNumber() == i) {
                        toBeRemoved.add(filteredList.get(a));
                    }
                }
            }
            noFilter = false;
        }

        else {

            // remove competitive exam books from the list as we don't want them by default
            for (int a = 0; a < filteredList.size(); a++) {
                if (filteredList.get(a).getBoardNumber() == 20) {
                    toBeRemoved.add(filteredList.get(a));
                }
            }
        }


        for (Book b:toBeRemoved) {
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
                    if (filteredList.get(a).getGradeNumber() == i) {
                        toBeRemoved.add(filteredList.get(a));
                    }
                }
            }
            noFilter = false;
        }

        for (Book b:toBeRemoved) {
            filteredList.remove(b);
        }

        toBeRemoved.clear();


        if(filters.hasBookDistance()){
            if(userLat != 0.0 && userLng != 0.0) {
                for (Book b : filteredList) {
                    if (!getBookUnderDistance(b, filters.getBookDistance(), userLat, userLng)) {
                        toBeRemoved.add(b);
                    }
                }
                noFilter = false;
            }
            else {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                if (homeActivity != null) {
                    homeActivity.startLocationUpdates();
                    //showSnackbar("Please Enable GPS");
                }

                filters.setBookDistance(-1);
            }
        }

        for (Book b:toBeRemoved) {
            filteredList.remove(b);
        }

        toBeRemoved.clear();


        if(filters.hasSortBy()){

            if(filters.getSortBy().equalsIgnoreCase("time")){
                //sort by book duration
                Collections.sort(filteredList, new Comparator<Book>() {
                    @Override
                    public int compare(Book o1, Book o2) {
                        try {
                            Date d1 = dateFormat.parse(o1.getBookTime());
                            Date d2 = dateFormat.parse(o2.getBookTime());
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

                    Collections.sort(filteredList, new Comparator<Book>() {
                        @Override
                        public int compare(Book o1, Book o2) {
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
                    showSnackbar("Error Fetching User Location");
                    filters.setSortBy("Relevance");
                }

            }

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
    }


    private void setDefaultFilters() {
        final String curUserId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        mFirestore.collection("users").document(curUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.e(TAG, "onEvent: listener error",e );
                    return;
                }
                currentUser = snapshot.toObject(User.class);
                Log.d(TAG, "setDefaultFilters: success");
                Filters defaultFilters = new Filters();
                List<Integer> gradesList = new ArrayList<Integer>();
                gradesList.add(currentUser.getGradeNumber());
                if (currentUser.getGradeNumber()<=5) {
                    gradesList.add(currentUser.getGradeNumber() + 1);
                }
                List<Integer> boardsList = new ArrayList<Integer>();
                boardsList.add(currentUser.getBoardNumber());
                defaultFilters.setBookGrade(gradesList);
                if (currentUser.isCompetitiveExam()) {
                    boardsList.add(20);
                }
                defaultFilters.setBookBoard(boardsList);
                defaultFilters.setIsText(true);
                defaultFilters.setBookDistance(-1);
                homeViewModel.setFilters(defaultFilters);
                onFilter(homeViewModel.getFilters());
            }
        });
    }


    @Override
    public void onBookSelected(Book book) {
        String book_id = book.getDocumentId();
        Intent bookDetails = new Intent(getActivity(), BookDetailsActivity.class);
        bookDetails.putExtra("bookid", book_id);
        bookDetails.putExtra("isHome",true);
        startActivity(bookDetails);
    }

    public  boolean getBookUnderDistance(Book book,int distance, double latitude, double longitude){
        float res;
        if(book.getLat() != 0.0 && book.getLng() != 0.0 ) {
            Location locationA = new Location("point A");
            Location locationB = new Location("point B");

            locationA.setLatitude(book.getLat());
            locationA.setLongitude(book.getLng());
            locationB.setLatitude(latitude);
            locationB.setLongitude(longitude);
            res = (locationA.distanceTo(locationB) / 1000);
            Log.d(TAG, "getBookUnderDistance: "+res);
            return (res <= distance);
        }

        return false;
    }


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
        if(mFirestore == null)
            mFirestore = FirebaseFirestore.getInstance();
        try{
            curUserId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            DocumentReference userReference =  mFirestore.collection("users").document(curUserId);
            userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    User user = snapshot.toObject(User.class);
                    if(user != null) {
                        currentUser = user;
                        gradeNumber=user.getGradeNumber();
                        boardNumber=user.getBoardNumber();
                        userType=user.getUserType();
                        year=user.getYear();

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

    public void showSnackbar(String message) {
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT)
                .setAction("OKAY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }
}