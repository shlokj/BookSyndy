package com.booksyndy.academics.android;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.booksyndy.academics.android.Data.Book;
import com.booksyndy.academics.android.util.BookUtil;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateGeneralListingActivity extends AppCompatActivity {

    private static final int CROP_IMAGE = 2;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 108;
    private static String TAG = "CREATELISTINGFULLGEN";
    private Spinner typeSpinner;
    private double book_lat,book_lng;
    private boolean isTextbook, detailsChanged = false;
    private String curUserId, bookName, bookDescription, phoneNumber, userId, bookAddress,book_photo_url;
    private int bookPrice;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStore;
    private Button postButton;
    private ProgressDialog progressDialog;
    private EditText nameField, descField, priceField, locField;
    private CheckBox free;
    private ArrayAdapter<String> typeAdapter;
    private PlacesClient placesClient;
    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,Place.Field.NAME,Place.Field.ADDRESS);
    AutocompleteSupportFragment places_fragment;
    private ImageView mBookImage;
    private String imageFilePath;
    private Uri selectedImageUri;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference bookPhotosStorageReference;
    private SharedPreferences userPref;
    private TextWatcher checkChange;
    private double progress;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 3;

// TODO: type implementaion (fiction/nonfiction)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_general_listing);
        typeSpinner = findViewById(R.id.bookTypeSpinner_g);
        postButton = findViewById(R.id.postButton_g);
        nameField = findViewById(R.id.bookNameField_g);
        descField = findViewById(R.id.bookDescField2_g);
        locField = findViewById(R.id.locField2_g);
        priceField = findViewById(R.id.priceField_g);
        free = findViewById(R.id.freeOrNot_g);
        mBookImage = findViewById(R.id.book_image_g);
        userPref = this.getSharedPreferences(getString(R.string.UserPref),0);

        phoneNumber  = getIntent().getStringExtra("PHONE_NUMBER");

        getSupportActionBar().setTitle("Create a listing");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkChange = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                detailsChanged=true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        nameField.addTextChangedListener(checkChange);
        descField.addTextChangedListener(checkChange);
        priceField.addTextChangedListener(checkChange);


        initFireBase();
        populateUserLocation();
//        locField.setEnabled(false);
        findViewById(R.id.btn_search_listing_g).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchCalled();
            }
        });

        typeAdapter = new ArrayAdapter<String>(CreateGeneralListingActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.general_types));

        typeSpinner.setAdapter(typeAdapter);

        mAuth = FirebaseAuth.getInstance();


        mBookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(CreateGeneralListingActivity.this);
//                builder.setTitle("Select Pic Using...");
                builder.setItems(options, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            openCameraIntent();
                        } else if (options[item].equals("Choose from Gallery")) {

                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, 1);

                        }
                    }
                });

                builder.show();
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


        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndUploadBook();
            }
        });

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
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
            Book book = BookUtil.addBook(userId,isTextbook,bookName,bookDescription,0,0,bookPrice,bookAddress,book_lat,book_lng);
            book.setGeneral(true);
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
                        Intent homeIntent = new Intent(CreateGeneralListingActivity.this,HomeActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (isTextbook) {
                            homeIntent.putExtra("SNACKBAR_MSG", "Your book has been listed! You can find it in the \'Your listings\' section of the app.");
                        }
                        else {
                            homeIntent.putExtra("SNACKBAR_MSG", "Your material has been listed! You can find it in the \'Your listings\' section of the app.");
                        }
                        homeIntent.putExtra("SB_LONG",true);
                        startActivity(homeIntent);
                    }
                    else{
                        Log.d(TAG, "onComplete: failed with",task.getException());
                        Toast.makeText(getApplicationContext(),"failed to add book!",Toast.LENGTH_SHORT).show();
                    }
                }
            });


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Create listing failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private void showSnackbar(String message) {
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
        showSaveDialog();

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
//            PlacesClient placesClient = Places.createClient(this);
        }
        catch (NullPointerException e){
            Log.e(TAG, "initFireBase: getCurrentUser error", e);
        }
    }

    private void populateUserLocation() {
        String address;
        address = userPref.getString(getString(R.string.p_area),"");
        if(address != null && !TextUtils.isEmpty(address))
            address = address + ", ";
        address  = address + userPref.getString(getString(R.string.p_city),"");
        book_lat = userPref.getFloat(getString(R.string.p_lat),0.0f);
        book_lng = userPref.getFloat(getString(R.string.p_lng),0.0f);
        locField.setText(address);
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
            case 0:// camera intent
                if (resultCode == RESULT_OK ) {
                    try {
                        File f = new File(imageFilePath);
                        selectedImageUri = FileProvider.getUriForFile(CreateGeneralListingActivity.this, BuildConfig.APPLICATION_ID + ".provider", f);
                        CropImage(selectedImageUri);
                    } catch (Exception e) {
                        showSnackbar("File error: please try again");
                    }

                }
                break;

            case 1:// gallery intent
                if (resultCode == RESULT_OK && imageReturnedIntent != null && imageReturnedIntent.getData() != null) {
                    selectedImageUri = imageReturnedIntent.getData();
                    CropImage(selectedImageUri);
                }
                break;
            //new crop image
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(imageReturnedIntent);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
//                        mBookImage.setImageBitmap(bitmap);
                        Glide.with(mBookImage.getContext())
                                .asBitmap()
                                .load(bitmap)
                                .into(mBookImage);
                        detailsChanged=true;
                        storeBookImage(bitmap);
                    } catch (IOException e) {
                        Log.e(TAG, "onActivityResult: CROP ERROR:",e);
                        Toast.makeText(this, "CROP ERROR:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(this, "CROP ERROR:"+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case AUTOCOMPLETE_REQUEST_CODE: // for places search
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(imageReturnedIntent);
                    //Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                    //Toast.makeText(CreateGeneralListingActivity.this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();

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
                    Toast.makeText(CreateGeneralListingActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
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
                Uri photoURI = FileProvider.getUriForFile(this, "com.booksyndy.academics.android.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(pictureIntent, 0);
            }
        }
    }

    private void storeBookImage(Bitmap bmp) {

        byte[] compressedImage = CompressResizeImage(bmp);

        //show progress
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading photo");
        progressDialog.show();

        try {
            String timeStamp =
                    new SimpleDateFormat("yyyyMMdd_HHmmss",
                            Locale.getDefault()).format(new Date());
            // Get a reference to store file at book_photos/<FILENAME>
            final StorageReference photoRef = bookPhotosStorageReference.child(timeStamp + "_" + selectedImageUri.getLastPathSegment());

            // Upload file to Firebase Storage
            UploadTask uploadTask = photoRef.putBytes(compressedImage);
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
                    progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    //displaying percentage in progress dialog
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    protected void CropImage(Uri picUri) {
        try {
            CropImage.activity(picUri)
                    .setInitialCropWindowPaddingRatio(0)
                    .setRequestedSize(1080,1080)
//                    .setMaxCropResultSize(1080,1080)
//                    .setMinCropResultSize(1000,1000)
                    .start(this);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Your device doesn't support the crop action!", Toast.LENGTH_SHORT).show();

        }
    }

    public byte[] CompressResizeImage(Bitmap bm)
    {
        int bmWidth = bm.getWidth();
        int bmHeight = bm.getHeight();
        int ivWidth = 1080;
        int ivHeight = 1080;

        Bitmap newbitMap;
        if (bmWidth>ivWidth || bmHeight>ivHeight) {
            int new_height = (int) Math.floor((double) bmHeight * ((double) ivWidth / (double) bmWidth));
            newbitMap = Bitmap.createScaledBitmap(bm, ivWidth, new_height, true);
        }
        else {
            int new_height = (int) Math.floor((double) bmHeight);
            newbitMap = Bitmap.createScaledBitmap(bm, bmWidth, new_height, true);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        newbitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return b;
    }

    public void checkAndUploadBook() {
        bookName = nameField.getText().toString().trim();
        bookDescription = descField.getText().toString().trim();
        bookAddress = locField.getText().toString().trim();
        isTextbook = typeSpinner.getSelectedItemPosition()==0;
        String bps = priceField.getText().toString().trim();
        String bys;

        if (selectedImageUri==null) {
            showSnackbar("Please take or select a picture of your book");
        }
        else if (progress<99.9) {
            showSnackbar("Please wait while we upload the picture...");
        }
        else if (bookName.length()<10) {
            showSnackbar("Please enter at least 10 characters for your book's name");
        }
        else if (bookDescription.length()<10) {
            showSnackbar("Please enter at least 10 characters for the description");
        }
        else if (bookAddress.length()==0) {
            showSnackbar("Couldn't get your location. Please try searching.");
        }
        else if (bps.length()==0) {
            showSnackbar("Please enter a price or give it for free");
        }
/*                else if (book_photo_url==null || book_photo_url.length()==0) {
                    showSnackbar("Please take a picture of your book");
                }*/
        else {
            bookPrice = Integer.parseInt(bps);
            uploadBook();

        }
    }

    public void showSaveDialog() {

        if (detailsChanged) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateGeneralListingActivity.this);
            builder.setTitle("Save your changes?");
            builder.setMessage("You have unsaved changes. Would you like to stay or discard them?");
            builder.setPositiveButton("Stay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
//                            Intent homeIntent = new Intent(UserProfileActivity.this, HomeActivity.class);
//                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            homeIntent.putExtra("SNACKBAR_MSG", "Your profile has been saved");
//                            startActivity(homeIntent);

                }
            });
            builder.setNegativeButton("Exit without saving", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent homeActivity = new Intent(CreateGeneralListingActivity.this, HomeActivity.class);
                    startActivity(homeActivity);
                    finish();
                }
            });
            builder.show();
        }
        else {
            Intent homeActivity = new Intent(CreateGeneralListingActivity.this, HomeActivity.class);
            startActivity(homeActivity);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showSaveDialog();
        }
        return true;
    }
}

