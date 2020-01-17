package co.in.prodigyschool.passiton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.Data.BookRequest;
import co.in.prodigyschool.passiton.Data.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class RequestDetailsActivity extends AppCompatActivity {

    private BookRequest currentBook;
    private User bookOwner;
    private TextView view_bookname,view_address,view_description, view_grade_and_board, sellerName;
    private FirebaseFirestore mFirestore;
    private DocumentReference bookUserRef;
    private DocumentReference bookRef;
    private ListenerRegistration mBookUserRegistration,mBookRegistration,mBookMarkRegistration;
    private CircleImageView sellerDp;
    private static final String TAG = "REQUEST_DETAILS";
    private String curAppUser, shareableLink="", bookid;
    private double latA,lngA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        getSupportActionBar().setTitle("View request");
        bookid = getIntent().getStringExtra("bookid");

        view_bookname = findViewById(R.id.reqTitle);
        view_description = findViewById(R.id.reqDescription);
        view_grade_and_board = findViewById(R.id.reqCategory);
        view_address = findViewById(R.id.reqLoc);
        sellerName = findViewById(R.id.sellerName1);
        sellerDp = findViewById(R.id.seller_dp1);

        initFireStore();

        findViewById(R.id.fab_chatreq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open chat
            }
        });

        View.OnClickListener toOpenProfile = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewProfile = new Intent(RequestDetailsActivity.this, ViewUserProfileActivity.class);
                viewProfile.putExtra("USER_PHONE", bookOwner.getPhone());
                viewProfile.putExtra("USER_NAME", bookOwner.getFirstName() + " " + bookOwner.getLastName());
                viewProfile.putExtra("USER_ID", bookOwner.getUserId());
                viewProfile.putExtra("USER_PHOTO", bookOwner.getImageUrl());
                startActivity(viewProfile);
            }
        };

        sellerName.setOnClickListener(toOpenProfile);
        sellerDp.setOnClickListener(toOpenProfile);

        if(getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }


    private void populateReqDetails(BookRequest book) {

        try {
            currentBook = book;
            if(currentBook == null){
                Log.d(TAG, "populateBookDetails: current book error");
                return;
            }
            bookUserRef = mFirestore.collection("bookRequest").document(currentBook.getUserId());
            view_bookname.setText(currentBook.getTitle());
            view_description.setText(currentBook.getDescription());
            view_address.setText(currentBook.getBookAddress());
            int gradeNumber = currentBook.getGrade();
            int boardNumber = currentBook.getBoard();
            int year = currentBook.getBookYear();

            if (currentBook.isText()) {
                view_grade_and_board.setText("Textbook " + getString(R.string.divider_bullet) + " ");
            } else {
                view_grade_and_board.setText("Notes / material " + getString(R.string.divider_bullet) + " ");
            }

            if (boardNumber == 20) {
                view_grade_and_board.append("Competitive exams");
            }
            else {
                if (gradeNumber == 1) {
                    view_grade_and_board.append("Grade 5 or below");
                } else if (gradeNumber == 2) {
                    view_grade_and_board.append("Grade 6 to 8");
                } else if (gradeNumber == 3) {
                    view_grade_and_board.append("Grade 9");
                } else if (gradeNumber == 4) {
                    view_grade_and_board.append("Grade 10");
                } else if (gradeNumber == 5) {
                    view_grade_and_board.append("Grade 11");
                } else if (gradeNumber == 6) {
                    view_grade_and_board.append("Grade 12");
                } else if (gradeNumber == 7) {
                    view_grade_and_board.append("Undergraduate");
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

            }

            mBookUserRegistration = bookUserRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "book:onEvent", e);
                        return;
                    }
                    bookOwner = snapshot.toObject(User.class);
                    if (bookOwner!=null) {
                        sellerName.setText(bookOwner.getUserId());
                        Glide.with(sellerDp.getContext())
                                .load(bookOwner.getImageUrl())
                                .into(sellerDp);
                    }
                }
            });
        }
        catch(Exception e){
            Log.e(TAG, "populateBookDetails: exception",e);
        }

        addDistance(currentBook.getLat(),currentBook.getLng());
    }


    private void addDistance(double latitude,double longitude){
        float res;
        if(latA != 0.0 && lngA != 0.0 && latitude != 0.0 && longitude != 0.0) {
            Location locationA = new Location("point A");
            Location locationB = new Location("point B");

            locationA.setLatitude(latA);
            locationA.setLongitude(lngA);
            locationB.setLatitude(latitude);
            locationB.setLongitude(longitude);
            res = locationA.distanceTo(locationB);
            if (res > 0.0f && res < 1000f) {
                res = Math.round(res);
                if (res > 0.0f)
                    view_address.append("  " + getString(R.string.divider_bullet) + " " + (int)res + " m");
            }
            else if(res > 1000f){
                res = Math.round(res / 100);
                res = res / 10;
                if (res > 0.0f)
                    view_address.append(" " + getString(R.string.divider_bullet) + " " + res + " km");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initFireStore() {
        mFirestore = FirebaseFirestore.getInstance();

        if(mFirestore != null){
            bookRef =  mFirestore.collection("bookRequest").document(bookid);
            curAppUser = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        }
        else{
            Toast.makeText(getApplicationContext(),"Firebase error",Toast.LENGTH_SHORT).show();
        }
    }

    public static String ordinal(int i) {
        String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        return i + suffixes[i % 10];
    }

}
