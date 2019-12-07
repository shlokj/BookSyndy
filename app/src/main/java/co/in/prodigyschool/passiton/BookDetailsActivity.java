package co.in.prodigyschool.passiton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import co.in.prodigyschool.passiton.Data.Book;

public class BookDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private String bookid;
    FirebaseFirestore mFirestore;
    Query mQuery;
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
                    Book book = snapshot.toObject(Book.class);

                    view_bookname.setText(book.getBookName());
                    view_description.setText(book.getBookDescription());
                    view_address.setText(book.getBookAddress());
                    view_price.setText("₹"+book.getBookPrice());
                    if(book.isTextbook()){
                        view_category.setText("Text Book");
                    }
                    else{
                        view_category.setText("Notes");
                    }
                    Glide.with(view_bookimage.getContext())
                            .load(book.getBookPhoto())
                            .into(view_bookimage);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.book_button_back:
                onBackArrowClicked(v);
                break;
            case R.id.fab_chat:
                break;
            case R.id.fab_favorite:
                //break;
        }
    }

    public void onBackArrowClicked(View view) {
        onBackPressed();
    }
}
