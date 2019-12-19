package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
        getSupportActionBar().setTitle("List a book");

        degrees = (RadioGroup) findViewById(R.id.bookDegreesButtonList);

        isTextbook = getIntent().getBooleanExtra("IS_TEXTBOOK", true);
        bookName = getIntent().getStringExtra("BOOK_NAME");
        bookDescription = getIntent().getStringExtra("BOOK_DESCRIPTION");
        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER",4);
        boardNumber = getIntent().getIntExtra("BOARD_NUMBER",6);
        getPrice = new Intent(GetBookDegreeActivity.this, GetBookPriceActivity.class);
        bookDegreeQ = (TextView) findViewById(R.id.bookDegreeQuestionTV);

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
                else if (degree==R.id.btechBook) {
                    degreeNumber=7;
                }
                else if (degree==R.id.bscBook) {
                    degreeNumber=8;
                }
                else if (degree==R.id.bcomBook) {
                    degreeNumber=9;
                }
                else if (degree==R.id.baBook) {
                    degreeNumber=10;
                }
                else if (degree==R.id.bbaBook) {
                    degreeNumber=11;
                }
                else if (degree==R.id.bcaBook) {
                    degreeNumber=12;
                }
                else if (degree==R.id.bedBook) {
                    degreeNumber=13;
                }
                else if (degree==R.id.llbBook) {
                    degreeNumber=14;
                }
                else if (degree==R.id.mbbsBook) {
                    degreeNumber=15;
                }
                else if (degree==R.id.otherDegreeBook) {
                    degreeNumber=16;
                }
                getPrice.putExtra("IS_TEXTBOOK", isTextbook);
                getPrice.putExtra("BOOK_NAME",bookName);
                getPrice.putExtra("BOOK_DESCRIPTION",bookDescription);
                getPrice.putExtra("GRADE_NUMBER",gradeNumber);
                getPrice.putExtra("DEGREE_NUMBER",degreeNumber);
                getPrice.putExtra("COLLEGE_STUDENT",true);
//                Toast.makeText(getApplicationContext(),"Degree number: "+degreeNumber,Toast.LENGTH_SHORT).show();
                if (degree!=-1) {
                    startActivity(getPrice);
                }
            }
        });
    }

    public void displaySnackbarYears(int year) {
        String yearNum = Integer.valueOf(year).toString();
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, "Please enter a year " + yearNum + " or below", Snackbar.LENGTH_SHORT)
                .setAction("OKAY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }
}
