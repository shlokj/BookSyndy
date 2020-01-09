package co.in.prodigyschool.passiton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.ui.home.HomeFragment;
import co.in.prodigyschool.passiton.ui.home.HomeViewModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class ViewUserProfileActivity extends AppCompatActivity {

    public static String TAG = "VIEWUSERPROFILE";

    private CircleImageView user_photo;
    private TextView mUserName,mUserId;
    private String fullName,userID,imageUrl,phone;
    private List<Book> userBookList;
    private FirebaseFirestore mFireStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);
        getSupportActionBar().setTitle("View profile");

        user_photo = findViewById(R.id.others_profile_image);
        mUserName = findViewById(R.id.view_fullname);
        mUserId = findViewById(R.id.usernameMsg);
        userBookList = new ArrayList<>();

        fullName  = getIntent().getStringExtra("USER_NAME");
        userID = getIntent().getStringExtra("USER_ID");
        imageUrl = getIntent().getStringExtra("USER_PHOTO");
        phone = getIntent().getStringExtra("USER_PHONE");


        initFireBase();
        initView();

    }

    private void initFireBase() {
        mFireStore = FirebaseFirestore.getInstance();
        mFireStore.collection("books").whereEqualTo("userId",phone).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty())
                userBookList.addAll(queryDocumentSnapshots.toObjects(Book.class));
                populateBooks();
                Log.d(TAG, "onSuccess: "+userBookList.size());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: fetch books",e );
            }
        });
    }

    private void populateBooks() {

    }

    private void initView() {
        Glide.with(user_photo.getContext())
                .load(imageUrl)
                .into(user_photo);
        mUserName.setText(fullName);
        mUserId.setText(userID);

    }
}
