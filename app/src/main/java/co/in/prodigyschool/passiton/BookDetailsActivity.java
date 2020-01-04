package co.in.prodigyschool.passiton;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.Data.User;


public class BookDetailsActivity extends AppCompatActivity implements View.OnClickListener, EventListener<DocumentSnapshot> {

    private String bookid;
    private boolean isHome, saved;
    private FirebaseFirestore mFirestore;
    private Book currentBook;
    private User bookOwner;
    private TextView view_bookname,view_address,view_price,view_category,view_description, view_grade_and_board;
    private ImageView view_bookimage;
    private ListenerRegistration mBookUserRegistration,mBookRegistration,mBookMarkRegistration;
    private DocumentReference bookUserRef;
    private DocumentReference bookRef;
    private DocumentReference bookMarkRef;
    private Menu menu;
    private int MENU_DELETE = 123;
    private String curAppUser;

    private static final String TAG = "BOOK_DETAILS";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        bookid = getIntent().getStringExtra("bookid");
        isHome = getIntent().getBooleanExtra("isHome",false);
        initFireStore();
        view_bookname = findViewById(R.id.book_name);
        view_address = findViewById(R.id.book_address);
        view_category = findViewById(R.id.book_category);
        view_price = findViewById(R.id.book_price);
        view_bookimage = findViewById(R.id.book_image);
        view_description = findViewById(R.id.bookDescriptionTV);
        view_grade_and_board = findViewById(R.id.book_grade_and_board);
        findViewById(R.id.fab_chat).setOnClickListener(this);

        if(getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    private void initFireStore() {
        mFirestore = FirebaseFirestore.getInstance();

        if(mFirestore != null){
            bookRef =  mFirestore.collection("books").document(bookid);
            curAppUser = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        }
        else{
            Toast.makeText(getApplicationContext(),"Firebase error",Toast.LENGTH_SHORT).show();
        }
    }




    private void populateBookDetails(Book book) {

        try {
            currentBook = book;
            if(currentBook == null){
                Log.d(TAG, "populateBookDetails: current book error");
                return;
            }
            bookUserRef = mFirestore.collection("users").document(currentBook.getUserId());
            view_bookname.setText(currentBook.getBookName());
            view_description.setText(currentBook.getBookDescription());
            view_address.setText(currentBook.getBookAddress());
            int gradeNumber = currentBook.getGradeNumber();
            int boardNumber = currentBook.getBoardNumber();
            int year = currentBook.getBookYear();


            if (boardNumber == 20) {
                view_grade_and_board.setText("Competitive exams");
            }
            else {
                if (gradeNumber == 1) {
                    view_grade_and_board.setText("Grade 5 or below");
                } else if (gradeNumber == 2) {
                    view_grade_and_board.setText("Grade 6 to 8");
                } else if (gradeNumber == 3) {
                    view_grade_and_board.setText("Grade 9");
                } else if (gradeNumber == 4) {
                    view_grade_and_board.setText("Grade 10");
                } else if (gradeNumber == 5) {
                    view_grade_and_board.setText("Grade 11");
                } else if (gradeNumber == 6) {
                    view_grade_and_board.setText("Grade 12");
                } else if (gradeNumber == 7) {
                    view_grade_and_board.setText("Undergraduate");
//                Toast.makeText(getApplicationContext(),"Grade number: 7\nBoard number: "+boardNumber,Toast.LENGTH_SHORT).show();

                    if (boardNumber == 7) {
                        view_grade_and_board.append(" " + getString(R.string.divider_bullet) + " B. Tech");
                    } else if (boardNumber == 8) {
                        view_grade_and_board.append(" " + getString(R.string.divider_bullet) + " B. Sc");
                    } else if (boardNumber == 9) {
                        view_grade_and_board.append(" " + getString(R.string.divider_bullet) + " B. Com");
                    } else if (boardNumber == 10) {
                        view_grade_and_board.append(" " + getString(R.string.divider_bullet) + " BA");
                    } else if (boardNumber == 11) {
                        view_grade_and_board.append(" " + getString(R.string.divider_bullet) + " BBA");
                    } else if (boardNumber == 12) {
                        view_grade_and_board.append(" " + getString(R.string.divider_bullet) + " BCA");
                    } else if (boardNumber == 13) {
                        view_grade_and_board.append(" " + getString(R.string.divider_bullet) + " B. Ed");
                    } else if (boardNumber == 14) {
                        view_grade_and_board.append(" " + getString(R.string.divider_bullet) + " LLB");
                    } else if (boardNumber == 15) {
                        view_grade_and_board.append(" " + getString(R.string.divider_bullet) + " MBBS");
                    } else if (boardNumber == 16) {
                        view_grade_and_board.append(" " + getString(R.string.divider_bullet) + " other degree");
                    }

                    if (year!=0) {
                        view_grade_and_board.append(", "+ordinal(year)+" year");
                    }
                }

                if (boardNumber == 1) {
                    view_grade_and_board.append(" " + getString(R.string.divider_bullet) + " CBSE");
                } else if (boardNumber == 2) {
                    view_grade_and_board.append(" " + getString(R.string.divider_bullet) + " ICSE/ISC");
                } else if (boardNumber == 3) {
                    view_grade_and_board.append(" " + getString(R.string.divider_bullet) + " IB");
                } else if (boardNumber == 4) {
                    view_grade_and_board.append(" " + getString(R.string.divider_bullet) + " IGCSE/CAIE");
                } else if (boardNumber == 5) {
                    view_grade_and_board.append(" " + getString(R.string.divider_bullet) + " state board");
                } else if (boardNumber == 6) {
                    view_grade_and_board.append(" " + getString(R.string.divider_bullet) + " other board");
                }
                    //TODO: append degree

            }
            view_price.setText("₹" + currentBook.getBookPrice());
            if (currentBook.isTextbook()) {
                view_category.setText("Textbook");
            } else {
                view_category.setText("Notes");
            }
            Glide.with(view_bookimage.getContext())
                    .load(currentBook.getBookPhoto())
                    .into(view_bookimage);
            mBookUserRegistration = bookUserRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "book:onEvent", e);
                        return;
                    }
                    bookOwner = snapshot.toObject(User.class);
                }
            });
        }
        catch(Exception e){
            Log.e(TAG, "populateBookDetails: exception",e);
        }

        view_bookimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentBook != null) {
                    Intent viewFullPic = new Intent(BookDetailsActivity.this, ViewPictureActivity.class);
                    viewFullPic.putExtra("IMAGE_STR", currentBook.getBookPhoto());
                    startActivity(viewFullPic);
                }
            }
        });

        updateBookMark();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_chat:
                startChatActivity();
                break;
        }
    }

    private void startChatActivity() {
        try {
            Intent chatIntent = new Intent(BookDetailsActivity.this, ChatActivity.class);
            chatIntent.putExtra("visit_user_id", currentBook.getUserId()); // phone number
            chatIntent.putExtra("visit_user_name", bookOwner.getUserId()); // unique user id
            chatIntent.putExtra("visit_image", bookOwner.getImageUrl());
            startActivity(chatIntent);
        }
        catch(Exception e){
            showSnackbar("Please try again");
        }
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

    @Override
    protected void onStart() {
        super.onStart();
        mBookRegistration = bookRef.addSnapshotListener(this);

        if(isHome){
            findViewById(R.id.fab_chat).setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.fab_chat).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBookUserRegistration != null){
            mBookUserRegistration.remove();
            mBookUserRegistration = null;
        }
        if(mBookRegistration != null){
            mBookRegistration.remove();
            mBookRegistration = null;
        }
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "book:onEvent", e);
            return;
        }
            populateBookDetails(snapshot.toObject(Book.class));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!isHome) {
            menu.clear();
            menu.add(0, MENU_DELETE, Menu.NONE, getString(R.string.mark_as_sold)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_listing, menu);
        this.menu = menu;

        return true;
    }

    private void updateBookMark() {
        try {
            if (curAppUser == null) {
                curAppUser = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            }
            if (mFirestore == null) {
                mFirestore = FirebaseFirestore.getInstance();
            }
            bookMarkRef = mFirestore.collection("bookmarks").document(curAppUser).collection("books").document(currentBook.getDocumentId());
            mBookMarkRegistration = bookMarkRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.e(TAG, "onEvent: BookMarkCheck Failed", e);
                        return;
                    }

                    if (snapshot.exists()) {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_bookmark_filled_24px));
                        saved = true;
                        // menu.getItem(0).setEnabled(false);

                    }

                }
            });

        }
        catch (Exception e){
            Log.e(TAG, "updateBookMark: failed with",e );
            Toast.makeText(getApplicationContext(),"BookMark Failed",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bookmark:
                if (!saved) {
                    //add to bookmarks
                    addToBookMark();
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_bookmark_filled_24px));
                    saved = true;
                }
                else {
                    removeFromBookMarks();
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_bookmark_border_24px));
                    saved = false;
                }
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    /*
    TODO:call this function on menu option selected
     */
    private void markAsSold(){
        final CollectionReference bookRef = mFirestore.collection("books");
        bookRef.document(bookid).update("bookSold",true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Book is sold",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Error: Please Try Again!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    if Needed else Delete this
     */
//    private void markAsAvailable(){
//        final CollectionReference bookRef = mFirestore.collection("books");
//        bookRef.document(bookid).update("bookSold",false).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toast.makeText(getApplicationContext(),"Book is sold",Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(),"Error: Please Try Again!",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void addToBookMark() {
        final CollectionReference bookMarkRef = mFirestore.collection("bookmarks");
        bookMarkRef.document(curAppUser).collection("books").document(bookid).set(currentBook).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showSnackbar("Bookmarked!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnackbar("Couldn't add to bookmarks");
            }
        });
    }

    private void removeFromBookMarks() {
        final CollectionReference bookMarkRef = mFirestore.collection("bookmarks");
        bookMarkRef.document(curAppUser).collection("books").document(bookid).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showSnackbar("Removed from bookmarks");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnackbar("Failed to remove from bookmarks");
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static String ordinal(int i) {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        return i + suffixes[i % 10];
    }

    public void showSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT)
                .setAction("OKAY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_orange_light))
                .show();
    }
}
