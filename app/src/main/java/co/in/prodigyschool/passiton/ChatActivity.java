package co.

in.prodigyschool.passiton;

import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView visiter_profile_picture;
    private String visit_user_id,visit_user_name,visit_image;
    private TextView visiter_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow_white);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        visiter_profile_picture = findViewById(R.id.visit_profile_image);
        visiter_name = findViewById(R.id.visit_profile_name);

        initializeChatRoom();

    }

    private void initializeChatRoom() {

        visit_image = getIntent().getStringExtra("visit_image");
        visit_user_id = getIntent().getStringExtra("visit_user_id");
        visit_user_name = getIntent().getStringExtra("visit_user_name");
        Glide.with(getApplicationContext()).load(visit_image).into(visiter_profile_picture);
        visiter_name.setText(visit_user_name);

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}