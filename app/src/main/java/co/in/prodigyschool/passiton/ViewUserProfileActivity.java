package co.in.prodigyschool.passiton;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
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
    private Menu menu;
    private LinearLayout sendMessageLL;

    // TODO: book request layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("View profile");
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        user_photo = findViewById(R.id.others_profile_image);
        mUserName = findViewById(R.id.view_fullname);
        mUserId = findViewById(R.id.usernameMsg);
        sendMessageLL = findViewById(R.id.sendMessageLL);
        fullName  = getIntent().getStringExtra("USER_NAME");
        userID = getIntent().getStringExtra("USER_ID");
        imageUrl = getIntent().getStringExtra("USER_PHOTO");
        phone = getIntent().getStringExtra("USER_PHONE");
        userBookList = new ArrayList<>();
        recyclerView = findViewById(R.id.other_recyclerView);
        mEmptyView = findViewById(R.id.other_view_empty);
        initFireBase();
        initView();

        sendMessageLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChatActivity();
            }
        });

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
        bookDetails.putExtra("isProfile",true);
        startActivity(bookDetails);

    }

    @Override
    public void onBookLongSelected(DocumentSnapshot snapshot) {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reportUser:
                AlertDialog.Builder rBuilder = new AlertDialog.Builder(ViewUserProfileActivity.this);
                rBuilder.setTitle("Report listing");
                rBuilder.setIcon(R.drawable.ic_report_24px_outlined);
                rBuilder.setMessage(Html.fromHtml("Are you sure you want to report <b>"+userID+"</b>?"));
                rBuilder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // code to report
                        Toast.makeText(getApplicationContext(),"Reported",Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
                rBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                rBuilder.show();
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_user_profile, menu);
        this.menu = menu;

        return true;
    }

    private void startChatActivity() {
        try {
            Intent chatIntent = new Intent(ViewUserProfileActivity.this, ChatActivity.class);
            chatIntent.putExtra("visit_user_id", phone); // phone number
            chatIntent.putExtra("visit_user_name",userID); // unique user id
            chatIntent.putExtra("visit_image", imageUrl);
            startActivity(chatIntent);
        }
        catch(Exception e){
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, "Please try again", Snackbar.LENGTH_SHORT)
                    .setAction("OKAY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                    .show();        }
    }
}
