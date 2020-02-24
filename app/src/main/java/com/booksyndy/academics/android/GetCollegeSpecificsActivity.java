package com.booksyndy.academics.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class GetCollegeSpecificsActivity extends AppCompatActivity {

    boolean isParent, validYear;
    TextView degreeQuestion;
    RadioGroup degrees;
    String firstName, lastName, username;
    int gradeNumber, degree, degreeNumber, yearNumber; // degree number is same as board number
    Intent getFinalAnswer;
    EditText yearField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_college_specifics);
        getSupportActionBar().setTitle("Sign up");
        yearField = (EditText) findViewById(R.id.yearField);
        yearField.setFocusable(false);
        isParent = getIntent().getBooleanExtra("IS_PARENT", false);
        firstName = getIntent().getStringExtra("FIRST_NAME");
        lastName = getIntent().getStringExtra("LAST_NAME");
        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER",4);
        username = getIntent().getStringExtra("USERNAME");
//        password = getIntent().getStringExtra("PASSWORD");
        degreeQuestion = (TextView) findViewById(R.id.degreeQuestionTV);
        degrees = (RadioGroup) findViewById(R.id.degreesButtonList);
//        degrees.requestFocus();
        getFinalAnswer = new Intent(GetCollegeSpecificsActivity.this, GetJoinPurposeActivity.class);
        getFinalAnswer.putExtra("IS_PARENT", isParent);
        getFinalAnswer.putExtra("FIRST_NAME",firstName);
        getFinalAnswer.putExtra("LAST_NAME",lastName);
        getFinalAnswer.putExtra("GRADE_NUMBER",gradeNumber);
        getFinalAnswer.putExtra("USERNAME",username);
//        getFinalAnswer.putExtra("PASSWORD",password);
        degrees.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                yearField.setFocusableInTouchMode(true);
                yearField.setFocusable(true);
                yearField.requestFocus();
            }
        });
        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab8);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validYear=true;
                View parentLayout = findViewById(android.R.id.content);
                degree = degrees.getCheckedRadioButtonId();

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
                    yearNumber = Integer.parseInt(yearField.getText().toString());

                    if (degree == R.id.btech) {
                        degreeNumber = 7;
                        if (yearNumber > 4 || yearNumber==0) {
                            validYear = false;
                            displaySnackbarYears(4);
                        }
                    } else if (degree == R.id.bsc) {
                        degreeNumber = 8;
                        if (yearNumber > 4 || yearNumber==0) {
                            validYear = false;
                            displaySnackbarYears(4);
                        }
                    } else if (degree == R.id.bcom) {
                        degreeNumber = 9;
                        if (yearNumber > 4 || yearNumber==0) {
                            validYear = false;
                            displaySnackbarYears(4);
                        }
                    } else if (degree == R.id.ba) {
                        degreeNumber = 10;
                        if (yearNumber > 4 || yearNumber==0) {
                            validYear = false;
                            displaySnackbarYears(4);
                        }
                    } else if (degree == R.id.bba) {
                        degreeNumber = 11;
                        if (yearNumber > 4 || yearNumber==0) {
                            validYear = false;
                            displaySnackbarYears(4);
                        }
                    } else if (degree == R.id.bca) {
                        degreeNumber = 12;
                        if (yearNumber > 4 || yearNumber==0) {
                            validYear = false;
                            displaySnackbarYears(4);
                        }
                    } else if (degree == R.id.bed) {
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
                    } else if (degree == R.id.mbbs) {
                        degreeNumber = 15;
                        if (yearNumber > 6 || yearNumber==0) {
                            validYear = false;
                            displaySnackbarYears(6);
                        }
                    } else if (degree == R.id.otherDegree) {
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

                    getFinalAnswer.putExtra("DEGREE_NUMBER",degreeNumber);
                    getFinalAnswer.putExtra("YEAR_NUMBER",yearNumber);
                    if (degree!=-1 && validYear) {
                        startActivity(getFinalAnswer);
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
