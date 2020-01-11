package co.in.prodigyschool.passiton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;

public class EditListingActivity extends AppCompatActivity {


    private static String TAG = "CREATELISTINGFULL";
    private Spinner typeSpinner,gradeSpinner,boardSpinner;
    private double book_lat,book_lng;
    boolean isTextbook, forCompExam;
    private String curUserId, bookName, bookDescription, phoneNumber, userId, bookAddress, bookImageUrl, selectedImage,book_photo_url;
    int gradeNumber, boardNumber, year;
    private int bookPrice;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStore;
    private TextView boardDegreeLabel;
    private Button postButton;
    private ProgressDialog progressDialog;
    private EditText nameField, descField, priceField, locField, yearField;
    private CheckBox competitiveExam, free;
    private ArrayAdapter<String> gradeAdapter, boardAdapter, degreeAdapter, typeAdapter;
    private PlacesClient placesClient;
    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,Place.Field.NAME,Place.Field.ADDRESS);
    AutocompleteSupportFragment places_fragment;
    private ImageView mBookImage;
    private String imageFilePath;
    private Uri selectedImageUri;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference bookPhotosStorageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);

        typeSpinner = findViewById(R.id.bookTypeSpinner);
        gradeSpinner = findViewById(R.id.gradeSpinner);
        boardSpinner = findViewById(R.id.boardSpinner);
        boardDegreeLabel = findViewById(R.id.boardLabel);
        postButton = findViewById(R.id.postButton);
        competitiveExam = findViewById(R.id.forCompetitiveExams);
        nameField = findViewById(R.id.bookNameField);
        descField = findViewById(R.id.bookDescField2);
        locField = findViewById(R.id.locField2);
        priceField = findViewById(R.id.priceField);
        yearField = findViewById(R.id.bookYearField1);
        free = findViewById(R.id.freeOrNot);
        mBookImage = findViewById(R.id.book_image);


        getSupportActionBar().setTitle("Edit listing");

        if(getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }


    private void initFireBase() {
        try {
            mFireStore = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            userId = mAuth.getCurrentUser().getPhoneNumber();
            mFirebaseStorage = FirebaseStorage.getInstance();
            bookPhotosStorageReference = mFirebaseStorage.getReference().child("book_photos");
        }
        catch (NullPointerException e){
            Log.e(TAG, "initFireBase: getCurrentUser error", e);
        }

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
                            locField.setText("");
                            if(area != null ){
                                locField.append(area+", ");
                            }
                            if(city != null){
                                locField.append(city);
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

}
