package co.in.prodigyschool.passiton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
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
import co.in.prodigyschool.passiton.util.BookUtil;

public class EditListingActivity extends AppCompatActivity {


    private static String TAG = "CREATELISTINGFULL";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 108;
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
    private String documentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);

        typeSpinner = findViewById(R.id.bookTypeSpinner_e);
        gradeSpinner = findViewById(R.id.gradeSpinner_e);
        boardSpinner = findViewById(R.id.boardSpinner_e);
        boardDegreeLabel = findViewById(R.id.boardLabel_e);
        postButton = findViewById(R.id.postButton_e);
        competitiveExam = findViewById(R.id.forCompetitiveExams_e);
        nameField = findViewById(R.id.bookNameField_e);
        descField = findViewById(R.id.bookDescField_e);
        locField = findViewById(R.id.locField_e);
        priceField = findViewById(R.id.priceField_e);
        yearField = findViewById(R.id.bookYearField_e);
        free = findViewById(R.id.freeOrNot_e);
        mBookImage = findViewById(R.id.book_image_e);


        getSupportActionBar().setTitle("Edit listing");

        if(getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        findViewById(R.id.btn_search_listing_e).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchCalled();
            }
        });

        initFireBase();


        gradeAdapter = new ArrayAdapter<String>(EditListingActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.grades));
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(gradeAdapter);

        boardAdapter = new ArrayAdapter<String>(EditListingActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.boards));
        boardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boardSpinner.setAdapter(boardAdapter);

        degreeAdapter = new ArrayAdapter<String>(EditListingActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.degrees));
        degreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        typeAdapter = new ArrayAdapter<String>(EditListingActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.types));
        degreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        mAuth = FirebaseAuth.getInstance();

        //TODO: get other details as well
        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER",4);
        boardNumber = getIntent().getIntExtra("BOARD_NUMBER", 6);
        bookName = getIntent().getStringExtra("BOOK_NAME");
        bookDescription = getIntent().getStringExtra("BOOK_DESC");
        bookAddress = getIntent().getStringExtra("BOOK_ADDRESS");
        book_photo_url = getIntent().getStringExtra("BOOK_PHOTO");
        book_lat = getIntent().getDoubleExtra("BOOK_LAT",0.0);
        book_lng = getIntent().getDoubleExtra("BOOK_LNG",0.0);
        isTextbook = getIntent().getBooleanExtra("BOOK_TYPE",false);
        bookPrice = getIntent().getIntExtra("BOOK_PRICE",0);
        year = getIntent().getIntExtra("BOOK_YEAR",0);
        documentId = getIntent().getStringExtra("DOCUMENT_ID");

        gradeSpinner.setSelection(gradeNumber - 1);

        if (gradeNumber>=7) {
            boardDegreeLabel.setText("Degree / course");
            boardSpinner.setSelection(boardNumber-7);
        }
        else {
            boardDegreeLabel.setText("Board");
            boardSpinner.setSelection(boardNumber-1);
        }

        if (year>0) {
            yearField.setText(year + "");
        }

        nameField.setText(bookName);
        descField.setText(bookDescription);
        locField.setText(bookAddress);
        Glide.with(mBookImage.getContext()).load(book_photo_url).into(mBookImage);
        if(isTextbook){
            typeSpinner.setSelection(0);
        }
        else{
            typeSpinner.setSelection(1);
        }
       priceField.setText(bookPrice+"");
        if(bookPrice == 0){
            free.setChecked(true);
           priceField.setEnabled(false);
        }else{
            free.setChecked(false);
        }


        mBookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookName = nameField.getText().toString().trim();
                bookDescription = descField.getText().toString().trim();
                bookAddress = locField.getText().toString().trim();
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
                else if (book_photo_url==null || book_photo_url.length()==0) {
                    showSnackbar("Please take a picture of your book");
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


    private void initFireBase() {
        try {
            mFireStore = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            userId = mAuth.getCurrentUser().getPhoneNumber();
            mFirebaseStorage = FirebaseStorage.getInstance();
            bookPhotosStorageReference = mFirebaseStorage.getReference().child("book_photos");
            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), getString(R.string.places_api_key));
            }
            PlacesClient placesClient = Places.createClient(this);
        }
        catch (NullPointerException e){
            Log.e(TAG, "initFireBase: getCurrentUser error", e);
        }
    }



    private void uploadBook() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Just a moment...");
        progressDialog.setTitle("Saving your changes");
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
            books.document(documentId).set(book).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d("Add Book","onComplete: Book added successfully");
                    progressDialog.dismiss();
                    Intent homeIntent = new Intent(EditListingActivity.this,HomeActivity.class);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    homeIntent.putExtra("SNACKBAR_MSG", "Your edits have been saved");
                    startActivity(homeIntent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onComplete: failed with",e);
                    Toast.makeText(getApplicationContext(),"failed to add book!",Toast.LENGTH_SHORT).show();
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

    public void onSearchCalled() {
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("IN") //NIGERIA
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }


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
            case AUTOCOMPLETE_REQUEST_CODE: // for places search
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(imageReturnedIntent);
                    //Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                    //Toast.makeText(CreateListingActivity.this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();

                    book_lat = place.getLatLng().latitude;
                    book_lng = place.getLatLng().longitude;
                    //returns list of addresses, take first one and send info to result receiver
                    Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(
                                book_lat,
                                book_lng,
                                1);
                    } catch (Exception ioException) {
                        Log.e("", "Error in getting address for the location");
                    }

                    if (addresses == null || addresses.size()  == 0) {
                        Log.e(TAG, "onActivityResult: Error Finding Address" );
                        Toast.makeText(getApplicationContext(), "Error Fetching Address", Toast.LENGTH_SHORT).show();
                    } else {

                        Address address = addresses.get(0);
                        if(address.getSubLocality() != null || address.getLocality() != null){
                            locField.setText("");
                        }
                        if(address.getSubLocality() != null){
                            locField.setText(address.getSubLocality());
                            locField.append(", ");
                        }
                        if(address.getLocality() != null){
                            locField.append(address.getLocality());
                        }
                    }

                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    Status status = Autocomplete.getStatusFromIntent(imageReturnedIntent);
                    Toast.makeText(EditListingActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                    Log.i(TAG, status.getStatusMessage());
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
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
//        progressDialog.show();
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

}
