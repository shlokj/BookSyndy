package com.booksyndy.academics.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
import java.util.HashMap;
import java.util.Locale;

import io.opencensus.internal.StringUtils;

public class LogBookCollectionActivity extends AppCompatActivity {

    // after a volunteer collects books, they use this to provide proof and confirmation that the books have been collected

    private ImageView donImage;
    private EditText descField;
    private Button confirmButton;

    private static String TAG = "LOG_BOOK_COLLECTION_ACTIVITY";

    private Uri selectedImageUri;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference bookPhotosStorageReference;

    private DocumentReference donationRef;
    private CollectionReference collectionLogRef;
    private String don_id, don_desc, donation_photo_url, curUserName, curUserPhone, imageFilePath;
    private double progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_book_collection);


        donImage = findViewById(R.id.book_image_l);
        descField = findViewById(R.id.bookDescField2_l);
        confirmButton = findViewById(R.id.confirmCollectButton);

        don_id = getIntent().getStringExtra("DON_DOC_NAME");
        curUserName = getIntent().getStringExtra("CUR_USER_NAME");
        curUserPhone = getIntent().getStringExtra("CUR_USER_PHONE");
        initFireStore();


        donImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(LogBookCollectionActivity.this);
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


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               TODO: add validations here

                don_desc = descField.getText().toString().trim();
                if(TextUtils.isEmpty(donation_photo_url)){
                    showSnackbar("Please add photo of books received by you");
                    return;
                }
                if (TextUtils.isEmpty(don_desc)) {
                    descField.setError("Please provide some description");
                    return;
                }
                logBookCollection();
            }
        });
    }


    private void initFireStore() {


        try {
            FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
            donationRef = mFirestore.collection("donations").document(don_id);
            collectionLogRef = mFirestore.collection("collectionLog");
            mFirebaseStorage = FirebaseStorage.getInstance();
            bookPhotosStorageReference = mFirebaseStorage.getReference().child("collection_log");

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_SHORT).show();
        }
    }

    private void logBookCollection() {
        final ProgressDialog sProgressDialog = new ProgressDialog(LogBookCollectionActivity.this);
        sProgressDialog.setMessage("Just a moment...");
        sProgressDialog.setTitle("Processing");
        sProgressDialog.setCancelable(false);
        sProgressDialog.show();
        try {

            HashMap<String, Object> o = new HashMap<>();
            o.put("createdAt", new Date().getTime());
            o.put("donPhoto", donation_photo_url);
            o.put("donDesc", don_desc);
            o.put("donId", don_id);
            o.put("volUserName", curUserName);
            o.put("volPhone", curUserPhone);

            collectionLogRef.document(don_id).set(o).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    donationRef.update("acceptedByName", curUserName, "acceptedByPhone", curUserPhone, "status", 3)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if (sProgressDialog.isShowing())
                                        sProgressDialog.dismiss();
                                    startActivity(new Intent(LogBookCollectionActivity.this, VolunteerDashboardActivity.class));
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (sProgressDialog.isShowing())
                                        sProgressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Failed to submit request. Please try again after some time.", Toast.LENGTH_SHORT).show();

                                }
                            });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (sProgressDialog.isShowing())
                                sProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed to submit request. Please try again after some time.", Toast.LENGTH_SHORT).show();

                        }
                    });


        } catch (Exception e) {
            if (sProgressDialog.isShowing())
                sProgressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Failed to submit request. Please try again after some time.", Toast.LENGTH_SHORT).show();

        }
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


    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

//        autofillBoardTemp();

        switch (requestCode) {
            case 0:// camera intent
                if (resultCode == RESULT_OK) {
                    try {
                        File f = new File(imageFilePath);
                        selectedImageUri = FileProvider.getUriForFile(LogBookCollectionActivity.this, BuildConfig.APPLICATION_ID + ".provider", f);
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
                        Glide.with(donImage.getContext())
                                .asBitmap()
                                .load(bitmap)
                                .into(donImage);
                        storeBookImage(bitmap);
                    } catch (IOException e) {
                        Log.e(TAG, "onActivityResult: CROP ERROR:", e);
                        Toast.makeText(this, "CROP ERROR:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(this, "CROP ERROR:" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    protected void cropImage(Uri picUri) {
        try {
            CropImage.activity(picUri)
                    .setInitialCropWindowPaddingRatio(0)
                    .setRequestedSize(1080, 1080)
//                    .setMaxCropResultSize(1080,1080)
//                    .setMinCropResultSize(1000,1000)
                    .start(this);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Your device doesn't support the crop action!", Toast.LENGTH_SHORT).show();

        }
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
            final StorageReference photoRef = bookPhotosStorageReference.child(don_id + "/" + timeStamp + "/" + selectedImageUri.getLastPathSegment());

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

    public byte[] compressResizeImage(Bitmap bm) {
        int bmWidth = bm.getWidth();
        int bmHeight = bm.getHeight();
        int ivWidth = 1080;
        int ivHeight = 1080;

        Bitmap newbitMap;
        if (bmWidth > ivWidth || bmHeight > ivHeight) {
            int new_height = (int) Math.floor((double) bmHeight * ((double) ivWidth / (double) bmWidth));
            newbitMap = Bitmap.createScaledBitmap(bm, ivWidth, new_height, true);
        } else {
            int new_height = (int) Math.floor((double) bmHeight);
            newbitMap = Bitmap.createScaledBitmap(bm, bmWidth, new_height, true);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        newbitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return b;
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


}
