package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class GetBookDegreeActivity extends AppCompatActivity {

    boolean isTextbook;
    String bookName, bookDescription;
    int gradeNumber, boardNumber, degreeNumber, degree;
    RadioGroup degrees;
    Intent getPrice;

    TextView bookDegreeQ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_book_degree);

        degrees = (RadioGroup) findViewById(R.id.bookDegreesButtonList);

        isTextbook = getIntent().getBooleanExtra("IS_TEXTBOOK", true);
        bookName = getIntent().getStringExtra("BOOK_NAME");
        bookDescription = getIntent().getStringExtra("BOOK_DESCRIPTION");
        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER",4);
        boardNumber = getIntent().getIntExtra("BOARD_NUMBER",1);
//TODO: Change default board number
        getPrice = new Intent(GetBookDegreeActivity.this, GetBookPriceActivity.class);
        bookDegreeQ = (TextView) findViewById(R.id.bookDegreeQuestionTV);
        if (isTextbook) {

        }
        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab16);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                degree = degrees.getCheckedRadioButtonId();
                if (degree==-1) {
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
                else if (degree==R.id.btech) {
                    degreeNumber=7;
                }
                else if (degree==R.id.bsc) {
                    degreeNumber=8;
                }
                else if (degree==R.id.bcom) {
                    degreeNumber=9;
                }
                else if (degree==R.id.ba) {
                    degreeNumber=10;
                }
                else if (degree==R.id.bba) {
                    degreeNumber=11;
                }
                else if (degree==R.id.bca) {
                    degreeNumber=12;
                }
                else if (degree==R.id.bed) {
                    degreeNumber=13;
                }
                else if (degree==R.id.llb) {
                    degreeNumber=14;
                }
                else if (degree==R.id.mbbs) {
                    degreeNumber=15;
                }
                else if (degree==R.id.otherDegreeBook) {
                    degreeNumber=16;
                }
                getPrice.putExtra("DEGREE_NUMBER",degreeNumber);
                if (degree!=-1) {
                    startActivity(getPrice);
                }
            }
        });
    }
}
