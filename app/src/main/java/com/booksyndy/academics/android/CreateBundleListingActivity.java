package com.booksyndy.academics.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.booksyndy.academics.android.Data.Book;
import com.booksyndy.academics.android.Data.Donation;
import com.booksyndy.academics.android.util.BookUtil;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class CreateBundleListingActivity extends AppCompatActivity {

    // for donations

    private static String TAG = "CREATELISTINGDONATEBUNDLE";


    private EditText nameField, descField, weightField, locField;
    private ImageView mBookImage;
    private Button postButton;
    private String imageFilePath;
    private String curUserId, donationName, donationDescription, phoneNumber, userId, bookAddress,donation_photo_url;
    private Uri selectedImageUri;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference bookPhotosStorageReference;
    private SharedPreferences userPref;
    private TextWatcher checkChange;
    private FirebaseFirestore mFireStore;
    private FirebaseAuth mAuth;

    private double progress;
    private boolean detailsChanged=false;
    private int approxWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bundle_listing);

        getSupportActionBar().setTitle("Donate a bundle");

        nameField = findViewById(R.id.bookNameField_d);
        descField = findViewById(R.id.bookDescField2_d);
        weightField = findViewById(R.id.weightField_d);
//        locField = findViewById(R.id.locField2_d);

        mBookImage = findViewById(R.id.book_image_d);

        postButton = findViewById(R.id.postButton_d);


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
        weightField.addTextChangedListener(checkChange);
//        locField.addTextChangedListener(checkChange);

        initFireBase();


        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndUploadDonation();
            }
        });

        mBookImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(CreateBundleListingActivity.this);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

//        autofillBoardTemp();

        switch (requestCode) {
            case 0:// camera intent
                if (resultCode == RESULT_OK ) {
                    try {
                        File f = new File(imageFilePath);
                        selectedImageUri = FileProvider.getUriForFile(CreateBundleListingActivity.this, BuildConfig.APPLICATION_ID + ".provider", f);
                        cropImage(selectedImageUri);
                    } catch (Exception e) {
                        showSnackbar("File error: please try again");
                    }

                }
                break;

            case 1:// gallery intent
                if (resultCode == RESULT_OK && imageReturnedIntent != null && imageReturnedIntent.getData() != null) {
                    selectedImageUri = imageReturnedIntent.getData();
                    cropImage(selectedImageUri);
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
        }
    }


    public byte[] compressResizeImage(Bitmap bm)
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



    private void storeBookImage(Bitmap bmp) {

        byte[] compressedImage = compressResizeImage(bmp);

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
                        donation_photo_url = task.getResult().toString();
//                        Toast.makeText(CreateBundleListingActivity.this, donation_photo_url, Toast.LENGTH_LONG).show();
//                        Toast.makeText(getApplicationContext(), "upload success", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Log.d(TAG, "onComplete: success url: " + donation_photo_url);
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


    protected void cropImage(Uri picUri) {
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



    private void uploadBook() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Posting...");
        progressDialog.setMessage("Submitting your donation request");
        progressDialog.setCancelable(false);
        progressDialog.show();
        try {

            if(userId == null){
                userId = mAuth.getCurrentUser().getPhoneNumber();
            }
/*            Book book = BookUtil.addBook(userId,isTextbook,donationName,donationDescription,gradeNumber,boardNumber,bookPrice,bookAddress,book_lat,book_lng);
            book.setBookYear(year);
            book.setGeneral(false);*/

            String donListTime = new SimpleDateFormat("yyyy MM dd HH:mm:ss",Locale.getDefault()).format(new Date());

            long ca = new Date().getTime();

            final Donation donation = new Donation(userId, donationName, donationDescription, "", donListTime, 0.0, 0.0, 0, approxWeight, ca);
            donation.setDocumentId(userId + "_" + ca);
            if(donation_photo_url != null && !donation_photo_url.isEmpty()){
                // book.setBookPhoto();
                donation.setDonationPhoto(donation_photo_url);
            }

            CollectionReference donations = mFireStore.collection("donations");
            donations.add(donation).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        Log.d("Add donation","onComplete: Donation added successfully");
                        progressDialog.dismiss();
                        //TODO: get address intent here
                        Intent getDonorAddress = new Intent(CreateBundleListingActivity.this, GetDonorAddressActivity.class);
                        getDonorAddress.putExtra("DON_DOC_ID",donation.getDocumentId());
//                        Toast.makeText(CreateBundleListingActivity.this, "doc id: " + donation.getDocumentId(), Toast.LENGTH_SHORT).show();
                        getDonorAddress.putExtra("PIC_URL",donation_photo_url);
                        getDonorAddress.putExtra("DON_DOC_NAME",task.getResult().getId());
                        startActivity(getDonorAddress);
//                        task.getResult().getId();

//                        Toast.makeText(CreateBundleListingActivity.this, "id: "+task.getResult().getId(), Toast.LENGTH_SHORT).show();
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


    public void showSaveDialog() {

        if (detailsChanged) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateBundleListingActivity.this);
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
                    Intent homeActivity = new Intent(CreateBundleListingActivity.this, HomeActivity.class);
                    startActivity(homeActivity);
                    finish();
                }
            });
            builder.show();
        }
        else {
            Intent homeActivity = new Intent(CreateBundleListingActivity.this, HomeActivity.class);
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

    private File createImageFile() throws IOException {

        String imageFileName = "DONATION" + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();

//        Toast.makeText(this, "image file path: " + imageFilePath + "image file name: " + imageFileName, Toast.LENGTH_SHORT).show();

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

    public void checkAndUploadDonation() {
        donationName = nameField.getText().toString().trim();
        donationDescription = descField.getText().toString().trim();
        if (weightField.getText().toString().trim().length() == 0) {
            approxWeight = 0;
        }
        else {
            approxWeight = Integer.parseInt(weightField.getText().toString().trim());
        }

        if (selectedImageUri==null) {
            showSnackbar("Please take or select a picture of your book");
        }
        else if (progress<99.9) {
            showSnackbar("Please wait while we upload the picture...");
        }
        else if (donationName.length()<10) {
            showSnackbar("Please enter at least 10 characters for your book's name");
        }
        else if (donationDescription.length()<10) {
            showSnackbar("Please enter at least 10 characters for the description");
        }
        else {
            uploadBook();
        }
    }

    public static String generateRandomString(int min, int max, int size) {
        String result = "";
        for (int i = 0; i < size; i++) {
            result += String.valueOf((char)(new Random().nextInt((max - min) + 1) + min));
        }
        return result;
    }
}


