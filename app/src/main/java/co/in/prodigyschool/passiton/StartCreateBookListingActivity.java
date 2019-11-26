package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class StartCreateBookListingActivity extends AppCompatActivity {

    RadioGroup materialTypeButtons;
    int bookTypeId;
    boolean isTextbook;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_create_book_listing);

        materialTypeButtons = (RadioGroup) findViewById(R.id.materialTypeRadioGroup);
        FloatingActionButton next = findViewById(R.id.fab11);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookTypeId = materialTypeButtons.getCheckedRadioButtonId();
                if (bookTypeId==-1) {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Please select an option", Snackbar.LENGTH_SHORT)
                            .setAction("OKAY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                }
                else if (bookTypeId==R.id.textbookOption) {
                    isTextbook=true;
                    Intent getBookName = new Intent(StartCreateBookListingActivity.this, GetBookNameActivity.class);
                    getBookName.putExtra("IS_TEXTBOOK", isTextbook);
                    startActivity(getBookName);
                }
                else if (bookTypeId==R.id.notesOption) {
                    isTextbook=false;
                    Intent getBookName = new Intent(StartCreateBookListingActivity.this, GetBookNameActivity.class);
                    getBookName.putExtra("IS_TEXTBOOK", isTextbook);
                    startActivity(getBookName);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent homeActivity = new Intent(StartCreateBookListingActivity.this,HomeActivity.class);
        startActivity(homeActivity);
    }
}
