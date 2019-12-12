package co.in.prodigyschool.passiton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;

import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.util.BookUtil;
import io.opencensus.internal.StringUtils;

public class GetBookSellerLocationActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore mFireStore;
    private static final String TAG = "getLocationActivity";
    private FirebaseAuth mAuth;
    boolean isTextbook;
    String bookName, bookDescription,phoneNumber,userId,bookAddress,bookImageUrl;
    int gradeNumber, boardNumber;
    private EditText locSearchDummy;
    private int bookPrice;
    private TextView locationTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_book_seller_location);
        getSupportActionBar().setTitle("List a book");
        locationTV = (TextView) findViewById(R.id.localityArea);
        locSearchDummy = findViewById(R.id.locationEditTextDummy);
        locSearchDummy.setFocusable(false);
        locSearchDummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchLoc = new Intent(GetBookSellerLocationActivity.this, EnterLocationManuallyActivity.class);
                startActivity(searchLoc);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
//        TODO: Comes after get price activity
        initFireBase();
        populateUserLocation();
        findViewById(R.id.fab18).setOnClickListener(this);
    }

    private void populateUserLocation() {

        try {
            DocumentReference docRef = mFireStore.collection("address").document(userId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String area = document.getString("addr2");
                            String city = document.getString("locality");
                            locationTV.setText("");
                            if(area != null ){
                                locationTV.append(area+", ");
                            }
                            if(city != null){
                                locationTV.append(city);
                            }
                            else {
                                Log.d(TAG, "no address found");
                            }
                        } else {
                            Log.d(TAG, "No address found in firebase");
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "Failed to get address", Snackbar.LENGTH_SHORT)
                                    .setAction("OKAY", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                    .show();
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout, "Failed to get address", Snackbar.LENGTH_SHORT)
                                .setAction("OKAY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                .show();
                    }
                }
            });
        }
        catch(Exception e){
            Log.d(TAG, "get failed with ",e);
            Toast.makeText(getApplicationContext(), "failed to get Address", Toast.LENGTH_SHORT).show();


        }
    }

    private void initFireBase() {
        try {
            mFireStore = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            userId = mAuth.getCurrentUser().getPhoneNumber();
        }
        catch (NullPointerException e){
            Log.e(TAG, "initFireBase: getCurrentUser error", e);
        }

    }

    private void addBook(Book book) {
        // TODO(developer): Add random restaurants
        CollectionReference books = mFireStore.collection("books");
        books.add(book).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
           if(task.isSuccessful()){
               Log.d("Add Book","onComplete: Book added successfully");
               Intent homeIntent = new Intent(GetBookSellerLocationActivity.this,HomeActivity.class);
               homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(homeIntent);
           }
           else{
               Log.d(TAG, "onComplete: failed with",task.getException());
               Toast.makeText(getApplicationContext(),"failed to add book!",Toast.LENGTH_SHORT).show();
           }
            }
        });
    }


    @Override
    public void onClick(View v) {
        try {
            if(locationTV.getText().toString().equals(R.string.getting_loc) || locationTV.getText().toString().isEmpty() || locationTV.getText().toString().equals("") || locationTV.getText().toString().length() == 0){
                Toast.makeText(getApplicationContext(),"Please enter a valid address",Toast.LENGTH_SHORT).show();
                return;
            }
            isTextbook = getIntent().getBooleanExtra("IS_TEXTBOOK", true);
            bookName = getIntent().getStringExtra("BOOK_NAME");
            bookDescription = getIntent().getStringExtra("BOOK_DESCRIPTION");
            gradeNumber = getIntent().getIntExtra("GRADE_NUMBER",4);
            boardNumber = getIntent().getIntExtra("BOARD_NUMBER", 1);
            boardNumber = getIntent().getIntExtra("DEGREE_NUMBER", boardNumber);
            bookPrice = getIntent().getIntExtra("BOOK_PRICE",0);
            bookImageUrl = getIntent().getStringExtra("BOOK_IMAGE_URL");
            bookAddress = locationTV.getText().toString();
            if(userId == null){
                userId = mAuth.getCurrentUser().getPhoneNumber();
            }
            Book book = BookUtil.addBook(userId,isTextbook,bookName,bookDescription,gradeNumber,boardNumber,bookPrice,bookAddress);
            if(bookImageUrl != null && !bookImageUrl.isEmpty()){
                book.setBookPhoto("https://firebasestorage.googleapis.com/v0/b/booksyndy-e8ef6.appspot.com/o/book_photos%2Fbook_photo_1.png?alt=media&token=df4a96f3-3162-4832-b95c-feea08ffcaaa");
            }
            addBook(book);


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "User Register Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}
