package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class GetBookDescriptionActivity extends AppCompatActivity {

    boolean isTextbook;
    String bookName, bookDescription;
    EditText bookDescField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_book_description);
        getSupportActionBar().setTitle("List a book");
        bookName = getIntent().getStringExtra("BOOK_NAME");
        isTextbook = getIntent().getBooleanExtra("IS_TEXTBOOK",true);
        bookDescField = (EditText) findViewById(R.id.bookDescription);
        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab13);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookDescription = bookDescField.getText().toString();
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
                    Intent getBookDescription = new Intent(GetBookDescriptionActivity.this, GetBookClassActivity.class);
                    getBookDescription.putExtra("IS_TEXTBOOK", isTextbook);
                    getBookDescription.putExtra("BOOK_NAME",bookName);
                    getBookDescription.putExtra("BOOK_DESCRIPTION",bookDescription);
                    startActivity(getBookDescription);
                }
            }
        });

    }
}
