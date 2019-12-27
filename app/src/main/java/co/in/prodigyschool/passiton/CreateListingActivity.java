package co.in.prodigyschool.passiton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

public class CreateListingActivity extends AppCompatActivity {

    private static String TAG = "CREATELISTINGFULL";
    private Spinner typeSpinner,gradeSpinner,boardSpinner;

    boolean isTextbook;
    String bookName, bookDescription, phoneNumber, userId, bookAddress, bookImageUrl, selectedImage,book_photo_url;
    int gradeNumber, boardNumber;
    private int bookPrice;
    private FirebaseFirestore mFirestore;
    private CheckBox compExams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);
        typeSpinner = findViewById(R.id.bookTypeSpinner);
        gradeSpinner = findViewById(R.id.gradeSpinner);
        boardSpinner = findViewById(R.id.boardSpinner);

        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER",4);
        boardNumber = getIntent().getIntExtra("BOARD_NUMBER", 6);


    }

}
