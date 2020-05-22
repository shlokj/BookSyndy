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

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.booksyndy.academics.android.Adapters.HomeAdapter;
import com.booksyndy.academics.android.BookDetailsActivity;
import com.booksyndy.academics.android.CreateGeneralListingActivity;
import com.booksyndy.academics.android.CreateListingActivity;
import com.booksyndy.academics.android.Data.Book;
import com.booksyndy.academics.android.Data.OnFilterSelectionListener;
import com.booksyndy.academics.android.Data.User;
import com.booksyndy.academics.android.GetBookPictureActivity;
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
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements HomeAdapter.OnBookSelectedListener, OnFilterSelectionListener, EventListener<QuerySnapshot> {

    private static String TAG = "HOME_FRAGMENT";
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private ViewGroup mEmptyView;
    private HomeAdapter mAdapter;
    private LinearLayoutManager layoutManager;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private User currentUser;
    private String curUserId, grades = "", boards = "";
    private int gradeNumber, boardNumber, year, userType;
    private boolean preferGuidedMode, preferGeneral;
    private FilterDialogFragment mFilterDialog;
    private List<Book> bookList;
    private List<Book> bookListFull;
    private ListenerRegistration booksRegistration;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View parentLayout;
    private SharedPreferences userPref;
    private MenuItem filterItem;
    private double userLat, userLng;
    private int userGrade;
    private SimpleDateFormat dateFormat;
    private List<Book> filteredList;
    private TextView nothingHereTV;
    private SearchView searchView;
    private NavController navController;
    private Snackbar sb;
    private final int MENU_CHAT = 789;
    private DocumentSnapshot lastVisibleItem;

    //TODO: progressbar for search


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getUserDetails();
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        dateFormat = new SimpleDateFormat("dd MM yyyy HH", Locale.getDefault());

    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //homeViewModel =
        //        ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        parentLayout = root;
        userPref = getActivity().getSharedPreferences(getString(R.string.UserPref), 0);
        userGrade = userPref.getInt(getString(R.string.p_grade), 4);
        mFilterDialog = new FilterDialogFragment(userPref.getInt(getString(R.string.p_grade), 4));
        setHasOptionsMenu(true);
        /* recycler view */
        recyclerView = root.findViewById(R.id.home_recycler_view);
        mEmptyView = root.findViewById(R.id.view_empty);
        nothingHereTV = root.findViewById(R.id.nothinghereTV);
        nothingHereTV.append(". Try changing your filters.");

        recyclerView.setVerticalScrollBarEnabled(false);
//        recyclerView.setHorizontalScrollBarEnabled(false);

        preferGuidedMode = userPref.getBoolean(getString(R.string.preferGuidedMode), false);
        preferGeneral = userPref.getBoolean(getString(R.string.preferGeneral), false);
        userLat = userPref.getFloat(getString(R.string.p_lat), 0.0f);
        userLng = userPref.getFloat(getString(R.string.p_lng), 0.0f);

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        FloatingActionButton fab = root.findViewById(R.id.fab_home);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserDetails();
                Intent startBookPub;
                if (preferGeneral) {
                    startBookPub = new Intent(getActivity(), CreateGeneralListingActivity.class);
                    startBookPub.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startBookPub.putExtra("USER_TYPE", userType);
                    startBookPub.putExtra("PHONE_NUMBER", currentUser.getPhone());
                } else {
                    if (preferGuidedMode) {
                        startBookPub = new Intent(getActivity(), GetBookPictureActivity.class);
                    } else {
                        startBookPub = new Intent(getActivity(), CreateListingActivity.class);
                    }
                    startBookPub.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startBookPub.putExtra("GRADE_NUMBER", gradeNumber);
                    startBookPub.putExtra("BOARD_NUMBER", boardNumber);
                    startBookPub.putExtra("YEAR_NUMBER", year);
                    startBookPub.putExtra("USER_TYPE", userType);
                    startBookPub.putExtra("PHONE_NUMBER", currentUser.getPhone());
                }
                startActivity(startBookPub);
            }
        });

        initFireStore();
        bookList = new ArrayList<>();
        bookListFull = new ArrayList<>();
        // specify an adapter
        mAdapter = new HomeAdapter(getContext(), bookList, this, userLat, userLng) {

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

        layoutManager = new LinearLayoutManager(getContext());
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swiperefreshhome);


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //code to reload with new books
                if (checkConnection(getContext())) {
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
//                    refreshHome();
//                    sortBooks(homeViewModel.getFilters());
                } else {
                    showSnackbar("Check your internet!");
                    if (mSwipeRefreshLayout.isRefreshing()) {
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
        //add list scroll listeners
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastItemPos = layoutManager.findLastCompletelyVisibleItemPosition();
                if (lastItemPos >= bookList.size() - 1) {
                    loadMoreBooks();
                }
            }
        });


        return root;
    }

    private void loadMoreBooks() {
        Log.d(TAG, "loadMoreBooks: called");
        if (lastVisibleItem != null) {
            mQuery = mQuery.startAfter(lastVisibleItem).limit(5);
        }
        mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, "onEvent: Books error", e);
                    return;
                }
                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    lastVisibleItem = queryDocumentSnapshots.getDocuments()
                            .get(queryDocumentSnapshots.size() - 1);
                    bookList.addAll(queryDocumentSnapshots.toObjects(Book.class));
                    bookListFull.addAll(queryDocumentSnapshots.toObjects(Book.class));
                    mAdapter.setBookList(bookList);
                    sortBooks(homeViewModel.getFilters());
                }

            }
        });

    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (booksRegistration == null) {
//            booksRegistration = mQuery.addSnapshotListener(this);
//        }
//    }
//
//

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.onDataChanged();
        }
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (mAdapter != null) {
//            mAdapter.onDataChanged();
//        }
////        gradeNumber = getActivity().getIntent().getIntExtra("GRADE_NUMBER",4);
////        boardNumber = getActivity().getIntent().getIntExtra("BOARD_NUMBER",1);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (booksRegistration != null) {
//            booksRegistration.remove();
//            booksRegistration = null;
//            mAdapter.onDataChanged();
//        }
//    }

    private void refreshHome() {
        if (booksRegistration != null) {
            booksRegistration.remove();
            booksRegistration = null;
            booksRegistration = mQuery.addSnapshotListener(this);
        }
        // mSwipeRefreshLayout.setRefreshing(false);
    }


    private void initFireStore() {
        Log.d(TAG, "initFireStore: entered");
        if (mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(true);

        Filters tFilters = homeViewModel.getFilters();
        /* firestore */
        mFirestore = FirebaseFirestore.getInstance();
        curUserId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        if (preferGeneral) {
            mQuery = mFirestore.collection("books").whereEqualTo("general", true).whereEqualTo("bookSold", false);
        } else {
            mQuery = mFirestore.collection("books").whereEqualTo("general", false).whereEqualTo("bookSold", false);
        }
        //default filters


        int grade = userPref.getInt(getString(R.string.p_grade), -1);
        int board = userPref.getInt(getString(R.string.p_board), -1);

        if (!preferGeneral) {
            Log.d(TAG, "initFireStore: userGrade: " + grade);
            if (grade != -1 && grade <= 5) {
                mQuery = mQuery.whereIn("gradeNumber", Arrays.asList(grade, grade + 1));
            } else if (grade != -1) {
                mQuery = mQuery.whereEqualTo("gradeNumber", grade);
            }
            if (board != -1)
                mQuery = mQuery.whereEqualTo("boardNumber", board);
        }
        mQuery = mQuery.orderBy("createdAt", Query.Direction.DESCENDING);
        booksRegistration = mQuery.limit(10).addSnapshotListener(this);
        setDefaultFilters(grade, board);
    }


    private void populateBookAdapter(QuerySnapshot queryDocumentSnapshots) {

        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }
        if (!queryDocumentSnapshots.isEmpty()) {
            lastVisibleItem = queryDocumentSnapshots.getDocuments()
                    .get(queryDocumentSnapshots.size() - 1);
            Log.d(TAG, "populateBookAdapter: snap count: " + queryDocumentSnapshots.size());
            bookList.clear();
            bookList.addAll(queryDocumentSnapshots.toObjects(Book.class));
            Log.d(TAG, "populateBookAdapter: books count: " + bookList.size());
            sortBooks(homeViewModel.getFilters());
            bookListFull = new ArrayList<>(bookList);
        }

        // onFilter(homeViewModel.getFilters());
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        menu.add(0, MENU_CHAT, Menu.FIRST, "Chats").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.getItem(0).setIcon(R.drawable.ic_chat_white_24px);

        Client client = new Client("B2XKIGCNXW", "8cfa545e393f40c1e03c35f834b7c6b6");
        final Index index = client.getIndex("prod_books");


        filterItem = menu.findItem(R.id.filter);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onQueryTextChange(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (sb != null && newText.length() == 0 && sb.isShown()) {
                    sb.dismiss();
                }
                if (newText == null || newText.trim().isEmpty()) {
                    searchView.clearFocus();
                    if (sb != null && newText.length() == 0 && sb.isShown()) {
                        sb.dismiss();
                    }
                    mAdapter.setBookList(bookList);
                    return true;
                }
                com.algolia.search.saas.Query query = new com.algolia.search.saas.Query(newText)
                        .setFilters("book.bookSold=0");
                if (preferGeneral)
                    query = query.setFilters("book.general=1");
                else
                    query = query.setFilters("book.general=0");
                query = query.setHitsPerPage(20);
                index.searchAsync(query, new CompletionHandler() {
                    @Override
                    public void requestCompleted(@Nullable JSONObject jsonObject, @Nullable AlgoliaException e) {

                        try {
                            JSONArray hits = jsonObject.getJSONArray("hits");
                            List<Book> filteredList1 = new ArrayList<>();
                            for (int i = 0; i < hits.length(); i++) {
                                JSONObject rootObject = hits.getJSONObject(i);
                                JSONObject bookObject = rootObject.getJSONObject("book");
                                Book book = new Book();
                                book.setDocumentId(rootObject.getString("objectID"));
                                book.setBookName(bookObject.getString("bookName"));
                                book.setBookAddress(bookObject.getString("bookAddress"));
                                book.setBookTime(bookObject.getString("bookTime"));
                                book.setBookPhoto(bookObject.getString("bookPhoto"));
                                book.setBookPrice(bookObject.getInt("bookPrice"));
                                filteredList1.add(book);
                            }
                            mAdapter.setBookList(filteredList1);
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                });


                return false;
            }
        });

        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                startActivity(new Intent(getActivity(), SearchActivity.class));
                searchView.onActionViewExpanded();
                searchView.requestFocus();
                if ((grades != null && grades.trim() != "") && (boards != null && boards.trim() != ""))
                    sb = Snackbar.make(parentLayout, "Searching in " + grades + " and " + boards, Snackbar.LENGTH_LONG);
                else
                    sb = Snackbar.make(parentLayout, "Searching in your filters" + boards, Snackbar.LENGTH_LONG);
                sb.show();
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
        } else if (id == R.id.action_search) {
            filterItem.setVisible(false);
        } else if (id == MENU_CHAT) {
            navController.navigate(R.id.nav_chats);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onFilterClicked() {
        // Show the dialog containing filter options

        if (!mFilterDialog.isAdded())
            mFilterDialog.show(getChildFragmentManager(), FilterDialogFragment.TAG);
    }

    @Override
    public void onFilter(final Filters filters) {

        Query fQuery;

        boolean noFilter = true;

        if (preferGeneral) {
            fQuery = mFirestore.collection("books").whereEqualTo("general", true).whereEqualTo("bookSold", false);
        } else {
            fQuery = mFirestore.collection("books").whereEqualTo("general", false).whereEqualTo("bookSold", false); // TODO: same as above


            if (filters.hasBookBoard()) {
                //filters are applied
                noFilter = false;

                List<Integer> fBoards = filters.getBookBoard();

                fQuery = fQuery.whereEqualTo("boardNumber", fBoards.get(0));

                boards = "board: ";
                if (fBoards.contains(1)) {
                    boards = boards + "CBSE";
                } else if (fBoards.contains(2)) {
                    boards = boards + "ICSE/ISC";
                } else if (fBoards.contains(3)) {
                    boards = boards + "IB";
                } else if (fBoards.contains(4)) {
                    boards = boards + "IGCSE/CAIE";
                } else if (fBoards.contains(5)) {
                    boards = boards + "State board";
                } else if (fBoards.contains(6)) {
                    boards = boards + "other board";
                } else if (fBoards.contains(20)) {
                    grades = "";
                    boards = "competitive exams";
                }

            }

            //grade
            if (filters.hasBookGrade()) {
                noFilter = false;

                List<Integer> fGrades = filters.getBookGrade();


                fQuery = fQuery.whereIn("gradeNumber", fGrades);

                grades = "grades: ";
                if (fGrades.size() == 1) {
                    if (fGrades.contains(1)) {
                        grades = grades + "5-";
                    } else if (fGrades.contains(2)) {
                        grades = grades + "6-8";
                    } else if (fGrades.contains(3)) {
                        grades = grades + "9";
                    } else if (fGrades.contains(4)) {
                        grades = grades + "10";
                    } else if (fGrades.contains(5)) {
                        grades = grades + "11";
                    } else if (fGrades.contains(6)) {
                        grades = grades + "12";
                    }
                } else if (fGrades.size() >= 2) {
                    if (fGrades.contains(1)) {
                        grades = grades + "5-, ";
                    }
                    if (fGrades.contains(2)) {
                        grades = grades + "6-8, ";
                    }
                    if (fGrades.contains(3)) {
                        grades = grades + "9, ";
                    }
                    if (fGrades.contains(4)) {
                        grades = grades + "10, ";
                    }
                    if (fGrades.contains(5)) {
                        grades = grades + "11, ";
                    }
                    if (fGrades.contains(6)) {
                        grades = grades + "12, ";
                    }
                }
                if (grades.substring(grades.length() - 2).equals(", ")) {
                    grades = grades.substring(0, grades.length() - 2);
                }
            } else {
                grades = "all grades";
            }

            if (!(filters.IsText() && filters.IsNotes())) {
                noFilter = false;

                if (filters.IsText()) {
                    fQuery = fQuery.whereEqualTo("textbook", true);
                } else if (filters.IsNotes()) {
                    fQuery = fQuery.whereEqualTo("textbook", false);
                }

            }
        }

        //price
        if (filters.hasPrice()) {
            noFilter = false;

            fQuery = fQuery.whereEqualTo("bookPrice", 0);
        }

        fQuery = fQuery.orderBy("createdAt", Query.Direction.DESCENDING);

        if (noFilter) {
            homeViewModel.setFilters(filters);
            refreshHome();
            sortBooks(filters);
        } else {
            homeViewModel.setFilters(filters);

            mQuery = fQuery;
            mQuery.limit(10).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.d(TAG, "onEvent: Books error", e);
                        return;
                    }
                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        bookList.clear();
                        lastVisibleItem = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);
                        bookList.addAll(queryDocumentSnapshots.toObjects(Book.class));
                        bookListFull.addAll(queryDocumentSnapshots.toObjects(Book.class));
                        mAdapter.setBookList(bookList);
                        sortBooks(filters);
                    } else if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(getActivity(), "No listings match your filters. Showing listings that match your previous filters, if any.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }


    private void sortBooks(Filters filters) {
        List<Book> filterList = new ArrayList<>(bookList);
        boolean hasFilter = false;
        if (filters.hasBookDistance()) {
            if (userLat != 0.0 && userLng != 0.0) {
                for (Book b : bookList) {
                    if (!getBookUnderDistance(b, filters.getBookDistance(), userLat, userLng)) {
                        filterList.remove(b);
                    }
                }
                bookList = filterList;
                bookListFull.clear();
                bookListFull.addAll(bookList);
//                mAdapter.setBookList(filterList);
            }

        }


        if (filters.hasSortBy()) {

            if (filters.getSortBy().equalsIgnoreCase("time")) {
                //sort by book duration
                try {
                    Collections.sort(bookList, new Comparator<Book>() {
                        @Override
                        public int compare(Book o1, Book o2) {
                            try {
                                Date d1 = dateFormat.parse(o1.getBookTime());
                                Date d2 = dateFormat.parse(o2.getBookTime());
                                if (d1.before(d2)) {
                                    return 1;
                                } else {
                                    return -1;
                                }

                            } catch (Exception e) {
                                return 0;
                            }
                        }
                    });

                } catch (Exception e) {
                    // don't tell the user anything as the app is still usable
//                    Toast.makeText(getActivity(),"Ran into an issue while filtering/sorting. Please contact the developer, or provide details in the feedback section.",Toast.LENGTH_LONG).show();
                }
            } else {
                //sort by book distance

                if (userLat != 0.0 && userLng != 0.0) {

                    Collections.sort(bookList, new Comparator<Book>() {
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


                            if (B1.distanceTo(userLocation) > B2.distanceTo(userLocation)) {
                                return 1;
                            } else {
                                return -1;
                            }
                        }
                    });
                } else {
                    showSnackbar("Error fetching location");
                    filters.setSortBy("Relevance");
                }

            }

        }

        mAdapter.setBookList(bookList);
        Log.d(TAG, "sortBooks: " + filters.getSortBy());
    }


    private void setDefaultFilters(int grade, int board) {

        Filters defaultFilters = new Filters();
        List<Integer> gradesList = new ArrayList<Integer>();
        gradesList.add(grade);
        if (grade <= 5) {
            gradesList.add(grade + 1);
        }
        List<Integer> boardsList = new ArrayList<Integer>();
        boardsList.add(board);
        defaultFilters.setBookGrade(gradesList);
        defaultFilters.setSortBy("time");
        defaultFilters.setBookBoard(boardsList);
        defaultFilters.setIsText(true);
        defaultFilters.setIsNotes(true);
        defaultFilters.setBookDistance(20);
        homeViewModel.setFilters(defaultFilters);

    }


    @Override
    public void onBookSelected(Book book) {
        String book_id = book.getDocumentId();
        Intent bookDetails = new Intent(getActivity(), BookDetailsActivity.class);
        bookDetails.putExtra("bookid", book_id);
        bookDetails.putExtra("isHome", true);
        bookDetails.putExtra("PREF_GEN",preferGeneral);
        bookDetails.putExtra("USER_PHONE", userPref.getString(getString(R.string.p_userphone), "0"));
        startActivity(bookDetails);
    }

    public boolean getBookUnderDistance(Book book, int distance, double latitude, double longitude) {
        float res;
        if (book.getLat() != 0.0 && book.getLng() != 0.0) {
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
            Toast.makeText(getActivity(), "Internet Required", Toast.LENGTH_LONG).show();
            return;
        }
        if (mFirestore == null)
            mFirestore = FirebaseFirestore.getInstance();
        try {
            curUserId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            DocumentReference userReference = mFirestore.collection("users").document(curUserId);
            userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    User user = snapshot.toObject(User.class);
                    if (user != null) {
                        currentUser = user;
                        gradeNumber = user.getGradeNumber();
                        boardNumber = user.getBoardNumber();
                        userType = user.getUserType();
                        year = user.getYear();

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: ", e);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "PopulateUserDetails method failed with  ", e);
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

    private void showSnackbar(String message) {
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