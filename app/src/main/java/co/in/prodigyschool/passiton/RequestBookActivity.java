package co.in.prodigyschool.passiton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.Data.BookRequest;

public class RequestBookActivity extends AppCompatActivity {
    private static String TAG = "REQUEST_BOOK";

    private FirebaseFirestore mFireStore;
    private String userPhone,userId,bookTitle,bookDesc,bookAddress;
    private int bookGrade,bookBoard;
    private boolean isText,isCompetitive;
    private double bookLat,bookLng;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_book);
        getSupportActionBar().setTitle("Request a book");
        initFirebase();

        findViewById(R.id.requestButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add validation and populate the fields here


                BookRequest bookRequest = new BookRequest(bookTitle,bookDesc,bookAddress,userPhone
                        ,userId,bookGrade,bookBoard,bookLat,bookLng,isCompetitive,false,isText);
                postBookRequest(bookRequest);
            }
        });

    }





    private void postBookRequest(BookRequest bookRequest) {
        mFireStore.collection("bookRequest").document(userPhone).set(bookRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Book Request Posted",Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Firebase Error: Try Again",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void initFirebase() {
        mFireStore = FirebaseFirestore.getInstance();
        userPhone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();


    }
}
