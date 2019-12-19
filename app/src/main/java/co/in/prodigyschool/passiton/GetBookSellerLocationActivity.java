package co.in.prodigyschool.passiton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.util.BookUtil;

public class GetBookSellerLocationActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore mFireStore;
    private static final String TAG = "getLocationActivity";
    private FirebaseAuth mAuth;
    boolean isTextbook;
    String bookName, bookDescription,phoneNumber,userId,bookAddress, selectedImage;
    int gradeNumber, boardNumber;
    private EditText locSearchDummy;
    private int bookPrice;
    private TextView locationTV;
    private ProgressDialog progressDialog;

    PlacesClient placesClient;
    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,Place.Field.NAME,Place.Field.ADDRESS);
    AutocompleteSupportFragment places_fragment;

    private double book_lat,book_lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_book_seller_location);
        getSupportActionBar().setTitle("List a book");
        locationTV = (TextView) findViewById(R.id.localityArea);
        initFireBase();
        initPlaces();
        populateUserLocation();
        setupPlaceAutoComplete();

        isTextbook = getIntent().getBooleanExtra("IS_TEXTBOOK", true);
        bookName = getIntent().getStringExtra("BOOK_NAME");
        bookDescription = getIntent().getStringExtra("BOOK_DESCRIPTION");
        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER",4);
        boardNumber = getIntent().getIntExtra("BOARD_NUMBER", 1);
        boardNumber = getIntent().getIntExtra("DEGREE_NUMBER", boardNumber);
//        Toast.makeText(getApplicationContext(),"Board number: "+boardNumber,Toast.LENGTH_SHORT).show();
        bookPrice = getIntent().getIntExtra("BOOK_PRICE",0);
        selectedImage = getIntent().getStringExtra("BOOK_IMAGE_URI");

        findViewById(R.id.fab18).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent finalizeListing = new Intent(GetBookSellerLocationActivity.this, ConfirmListingActivity.class);
                finalizeListing.putExtra("BOOK_IMAGE_URI", selectedImage);
                finalizeListing.putExtra("IS_TEXTBOOK", isTextbook);
                finalizeListing.putExtra("BOOK_NAME",bookName);
                finalizeListing.putExtra("BOOK_DESCRIPTION",bookDescription);
                finalizeListing.putExtra("GRADE_NUMBER",gradeNumber);
                finalizeListing.putExtra("BOARD_NUMBER",boardNumber);
                finalizeListing.putExtra("BOOK_PRICE",bookPrice);
                finalizeListing.putExtra("LATITUDE",book_lat);
                finalizeListing.putExtra("LONGITUDE",book_lng);
                if (!(locationTV.getText().toString().equals(R.string.getting_loc))) {
                    finalizeListing.putExtra("BOOK_LOCATION", locationTV.getText().toString());
                    startActivity(finalizeListing);
                }
                else {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Please wait while we get your location", Snackbar.LENGTH_SHORT)
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

    private void setupPlaceAutoComplete() {
    places_fragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
    places_fragment.setPlaceFields(placeFields);
    places_fragment.setCountry("IN");
    places_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
        @Override
        public void onPlaceSelected(@NonNull Place place) {
            locationTV.setText(place.getName()+","+place.getAddress());
        }

        @Override
        public void onError(@NonNull Status status) {
            Toast.makeText(GetBookSellerLocationActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    });
    }

    private void initPlaces() {
        Places.initialize(this,getString(R.string.places_api_key));
        placesClient = Places.createClient(this);
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
                            book_lat = document.getDouble("lat");
                            book_lng = document.getDouble("lng");
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
               progressDialog.dismiss();
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting");
        progressDialog.setTitle("Creating your listing...");
        progressDialog.setCancelable(false);
        progressDialog.show();
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
            selectedImage = getIntent().getStringExtra("BOOK_IMAGE_URI");
            bookAddress = locationTV.getText().toString();
            if(userId == null){
                userId = mAuth.getCurrentUser().getPhoneNumber();
            }
            Book book = BookUtil.addBook(userId,isTextbook,bookName,bookDescription,gradeNumber,boardNumber,bookPrice,bookAddress,book_lat,book_lng);
            if(selectedImage != null && !selectedImage.isEmpty()){
               // book.setBookPhoto();
            }
            addBook(book);


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "User Register Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}
