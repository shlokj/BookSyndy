package co.in.prodigyschool.passiton;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GetBookPictureActivity extends AppCompatActivity {

    public static String TAG = "GET_BOOK_PICTURE";
    LinearLayout takePic, choosePic;
    ImageView takenPic, chosenPic;
    private StorageReference bookPhotosStorageReference;
    private FirebaseStorage mFirebaseStorage;
    private String book_photo_url = null;
    private String imageFilePath;
    private Uri selectedImage;
    private int gradeNumber,boardNumber;
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

        takenPic = findViewById(R.id.picChosenIV);
        chosenPic = findViewById(R.id.picTakenIV);

        takePic = findViewById(R.id.takePicLL);
        choosePic = findViewById(R.id.chooseFromGalleryLL);

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCameraIntent();
            }
        });

        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                if(selectedImage != null)
                    getBookType.putExtra("BOOK_IMAGE_URI", selectedImage.toString());

                getBookType.putExtra("GRADE_NUMBER",gradeNumber);
                getBookType.putExtra("BOARD_NUMBER",boardNumber);

                startActivity(getBookType);



            }
        });
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

                    Glide.with(this).load(imageFilePath).into(takenPic);
                    File f = new File(imageFilePath);
                    selectedImage = Uri.fromFile(f);
                    takenPic.setVisibility(View.VISIBLE);
                    //storeBookImage(selectedImage);
                }
                break;

            case 1:// gallery intent
                if (resultCode == RESULT_OK && imageReturnedIntent != null && imageReturnedIntent.getData() != null) {
                    chosenPic.setVisibility(View.VISIBLE);
                    selectedImage = imageReturnedIntent.getData();
                    performCrop(selectedImage);
                    //storeBookImage(selectedImage);
                    chosenPic.setImageURI(selectedImage);
                }
                break;


        }
    }
//need to move this function to last book listing activity
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
                MediaStore.ACTION_IMAGE_CAPTURE );
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

    private void performCrop(Uri picUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 500);
            cropIntent.putExtra("outputY", 500);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    @Override
    public void onBackPressed() {
        Intent homeActivity = new Intent(GetBookPictureActivity.this, HomeActivity.class);
        startActivity(homeActivity);
    }
}
