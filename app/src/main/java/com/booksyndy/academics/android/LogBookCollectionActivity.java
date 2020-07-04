package com.booksyndy.academics.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class LogBookCollectionActivity extends AppCompatActivity {

    // after a volunteer collects books, they use this to provide proof and confirmation that the books have been collected

    private ImageView donImage;
    private EditText descField;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_book_collection);

        donImage = findViewById(R.id.book_image_l);
        descField = findViewById(R.id.bookDescField2_l);
        confirmButton = findViewById(R.id.confirmCollectButton);


    }
}
