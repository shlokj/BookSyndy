package co.in.prodigyschool.passiton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.util.BookUtil;


public class ConfirmListingActivity extends AppCompatActivity {

    public static final String TAG = "CONFIRM LISTING";

    boolean isTextbook;
    String bookName, bookDescription, phoneNumber, userId, bookAddress, bookImageUrl, selectedImage,book_photo_url;
    int gradeNumber, boardNumber;
    private int bookPrice;
    private TextView bookNameTV, bookDescriptionTV, bookTypeTV, bookCategoryTV, bookPriceTV, bookLocTV;
    private Button confirmAndPost;
    private double book_lat,book_lng;
    private StorageReference bookPhotosStorageReference;
    private FirebaseStorage mFirebaseStorage;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFireStore;
    ImageView bookPicFinal;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_listing);

        getSupportActionBar().setTitle("Confirm your listing");
/*
        ScrollView detailsSV = findViewById(R.id.scrollView2);
        ViewGroup.LayoutParams params = detailsSV.getLayoutParams();
        params.height = params.height-112;
        params.width = params.width;
        detailsSV.setLayoutParams(params);*/
        mAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        bookPhotosStorageReference = mFirebaseStorage.getReference().child("book_photos");
        selectedImage = getIntent().getStringExtra("BOOK_IMAGE_URI");
        isTextbook = getIntent().getBooleanExtra("IS_TEXTBOOK", true);
        bookName = getIntent().getStringExtra("BOOK_NAME");
        bookDescription = getIntent().getStringExtra("BOOK_DESCRIPTION");
        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER", 4);
        boardNumber = getIntent().getIntExtra("BOARD_NUMBER", 1);
        bookPrice = getIntent().getIntExtra("BOOK_PRICE", 0);
        bookImageUrl = getIntent().getStringExtra("BOOK_IMAGE_URL");
        bookAddress = getIntent().getStringExtra("BOOK_LOCATION");
        if(getIntent().getStringExtra("LATITUDE") != null && getIntent().getStringExtra("LONGITUDE") != null) {
            book_lat = Double.parseDouble(getIntent().getStringExtra("LATITUDE"));
            book_lng = Double.parseDouble(getIntent().getStringExtra("LONGITUDE"));
        }
        else{
            book_lng = 0.0;
            book_lat = 0.0;
        }

        bookPicFinal = findViewById(R.id.bookPicFinal);
        bookNameTV = findViewById(R.id.bookTitleFinal);
        bookDescriptionTV = findViewById(R.id.bookDescriptionFinal);
        bookTypeTV = findViewById(R.id.bookTypeFinal);
        bookCategoryTV = findViewById(R.id.bookCategoryFinal);
        bookPriceTV = findViewById(R.id.bookPriceFinal);
        bookLocTV = findViewById(R.id.bookLocFinal);
        confirmAndPost = findViewById(R.id.confirmAndPost);
        confirmAndPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImage == null)
                    storeBookImage(null);
                else
                    storeBookImage(Uri.parse(selectedImage));
            }
        });

        if(selectedImage != null && !selectedImage.isEmpty()){
            bookPicFinal.setImageURI(Uri.parse(selectedImage));
        }

        bookNameTV.setText(bookName);
        bookDescriptionTV.setText(bookDescription);
        if (isTextbook) {
            bookTypeTV.setText("Textbook");
        } else {
            bookTypeTV.setText("Notes / other material");
        }
        if (boardNumber == 20) {
            bookCategoryTV.setText("Competitive exams");
        } else {
            if (gradeNumber == 1) {
                bookCategoryTV.setText("Grade 5 or below");
            } else if (gradeNumber == 2) {
                bookCategoryTV.setText("Grade 6 to 8");
            } else if (gradeNumber == 3) {
                bookCategoryTV.setText("Grade 9");
            } else if (gradeNumber == 4) {
                bookCategoryTV.setText("Grade 10");
            } else if (gradeNumber == 5) {
                bookCategoryTV.setText("Grade 11");
            } else if (gradeNumber == 6) {
                bookCategoryTV.setText("Grade 12");
            } else if (gradeNumber == 7) {
                bookCategoryTV.setText("Undergraduate");
//                Toast.makeText(getApplicationContext(),"Grade number: 7\nBoard number: "+boardNumber,Toast.LENGTH_SHORT).show();

                if (boardNumber == 7) {
                    bookCategoryTV.append(", B. Tech");
                } else if (boardNumber == 8) {
                    bookCategoryTV.append(", B. Sc");
                } else if (boardNumber == 9) {
                    bookCategoryTV.append(", B. Com");
                } else if (boardNumber == 10) {
                    bookCategoryTV.append(", BA");
                } else if (boardNumber == 11) {
                    bookCategoryTV.append(", BBA");
                } else if (boardNumber == 12) {
                    bookCategoryTV.append(", BCA");
                } else if (boardNumber == 13) {
                    bookCategoryTV.append(", B. Ed");
                } else if (boardNumber == 14) {
                    bookCategoryTV.append(", LLB");
                } else if (boardNumber == 15) {
                    bookCategoryTV.append(", MBBS");
                } else if (boardNumber == 16) {
                    bookCategoryTV.append(", other degree");
                }

                if (boardNumber == 1) {
                    bookCategoryTV.append(", CBSE");
                } else if (boardNumber == 2) {
                    bookCategoryTV.append(", ICSE//ISC");
                } else if (boardNumber == 3) {
                    bookCategoryTV.append(", IB");
                } else if (boardNumber == 4) {
                    bookCategoryTV.append(", IGCSE");
                } else if (boardNumber == 5) {
                    bookCategoryTV.append(", state board");
                } else if (boardNumber == 6) {
                    bookCategoryTV.append(", other board");
                }
                //TODO: append degree
            }
        }
        if (bookPrice != 0) {
            bookPriceTV.setText("₹" + bookPrice);
        } else {
            bookPriceTV.setText("Free");
        }
        bookLocTV.setText(bookAddress);

    }

    private void storeBookImage(Uri selectedImageUri) {
        if(selectedImageUri == null){
            uploadBook();
            return;
        }
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
                        Toast.makeText(getApplicationContext(), "upload success", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Log.d(TAG, "onComplete: success url: " + book_photo_url);
                        uploadBook();
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

    private void uploadBook() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Posting");
        progressDialog.setTitle("Creating your listing...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        try {

            if(userId == null){
                userId = mAuth.getCurrentUser().getPhoneNumber();
            }
            Book book = BookUtil.addBook(userId,isTextbook,bookName,bookDescription,gradeNumber,boardNumber,bookPrice,bookAddress,book_lat,book_lng);
            if(selectedImage != null && !selectedImage.isEmpty()){
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
                        Intent homeIntent = new Intent(ConfirmListingActivity.this,HomeActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
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
}
