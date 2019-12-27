package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class GetBookDegreeActivity extends AppCompatActivity {

    boolean isTextbook,validYear;
    String bookName, bookDescription,selectedImage;
    int gradeNumber, boardNumber, degreeNumber, degree, yearNumber;
    RadioGroup degrees;
    Intent getPrice;
    EditText yearField;
    TextView bookDegreeQ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_book_degree);
        Objects.requireNonNull(getSupportActionBar()).setTitle("List a book");

        degrees = findViewById(R.id.bookDegreesButtonList);
        selectedImage = getIntent().getStringExtra("BOOK_IMAGE_URI");
        isTextbook = getIntent().getBooleanExtra("IS_TEXTBOOK", true);
        bookName = getIntent().getStringExtra("BOOK_NAME");
        bookDescription = getIntent().getStringExtra("BOOK_DESCRIPTION");
        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER",4);
        boardNumber = getIntent().getIntExtra("BOARD_NUMBER",6);
        getPrice = new Intent(GetBookDegreeActivity.this, GetBookPriceActivity.class);
        bookDegreeQ = findViewById(R.id.bookDegreeQuestionTV);
        yearField = findViewById(R.id.bookYearField);

        if (boardNumber==7) {
            degrees.check(R.id.btechBook);
        }
        else if (boardNumber==8) {
            degrees.check(R.id.bscBook);
        }
        else if (boardNumber==9) {
            degrees.check(R.id.bcomBook);
        }
        else if (boardNumber==10) {
            degrees.check(R.id.baBook);
        }
        else if (boardNumber==11) {
            degrees.check(R.id.bbaBook);
        }
        else if (boardNumber==12) {
            degrees.check(R.id.bcaBook);
        }
        else if (boardNumber==13) {
            degrees.check(R.id.bedBook);
        }
        else if (boardNumber==14) {
            degrees.check(R.id.llbBook);
        }
        else if (boardNumber==15) {
            degrees.check(R.id.mbbsBook);
        }
        else if (boardNumber==16) {
            degrees.check(R.id.otherDegreeBook);
        }

        degrees.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                yearField.setFocusableInTouchMode(true);
                yearField.setFocusable(true);
                yearField.requestFocus();
            }
        });
        FloatingActionButton next = findViewById(R.id.fab16);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validYear=true;
                View parentLayout = findViewById(android.R.id.content);

                if (degree==-1) {
                    if (yearField.getText().toString().length()==0) {
                        Snackbar.make(parentLayout, "Please enter your degree and year", Snackbar.LENGTH_SHORT)
                                .setAction("OKAY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                .show();
                    }
                }
                else if (yearField.getText().toString().length()==0) {
                    Snackbar.make(parentLayout, "Please enter your year", Snackbar.LENGTH_SHORT)
                            .setAction("OKAY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                }
                else if (yearField.getText().toString().length()==1) {
                    degree = degrees.getCheckedRadioButtonId();
//                    Toast.makeText(getApplicationContext(),"Check",Toast.LENGTH_SHORT).show();
                    yearNumber = Integer.parseInt(yearField.getText().toString());
                    if (degree == R.id.btechBook) {
                        degreeNumber = 7;
                        if (yearNumber > 4 || yearNumber==0) {
                            validYear = false;
                            displaySnackbarYears(4);
                        }
                    } else if (degree == R.id.bscBook) {
                        degreeNumber = 8;
                        if (yearNumber > 4 || yearNumber==0) {
                            validYear = false;
                            displaySnackbarYears(4);
                        }
                    } else if (degree == R.id.bcomBook) {
                        degreeNumber = 9;
                        if (yearNumber > 4 || yearNumber==0) {
                            validYear = false;
                            displaySnackbarYears(4);
                        }
                    } else if (degree == R.id.baBook) {
                        degreeNumber = 10;
                        if (yearNumber > 4 || yearNumber==0) {
                            validYear = false;
                            displaySnackbarYears(4);
                        }
                    } else if (degree == R.id.bbaBook) {
                        degreeNumber = 11;
                        if (yearNumber > 4 || yearNumber==0) {
                            validYear = false;
                            displaySnackbarYears(4);
                        }
                    } else if (degree == R.id.bcaBook) {
                        degreeNumber = 12;
                        if (yearNumber > 4 || yearNumber==0) {
                            validYear = false;
                            displaySnackbarYears(4);
                        }
                    } else if (degree == R.id.bedBook) {
                        degreeNumber = 13;
                        if (yearNumber > 4 || yearNumber==0) {
                            validYear = false;
                            displaySnackbarYears(4);
                        }
                    } else if (degree == R.id.llb) {
                        degreeNumber = 14;
                        if (yearNumber > 5 || yearNumber==0) {
                            validYear = false;
                            displaySnackbarYears(5);
                        }
                    } else if (degree == R.id.mbbsBook) {
                        degreeNumber = 15;
                        if (yearNumber > 6 || yearNumber==0) {
                            validYear = false;
                            displaySnackbarYears(6);
                        }
                    } else if (degree == R.id.otherDegreeBook) {
                        degreeNumber = 16;
                        if (yearNumber==0) {
                            validYear = false;
                            Snackbar.make(parentLayout, "Your year can't be 0", Snackbar.LENGTH_SHORT)
                                    .setAction("OKAY", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    })
                                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                    .show();
                        }
                    }
//                    Toast.makeText(getApplicationContext(),"Board number: "+degreeNumber,Toast.LENGTH_SHORT).show();
                    getPrice.putExtra("BOARD_NUMBER",degreeNumber);
                    getPrice.putExtra("BOOK_IMAGE_URI", selectedImage);
                    getPrice.putExtra("IS_TEXTBOOK", isTextbook);
                    getPrice.putExtra("BOOK_NAME",bookName);
                    getPrice.putExtra("BOOK_DESCRIPTION",bookDescription);
                    getPrice.putExtra("GRADE_NUMBER",gradeNumber);
                    getPrice.putExtra("DEGREE_NUMBER",degreeNumber);
                    getPrice.putExtra("YEAR_NUMBER",yearNumber);
                    getPrice.putExtra("COLLEGE_STUDENT",true);
                    if (degree!=-1 && validYear) {
                        startActivity(getPrice);
                    }
                }
                else {

                    Snackbar.make(parentLayout, "Please enter your year", Snackbar.LENGTH_SHORT)
                            .setAction("OKAY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                }
            }
        });
    }

    public void displaySnackbarYears(int year) {
        String yearNum = Integer.valueOf(year).toString();
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, "Please enter a valid year " + yearNum + " or below, and not 0", Snackbar.LENGTH_SHORT)
                .setAction("OKAY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }
}
