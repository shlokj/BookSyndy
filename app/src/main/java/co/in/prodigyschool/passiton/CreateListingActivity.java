package co.in.prodigyschool.passiton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.Data.User;
import co.in.prodigyschool.passiton.util.BookUtil;

public class CreateListingActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_create_listing);
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


        getSupportActionBar().setTitle("Create a listing");

        if(getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        initFireBase();
        populateUserLocation();
        //initPlaces();
        //setupPlaceAutoComplete();


        gradeAdapter = new ArrayAdapter<String>(CreateListingActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.grades));
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(gradeAdapter);

        boardAdapter = new ArrayAdapter<String>(CreateListingActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.boards));
        boardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boardSpinner.setAdapter(boardAdapter);

        degreeAdapter = new ArrayAdapter<String>(CreateListingActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.degrees));
        degreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        typeAdapter = new ArrayAdapter<String>(CreateListingActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.types));
        degreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        mAuth = FirebaseAuth.getInstance();

        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER",4);
        boardNumber = getIntent().getIntExtra("BOARD_NUMBER", 6);

        mBookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: add dialog to choose between camera or gallery
//                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(pickPhoto, 1);
                openCameraIntent();
            }
        });

        // to disallow enter
        nameField.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            public void afterTextChanged(Editable s) {

                for(int i = s.length(); i > 0; i--) {

                    if(s.subSequence(i-1, i).toString().equals("\n"))
                        s.replace(i-1, i, "");
                }
            }
        });

        competitiveExam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                gradeSpinner.setEnabled(!isChecked);
                gradeSpinner.setFocusable(!isChecked);
                gradeSpinner.setFocusableInTouchMode(!isChecked);
                boardSpinner.setEnabled(!isChecked);
                boardSpinner.setFocusable(!isChecked);
                boardSpinner.setFocusableInTouchMode(!isChecked);
            }
        });
        free.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!(free.isChecked())) {
                    priceField.setEnabled(true);
                    priceField.setFocusableInTouchMode(true);
                    priceField.setFocusable(true);
                    priceField.setText("");
                    priceField.requestFocus();
                }
                else {
                    priceField.setEnabled(false);
                    priceField.setFocusableInTouchMode(false);
                    priceField.setFocusable(false);
                    priceField.setText("0");
                }
            }
        });

        gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position<6) {
                    boardDegreeLabel.setText("Board");
                    boardSpinner.setAdapter(boardAdapter);
                    yearField.setVisibility(View.GONE);
                    if (position==4 || position==5) {
                        competitiveExam.setVisibility(View.VISIBLE);
                    }
                    else {
                        competitiveExam.setVisibility(View.GONE);
                    }
                }
                else {
                    boardDegreeLabel.setText("Degree / course");
                    boardSpinner.setAdapter(degreeAdapter);
                    yearField.setVisibility(View.VISIBLE);
                    competitiveExam.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        autoFillGradeAndBoard();

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookName = nameField.getText().toString();
                bookDescription = descField.getText().toString();
                bookAddress = locField.getText().toString();
                isTextbook = typeSpinner.getSelectedItemPosition()==0;
                gradeNumber = gradeSpinner.getSelectedItemPosition()+1;
                if (gradeNumber>=7) {
                    boardNumber = boardSpinner.getSelectedItemPosition() + 7;
                }
                else {
                    boardNumber = boardSpinner.getSelectedItemPosition() + 1;
                }
                String bps = priceField.getText().toString().trim();
                String bys;
                forCompExam = competitiveExam.isChecked();

                if (bookName.length()<10) {
                    showSnackbar("Please enter at least 10 characters for your book's name");
                }
                else if (bookDescription.length()<10) {
                    showSnackbar("Please enter at least 10 characters for the description");
                }
                else if (gradeNumber>=7 && yearField.getText().toString().length()==0) {
                    showSnackbar("Please enter a year for your book");
                }
                else if (bookAddress.length()==0) {
                    showSnackbar("Couldn't get your location. Please enter it manually.");
                }
                else if (bps.length()==0) {
                    showSnackbar("Please enter a price or give it for free");
                }
                else {
                    boolean validYear = true;
                    bookPrice = Integer.parseInt(bps);
                    if (gradeNumber>=7) {
//                        Toast.makeText(getApplicationContext(),"Undergrad",Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getApplicationContext(),"Board number: "+boardNumber,Toast.LENGTH_SHORT).show();

                        bys = yearField.getText().toString().trim();
                        if (bys.length()==0) {
                            showSnackbar("Please enter a year for your book");
                        }
                        else {
                            year = Integer.parseInt(bys);

                            if (boardNumber == 7) {

                                if (year > 4 || year == 0) {
                                    validYear = false;
                                    displaySnackbarYears(4);
                                }
                            } else if (boardNumber == 8) {
                                if (year > 4 || year == 0) {
                                    validYear = false;
                                    displaySnackbarYears(4);
                                }
                            } else if (boardNumber == 9) {
                                if (year > 4 || year == 0) {
                                    validYear = false;
                                    displaySnackbarYears(4);
                                }
                            } else if (boardNumber == 10) {
                                if (year > 4 || year == 0) {
                                    validYear = false;
                                    displaySnackbarYears(4);
                                }
                            } else if (boardNumber == 11) {
                                if (year > 4 || year == 0) {
                                    validYear = false;
                                    displaySnackbarYears(4);
                                }
                            } else if (boardNumber == 12) {
                                if (year > 4 || year == 0) {
                                    validYear = false;
                                    displaySnackbarYears(4);
                                }
                            } else if (boardNumber == 13) {
                                if (year > 4 || year == 0) {
                                    validYear = false;
                                    displaySnackbarYears(4);
                                }
                            } else if (boardNumber == 14) {
                                if (year > 5 || year == 0) {
                                    validYear = false;
                                    displaySnackbarYears(5);
                                }
                            } else if (boardNumber == 15) {
                                boardNumber = 15;
//                                Toast.makeText(getApplicationContext(),"MBBS",Toast.LENGTH_LONG).show();
                                if (year > 6 || year == 0) {
                                    validYear = false;
                                    displaySnackbarYears(6);
                                }
                            } else if (boardNumber == 16) {
                                if (year == 0) {
                                    validYear = false;
                                    View parentLayout = findViewById(android.R.id.content);
                                    Snackbar.make(parentLayout, "Your year can't be 0", Snackbar.LENGTH_SHORT)
                                            .setAction("OKAY", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                }
                                            })
                                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                            .show();
                                }
                            }
                        }
                        if (bys.length()==0) {
                            showSnackbar("Please enter a year for your book");
                        }
                        else {
                            year=Integer.parseInt(bys);
                        }
                    }
                    else {
                        year=0;
                    }
                    if (validYear) {
//                        Toast.makeText(getApplicationContext(),"Valid, start upload",Toast.LENGTH_SHORT).show();
                        uploadBook();
                    }
                }
            }
        });
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


    private void autoFillGradeAndBoard() {
        if (!checkConnection(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),"Internet Required",Toast.LENGTH_LONG).show();
            return;
        }
        mFireStore = FirebaseFirestore.getInstance();
        try {
            curUserId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            DocumentReference userReference =  mFireStore.collection("users").document(curUserId);
            userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    co.in.prodigyschool.passiton.Data.User user = snapshot.toObject(User.class);
                    if(user != null) {
                        gradeNumber=user.getGradeNumber();
                        boardNumber=user.getBoardNumber();

                        gradeSpinner.setAdapter(gradeAdapter);
                        gradeSpinner.setSelection(gradeNumber-1);

                        if (gradeNumber>=7) {
                            boardDegreeLabel.setText("Degree / course");
//
//                            findViewById(R.id.boardLL).setVisibility(View.INVISIBLE);
//                            findViewById(R.id.collegeDegreeAndYearLL).setVisibility(View.VISIBLE);

                            boardDegreeLabel.setText("Degree / Board");
                            boardSpinner.setAdapter(degreeAdapter);
                            boardSpinner.setSelection(boardNumber-7);
                        }
                        else {
                            boardDegreeLabel.setText("Board");
                            boardSpinner.setAdapter(boardAdapter);
                            boardSpinner.setSelection(boardNumber-1);
                        }
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


    private void uploadBook() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting...");
        progressDialog.setTitle("Creating your listing");
        progressDialog.setCancelable(false);
        progressDialog.show();
        try {

            if(userId == null){
                userId = mAuth.getCurrentUser().getPhoneNumber();
            }
            Book book = BookUtil.addBook(userId,isTextbook,bookName,bookDescription,gradeNumber,boardNumber,bookPrice,bookAddress,book_lat,book_lng);
            book.setBookYear(year);
            if(book_photo_url != null && !book_photo_url.isEmpty()){
                // book.setBookPhoto();
                book.setBookPhoto(book_photo_url);
            }

            CollectionReference books = mFireStore.collection("books");
            books.add(book).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        Log.d("Add Book","onComplete: Book added successfully");
                        progressDialog.dismiss();
                        Intent homeIntent = new Intent(CreateListingActivity.this,HomeActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (isTextbook) {
                            homeIntent.putExtra("SNACKBAR_MSG", "Your book has been listed!");
                        }
                        else {
                            homeIntent.putExtra("SNACKBAR_MSG", "Your material has been listed!");
                        }
                        startActivity(homeIntent);
                    }
                    else{
                        Log.d(TAG, "onComplete: failed with",task.getException());
                        Toast.makeText(getApplicationContext(),"failed to add book!",Toast.LENGTH_SHORT).show();
                    }
                }
            });


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "User Register Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    public void showSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT)
                .setAction("OKAY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent homeActivity = new Intent(CreateListingActivity.this, HomeActivity.class);
        startActivity(homeActivity);
    }

    public void displaySnackbarYears(int year) {
        String yearNum = Integer.valueOf(year).toString();
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, "Please enter a valid year " + yearNum + " or below, and not 0", Snackbar.LENGTH_SHORT)
                .setAction("OKAY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
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

    private void initPlaces() {
        Places.initialize(this,getString(R.string.places_api_key));
        placesClient = Places.createClient(this);
    }

//    private void setupPlaceAutoComplete() {
//        places_fragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment2);
//        places_fragment.setPlaceFields(placeFields);
//        places_fragment.setCountry("IN");
//        places_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(@NonNull Place place) {
//                locField.setText(place.getName()+","+place.getAddress());
//                LatLng latLng= place.getLatLng();
//                book_lat = latLng.latitude;
//                book_lng = latLng.longitude;
//            }
//
//            @Override
//            public void onError(@NonNull Status status) {
//                Toast.makeText(CreateListingActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {

/*            case PIC_CROP:

                if (imageReturnedIntent != null) {
                    // get the returned data
                    Bundle extras = imageReturnedIntent.getExtras();

                    Bitmap selectedBitmap = extras.getParcelable("data");

                    chosenPic.setImageBitmap(selectedBitmap);
                }*/

            case 0:// camera intent
                if (resultCode == RESULT_OK ) {

                    Glide.with(this).load(imageFilePath).into(mBookImage);
                    File f = new File(imageFilePath);
                    selectedImageUri = Uri.fromFile(f);
                    storeBookImage(selectedImageUri);
                }
                break;

            case 1:// gallery intent
                if (resultCode == RESULT_OK && imageReturnedIntent != null && imageReturnedIntent.getData() != null) {
                    selectedImageUri = imageReturnedIntent.getData();
                    //performCrop(selectedImageUri);
                    storeBookImage(selectedImageUri);
                    Glide.with(this).load(selectedImageUri).into(mBookImage);
                }
                break;


        }
    }

    private File createImageFile() throws IOException {

        String imageFileName = "BOOK" + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "co.in.prodigyschool.passiton.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(pictureIntent,
                        0);
            }
        }
    }

    private void storeBookImage(Uri selectedImageUri) {
        //show progress
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        try {
            String timeStamp =
                    new SimpleDateFormat("yyyyMMdd_HHmmss",
                            Locale.getDefault()).format(new Date());
            // Get a reference to store file at book_photos/<FILENAME>
            final StorageReference photoRef = bookPhotosStorageReference.child(timeStamp + "_" + selectedImageUri.getLastPathSegment());

            // Upload file to Firebase Storage
            UploadTask uploadTask = photoRef.putFile(selectedImageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "then: failure download url", task.getException());
                        progressDialog.dismiss();
                    }

                    // Continue with the task to get the download URL
                    return photoRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        book_photo_url = task.getResult().toString();
//                        Toast.makeText(getApplicationContext(), "upload success", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Log.d(TAG, "onComplete: success url: " + book_photo_url);
                    } else {
                        // Handle failures
                        progressDialog.dismiss();
                        Log.e(TAG, "onComplete: failure", task.getException());
                    }
                }
            });

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    //displaying percentage in progress dialog
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }


    }
}
