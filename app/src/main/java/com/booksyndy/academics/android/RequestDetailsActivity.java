package com.booksyndy.academics.android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.booksyndy.academics.android.Data.BookRequest;
import com.booksyndy.academics.android.Data.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestDetailsActivity extends AppCompatActivity implements EventListener<DocumentSnapshot> {

    private boolean isTextbook;
    private BookRequest currentBook;
    private User bookOwner;
    private TextView view_bookname, view_address, view_description, view_grade_and_board, view_type, sellerName;
    private FirebaseFirestore mFirestore;
    private DocumentReference bookUserRef;
    private DocumentReference bookRef;
    private ListenerRegistration mBookUserRegistration;
    private CircleImageView sellerDp;
    private Menu menu;
    private static final String TAG = "REQUEST_DETAILS";
    private String curAppUser, bookOwnerPhone, shareableLink = "", bookid;
    private double latA, lngA, bookLat, bookLng;
    private boolean byme;
    private FloatingActionButton chat;

    private int gradeNumber, boardNumber, yearNumber;
    private String bookName, bookDescription, phoneNumber, userId, bookAddress;
    private SharedPreferences userPref;

    public static String ordinal(int i) {
        String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        return i + suffixes[i % 10];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);
        getSupportActionBar().setTitle("View request");
        userPref = this.getSharedPreferences(getString(R.string.UserPref), 0);
        bookid = getIntent().getStringExtra("bookid");
        byme = getIntent().getBooleanExtra("byme", false);
        view_bookname = findViewById(R.id.reqTitle);
        view_description = findViewById(R.id.reqDescription);
        view_type = findViewById(R.id.reqType);
        view_grade_and_board = findViewById(R.id.reqCategory);
        view_address = findViewById(R.id.reqLoc);
        sellerName = findViewById(R.id.sellerName1);
        sellerDp = findViewById(R.id.seller_dp1);
        chat = findViewById(R.id.fab_chatreq);

        bookName = getIntent().getStringExtra("REQ_TITLE");
        bookDescription = getIntent().getStringExtra("REQ_DESC");
        gradeNumber = getIntent().getIntExtra("REQ_GRADENUMBER", 0);
        boardNumber = getIntent().getIntExtra("REQ_BOARDNUMBER", 6);
        yearNumber = getIntent().getIntExtra("REQ_YEAR", 0);
        isTextbook = getIntent().getBooleanExtra("REQ_ISTB", true);
        bookAddress = getIntent().getStringExtra("REQ_ADDRESS");
        bookOwnerPhone = getIntent().getStringExtra("REQ_PHONE");
        bookLat = getIntent().getDoubleExtra("REQ_LAT", 0.0);
        bookLat = getIntent().getDoubleExtra("REQ_LNG", 0.0);
        curAppUser = userPref.getString(getString(R.string.p_userphone), "");
        latA = userPref.getFloat(getString(R.string.p_lat), 0.0f);
        lngA = userPref.getFloat(getString(R.string.p_lng), 0.0f);
        initFireStore();

        if (bookOwnerPhone != null && bookOwnerPhone.equalsIgnoreCase(curAppUser)) {
            chat.hide();
            sellerName.setEnabled(false);
            sellerDp.setEnabled(false);
        } else {
            chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (bookOwner != null) {
                        Intent chatIntent = new Intent(RequestDetailsActivity.this, ChatActivity.class);
                        chatIntent.putExtra("visit_user_id", bookOwnerPhone); // phone number
                        chatIntent.putExtra("visit_user_name", bookOwner.getUserId()); // unique user id
                        chatIntent.putExtra("visit_image", bookOwner.getImageUrl());
                        startActivity(chatIntent);
                    }
                }
            });

        }

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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        view_bookname.setText(bookName);
        view_description.setText(bookDescription);
        if (isTextbook) {
            view_type.setText("Textbook");
        } else {
            view_type.setText("Notes / other material");
        }
        if (boardNumber == 20) {
            view_grade_and_board.setText("Competitive exams");
        } else {
            if (gradeNumber == 1) {
                view_grade_and_board.setText(R.string.g5b);
            } else if (gradeNumber == 2) {
                view_grade_and_board.setText(R.string.g68);
            } else if (gradeNumber == 3) {
                view_grade_and_board.setText(R.string.g9);
            } else if (gradeNumber == 4) {
                view_grade_and_board.setText(R.string.g10);
            } else if (gradeNumber == 5) {
                view_grade_and_board.setText(R.string.g11);
            } else if (gradeNumber == 6) {
                view_grade_and_board.setText(R.string.g12);
            } else if (gradeNumber == 7) {
                view_grade_and_board.setText(R.string.undergraduate);
//                Toast.makeText(getApplicationContext(),"Grade number: 7\nBoard number: "+boardNumber,Toast.LENGTH_SHORT).show();

                if (boardNumber == 7) {
                    view_grade_and_board.append(", B. Tech");
                } else if (boardNumber == 8) {
                    view_grade_and_board.append(", B. Sc");
                } else if (boardNumber == 9) {
                    view_grade_and_board.append(", B. Com");
                } else if (boardNumber == 10) {
                    view_grade_and_board.append(", BA");
                } else if (boardNumber == 11) {
                    view_grade_and_board.append(", BBA");
                } else if (boardNumber == 12) {
                    view_grade_and_board.append(", BCA");
                } else if (boardNumber == 13) {
                    view_grade_and_board.append(", B. Ed");
                } else if (boardNumber == 14) {
                    view_grade_and_board.append(", LLB");
                } else if (boardNumber == 15) {
                    view_grade_and_board.append(", MBBS");
                } else if (boardNumber == 16) {
                    view_grade_and_board.append(", other degree");
                }

                view_grade_and_board.append(", " + ordinal(yearNumber) + " year");
            }

            if (boardNumber == 1) {
                view_grade_and_board.append(", CBSE");
            } else if (boardNumber == 2) {
                view_grade_and_board.append(", ICSE//ISC");
            } else if (boardNumber == 3) {
                view_grade_and_board.append(", IB");
            } else if (boardNumber == 4) {
                view_grade_and_board.append(", IGCSE/CAIE");
            } else if (boardNumber == 5) {
                view_grade_and_board.append(", state board");
            } else if (boardNumber == 6) {
                view_grade_and_board.append(", other board");
            }
        }
        view_address.setText(bookAddress);
    }

    private void populateReqDetails(User user) {

        try {
            bookOwner = user;
            sellerName.setText(bookOwner.getUserId());
            Glide.with(sellerDp.getContext())
                    .load(bookOwner.getImageUrl())
                    .into(sellerDp);

        } catch (Exception e) {
            Log.e(TAG, "populateBookDetails: exception", e);
        }

        addDistance(bookLat, bookLng);
    }

    private void addDistance(double latitude, double longitude) {
        float res;
        if (latA != 0.0 && lngA != 0.0 && latitude != 0.0 && longitude != 0.0) {
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
                    view_address.append("  " + getString(R.string.divider_bullet) + " " + (int) res + " m");
            } else if (res > 1000f) {
                res = Math.round(res / 100);
                res = res / 10;
                if (res > 0.0f)
                    view_address.append(" " + getString(R.string.divider_bullet) + " " + res + " km");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!byme) {
            getMenuInflater().inflate(R.menu.menu_request_details, menu);
            this.menu = menu;
        } else {
            getMenuInflater().inflate(R.menu.menu_myreq, menu);
            this.menu = menu;
        }

        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.reportRequest:
                AlertDialog.Builder rBuilder = new AlertDialog.Builder(RequestDetailsActivity.this);
                rBuilder.setTitle("Report request");
                rBuilder.setIcon(R.drawable.ic_report_24px_outlined);
                rBuilder.setMessage("Are you sure you want to report this request?");
                rBuilder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        reportRequest();
                        Toast.makeText(getApplicationContext(), "Reported", Toast.LENGTH_SHORT).show();

                        reportListing();
                        onBackPressed();
                    }
                });
                rBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                rBuilder.show();
                break;
            case R.id.deleteRequest:
                AlertDialog.Builder dBuilder = new AlertDialog.Builder(RequestDetailsActivity.this);
                dBuilder.setTitle("Delete this request?");
                dBuilder.setMessage("Once you delete a request, it can't be restored.");
                dBuilder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bookRef.update("complete", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Error Deleting Request", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
                dBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dBuilder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void reportListing() {
        if (!checkConnection(this)) {
            Toast.makeText(this, "InterNet Required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (curAppUser != null) {
            final DocumentReference reportRef = mFirestore.collection("report_request").document(bookid);
//            final DocumentReference bookRef = mFirestore.collection("bookRequest").document(currentBook.getDocumentId());
            reportRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    if (snapshot.exists()) {
                        //update here
                        reportRef.update("Report count", FieldValue.increment(1));
                    } else {
                        Map<String, Object> bookDetails = new HashMap<>();
                        bookDetails.put("bookRef", bookRef);
                        bookDetails.put("Reported By", curAppUser);
                        bookDetails.put("Report count", FieldValue.increment(1));
                        reportRef.set(bookDetails);
                    }
                }
            });


        }


    }

    private void initFireStore() {
        mFirestore = FirebaseFirestore.getInstance();

        try {
            bookRef = mFirestore.collection("bookRequest").document(bookid);
            bookUserRef = mFirestore.collection("users").document(bookOwnerPhone);
//            curAppUser = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            mBookUserRegistration = bookUserRef.addSnapshotListener(this);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Firebase error", Toast.LENGTH_SHORT).show();
        }
    }

    //called for mBookRegistration
    @Override
    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "book:onEvent", e);
            return;
        }
        if (snapshot != null && snapshot.exists())
            populateReqDetails(snapshot.toObject(User.class));
        else {
            Toast.makeText(getApplicationContext(), "Book sold or unavailable", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }
}
