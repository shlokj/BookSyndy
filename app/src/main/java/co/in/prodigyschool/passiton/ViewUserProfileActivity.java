package co.in.prodigyschool.passiton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import co.in.prodigyschool.passiton.Adapters.BookAdapter;
import co.in.prodigyschool.passiton.Data.Book;
import de.hdodenhof.circleimageview.CircleImageView;

public class ViewUserProfileActivity extends AppCompatActivity implements BookAdapter.OnBookSelectedListener,BookAdapter.OnBookLongSelectedListener {

    public static String TAG = "VIEWUSERPROFILE";

    private CircleImageView user_photo;
    private TextView mUserName,mUserId;
    private String fullName,userID,imageUrl,phone;
    private List<Book> userBookList;
    private FirebaseFirestore mFireStore;
    private RecyclerView recyclerView;
    private BookAdapter mAdapter;
    private View mEmptyView;
    private Query mQuery;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);
        getSupportActionBar().setTitle("View profile");

        user_photo = findViewById(R.id.others_profile_image);
        mUserName = findViewById(R.id.view_fullname);
        mUserId = findViewById(R.id.usernameMsg);

        fullName  = getIntent().getStringExtra("USER_NAME");
        userID = getIntent().getStringExtra("USER_ID");
        imageUrl = getIntent().getStringExtra("USER_PHOTO");
        phone = getIntent().getStringExtra("USER_PHONE");
        userBookList = new ArrayList<>();
        recyclerView = findViewById(R.id.other_recyclerView);
        mEmptyView = findViewById(R.id.other_view_empty);
        initFireBase();
        initView();


        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    private void initFireBase() {
        mFireStore = FirebaseFirestore.getInstance();
        mQuery =  mFireStore.collection("books").whereEqualTo("userId",phone).whereEqualTo("bookSold",false);
        mAdapter = new BookAdapter(mQuery,this,this){

               @Override
               protected void onDataChanged() {
                   super.onDataChanged();

                   if (getItemCount() == 0) {
                       recyclerView.setVisibility(View.GONE);
                       mEmptyView.setVisibility(View.VISIBLE);
                   } else {
                       recyclerView.setVisibility(View.VISIBLE);
                       mEmptyView.setVisibility(View.GONE);
                   }
               }

               @Override
               protected void onError(FirebaseFirestoreException e) {

                   Log.e(TAG, "Error: check logs for info.");
               }

       };
    }


    private void initView() {
        Glide.with(user_photo.getContext())
                .load(imageUrl)
                .into(user_photo);
        mUserName.setText(fullName);
        mUserId.setText(userID);

    }

    @Override
    public void onBookSelected(DocumentSnapshot snapshot) {
        String book_id = snapshot.getId();
        Intent bookDetails = new Intent(ViewUserProfileActivity.this, BookDetailsActivity.class);
        bookDetails.putExtra("bookid", book_id);
        bookDetails.putExtra("isHome",false);
        startActivity(bookDetails);

    }

    @Override
    public void onBookLongSelected(DocumentSnapshot snapshot) {

    }
}
