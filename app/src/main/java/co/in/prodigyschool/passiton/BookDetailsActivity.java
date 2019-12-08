package co.in.prodigyschool.passiton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.Data.User;


public class BookDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private String bookid;
    FirebaseFirestore mFirestore;
    Book currentBook;
    User bookOwner;
    private FloatingActionButton fab_favourite;
    private TextView view_bookname,view_address,view_price,view_category,view_description;
    private ImageView view_bookimage;

    private static final String TAG = "BOOK DETAILS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        bookid = getIntent().getStringExtra("bookid");
        initFireStore();
        view_bookname = findViewById(R.id.book_name);
        view_address = findViewById(R.id.book_address);
        view_category = findViewById(R.id.book_category);
        view_price = findViewById(R.id.book_price);
        view_bookimage = findViewById(R.id.book_image);
        view_description = findViewById(R.id.bookDescriptionTV);
        findViewById(R.id.book_button_back).setOnClickListener(this);
        findViewById(R.id.fab_chat).setOnClickListener(this);
        findViewById(R.id.fab_favorite).setOnClickListener(this);

    }

    private void initFireStore() {
        mFirestore = FirebaseFirestore.getInstance();

        if(mFirestore != null){
            populateBookDetails();

        }
        else{
            Toast.makeText(getApplicationContext(),"Firebase error",Toast.LENGTH_SHORT).show();
        }
    }



    private void populateBookDetails() {
        //
        try{
            DocumentReference bookReference =  mFirestore.collection("books").document(bookid);
            bookReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    currentBook = snapshot.toObject(Book.class);
                    view_bookname.setText(currentBook.getBookName());
                    view_description.setText(currentBook.getBookDescription());
                    view_address.setText(currentBook.getBookAddress());
                    view_price.setText("₹"+currentBook.getBookPrice());
                    if(currentBook.isTextbook()){
                        view_category.setText("Text Book");
                    }
                    else{
                        view_category.setText("Notes");
                    }
                    Glide.with(view_bookimage.getContext())
                            .load(currentBook.getBookPhoto())
                            .into(view_bookimage);

                    populateUserDetails();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: ",e );
                }
            });

        }
        catch(Exception e){
            Log.e(TAG, "populateBookDetails failed with  ",e);
        }
    }

    /*
    method to get user details;
     */
    private void populateUserDetails() {
        if (!checkConnection(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),"Internet Required",Toast.LENGTH_LONG).show();
            return;
        }
        DocumentReference reference = mFirestore.collection("users").document(currentBook.getUserId());
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
           if(snapshot.exists()){
               bookOwner = snapshot.toObject(User.class);
           }
           else{
               Log.d(TAG, "onSuccess: no user found for the book");
           }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: populateuserdetails" ,e );
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.book_button_back:
                onBackArrowClicked(v);
                break;
            case R.id.fab_chat:
                startChatActivity();
                break;
            case R.id.fab_favorite:
                //break;
        }
    }

    private void startChatActivity() {
        Intent chatIntent = new Intent(BookDetailsActivity.this, ChatActivity.class);
        chatIntent.putExtra("visit_user_id", currentBook.getUserId()); // phone number
        chatIntent.putExtra("visit_user_name", bookOwner.getUserId()); // unique user id
        chatIntent.putExtra("visit_image", bookOwner.getImageUrl());
        startActivity(chatIntent);
    }


    public void onBackArrowClicked(View view) {
        onBackPressed();
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

}
