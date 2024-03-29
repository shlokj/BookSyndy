package com.booksyndy.academics.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class GetBookDescriptionActivity extends AppCompatActivity {

    boolean isTextbook;
    String bookName, bookDescription,selectedImage;
    EditText bookDescField;
    private int gradeNumber,boardNumber, year;

    //TODO: Save the description in the edittext in case the user goes back (don't make him type it again)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_book_description);
        getSupportActionBar().setTitle("List a book");

        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER",4);
        boardNumber = getIntent().getIntExtra("BOARD_NUMBER", 6);
        year = getIntent().getIntExtra("YEAR_NUMBER",0);

        selectedImage = getIntent().getStringExtra("BOOK_IMAGE_URI");
        bookName = getIntent().getStringExtra("BOOK_NAME");
        isTextbook = getIntent().getBooleanExtra("IS_TEXTBOOK",true);
        bookDescField = (EditText) findViewById(R.id.bookDescription);
        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab13);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookDescription = bookDescField.getText().toString().trim();
                if (bookDescription.length() < 10) {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Please enter at least 10 characters", Snackbar.LENGTH_SHORT)
                            .setAction("OKAY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                }
                else {
                    Intent getBookClass = new Intent(GetBookDescriptionActivity.this, GetBookClassActivity.class);
                    getBookClass.putExtra("BOOK_IMAGE_URI", selectedImage);
                    getBookClass.putExtra("IS_TEXTBOOK", isTextbook);
                    getBookClass.putExtra("GRADE_NUMBER",gradeNumber);
                    getBookClass.putExtra("BOARD_NUMBER",boardNumber);
                    getBookClass.putExtra("BOOK_NAME",bookName);
                    getBookClass.putExtra("BOOK_DESCRIPTION",bookDescription);
                    getBookClass.putExtra("YEAR_NUMBER", year);
                    startActivity(getBookClass);
                }
            }
        });

    }
}
