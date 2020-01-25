package co.in.prodigyschool.passiton;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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

public class GetBookPictureActivity extends AppCompatActivity {

    public static String TAG = "GET_BOOK_PICTURE";
    private LinearLayout takePic, choosePic;
    private ImageView takenPic, chosenPic;
    private TextView rtpi, rcpi;
    private StorageReference bookPhotosStorageReference;
    private FirebaseStorage mFirebaseStorage;
    private String book_photo_url = null;
    private String imageFilePath;
    private Uri selectedImageUri;
    private int gradeNumber,boardNumber, year;
    private boolean fromCamera;
    final int PIC_CROP = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_book_picture);
        getSupportActionBar().setTitle("List a book");
        mFirebaseStorage = FirebaseStorage.getInstance();
        bookPhotosStorageReference = mFirebaseStorage.getReference().child("book_photos");

        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER",4);
        boardNumber = getIntent().getIntExtra("BOARD_NUMBER", 6);
        year = getIntent().getIntExtra("YEAR_NUMBER",0);

        takenPic = findViewById(R.id.picTakenIV);
        chosenPic = findViewById(R.id.picChosenIV);
        rtpi = findViewById(R.id.retakePicInstructions);
        rcpi = findViewById(R.id.rechoosePicInstructions);

        takePic = findViewById(R.id.takePicLL);
        choosePic = findViewById(R.id.chooseFromGalleryLL);

        if(getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromCamera = true;
                openCameraIntent();
            }
        });

        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromCamera = false;
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
            }
        });

        FloatingActionButton next = findViewById(R.id.fab19);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getBookType = new Intent(GetBookPictureActivity.this, GetBookMaterialTypeActivity.class);
                // To find an efficient way to pass on the image of the book; mostly the uri
                if(selectedImageUri != null) {
                    getBookType.putExtra("BOOK_IMAGE_URI", selectedImageUri.toString());
                    getBookType.putExtra("GRADE_NUMBER", gradeNumber);
                    getBookType.putExtra("BOARD_NUMBER", boardNumber);
                    getBookType.putExtra("YEAR_NUMBER", year);
                    startActivity(getBookType);
                }
                else {
                    showSnackbar("Please take or select a picture of your book");
                }
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:// camera intent
                if (resultCode == RESULT_OK ) {
                    File f = new File(imageFilePath);
                    selectedImageUri = FileProvider.getUriForFile(GetBookPictureActivity.this, BuildConfig.APPLICATION_ID + ".provider",f);
                    CropImage(selectedImageUri);

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
//                        takenPic.setImageBitmap(bitmap);
                        if (fromCamera) {
                            chosenPic.setImageResource(android.R.color.transparent);
                            takePic.setVisibility(View.INVISIBLE);
                            choosePic.setVisibility(View.VISIBLE);
                            rtpi.setVisibility(View.VISIBLE);
                            rcpi.setVisibility(View.INVISIBLE);
                            Glide.with(takenPic.getContext())
                                    .load(resultUri)
                                    .into(takenPic);
                        }
                        else {
                            takenPic.setImageResource(android.R.color.transparent);
                            takePic.setVisibility(View.VISIBLE);
                            choosePic.setVisibility(View.INVISIBLE);
                            rtpi.setVisibility(View.INVISIBLE);
                            rcpi.setVisibility(View.VISIBLE);
                            Glide.with(chosenPic.getContext())
                                    .load(resultUri)
                                    .into(chosenPic);
                        }
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
                startActivityForResult(pictureIntent, 0);
            }
        }
    }

    private void storeBookImage(Bitmap bmp) {

        byte[] compressedImage = CompressResizeImage(bmp);

        //show progress
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        //progressDialog.show();

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

    protected void CropImage(Uri picUri) {
        try {
            CropImage.activity(picUri)
//                    .setAspectRatio(1,1)
                    .setRequestedSize(500,500)
                    .start(this);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Your device doesn't support the crop action!", Toast.LENGTH_SHORT).show();

        }
    }

    public byte[] CompressResizeImage(Bitmap bm)
    {
        int bmWidth = bm.getWidth();
        int bmHeight = bm.getHeight();
        int ivWidth = takenPic.getWidth();
        int ivHeight = takenPic.getHeight();

        int new_height = (int) Math.floor((double) bmHeight *( (double) ivWidth / (double) bmWidth));
        Bitmap newbitMap = Bitmap.createScaledBitmap(bm, ivWidth, new_height, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        newbitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return b;
    }


    @Override
    public void onBackPressed() {
        Intent homeActivity = new Intent(GetBookPictureActivity.this, HomeActivity.class);
        startActivity(homeActivity);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
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

}
