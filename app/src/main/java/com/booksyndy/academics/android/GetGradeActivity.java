package com.booksyndy.academics.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class GetGradeActivity extends AppCompatActivity {
    private boolean isParent, phoneNumberPublic, modeSwitched;
    private TextView gradeQuestion, gradeInstructions;
    private RadioGroup grades;
    private String firstName, lastName, username;
    private int grade, gradeNumber;
//     Grade numbers:
//     1 to 5: 1
//     6 to 8: 2
//     9: 3
//     10: 4
//     11:5
//     12: 6
//     UG: 7

    boolean tmp=true;
    Intent getBoard;
    private int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_grade);
        tmp=true;
        getSupportActionBar().setTitle("Sign up");
        isParent = getIntent().getBooleanExtra("IS_PARENT", false);
        firstName = getIntent().getStringExtra("FIRST_NAME");
        lastName = getIntent().getStringExtra("LAST_NAME");
        username = getIntent().getStringExtra("USERNAME");
        userType = getIntent().getIntExtra("USER_TYPE",1);
        phoneNumberPublic = getIntent().getBooleanExtra("PUBLIC_PHONE",true);
        modeSwitched = getIntent().getBooleanExtra("MODE_SWITCHED",false);

//        password = getIntent().getStringExtra("PASSWORD");
        gradeQuestion = (TextView) findViewById(R.id.gradeQuestionTV);
        if (userType==2) {
            gradeQuestion.setText(R.string.parent_grade_q);
        }
        else if (userType==3) {
            gradeQuestion.setText(R.string.which_grade_teacher);
        }
        grades = (RadioGroup) findViewById(R.id.gradesButtonList);
        getBoard = new Intent(GetGradeActivity.this, GetBoardActivity.class);
        getBoard.putExtra("IS_PARENT", isParent);
        getBoard.putExtra("FIRST_NAME",firstName);
        getBoard.putExtra("LAST_NAME",lastName);
        getBoard.putExtra("USERNAME",username);
        getBoard.putExtra("USER_TYPE",userType);
        getBoard.putExtra("PUBLIC_PHONE",phoneNumberPublic);
        getBoard.putExtra("MODE_SWITCHED",modeSwitched);

        gradeInstructions = findViewById(R.id.settingGradeInstructions);
//        getBoard.putExtra("PASSWORD",password);
        if (userType!=1) {
            gradeInstructions.setVisibility(View.INVISIBLE);
        }
        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab5);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grade = grades.getCheckedRadioButtonId();
                if (grade==-1) {
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
                else if (grade==R.id.grade5OrBelow) {
                    gradeNumber=1;
                }
                else if (grade==R.id.grade6to8) {
                    gradeNumber=2;
                }
                else if (grade==R.id.grade9) {
                    gradeNumber=3;
                }
                else if (grade==R.id.grade10) {
                    gradeNumber=4;
                }
                else if (grade==R.id.grade11) {
                    gradeNumber=5;
                }
                else if (grade==R.id.grade12) {
                    gradeNumber=6;
                }
                else if (grade==R.id.university) {
                    gradeNumber=7;
                    Intent getDegree = new Intent(GetGradeActivity.this, GetCollegeSpecificsActivity.class);
                    getDegree.putExtra("IS_PARENT", isParent);
                    getDegree.putExtra("FIRST_NAME",firstName);
                    getDegree.putExtra("LAST_NAME",lastName);
                    getDegree.putExtra("GRADE_NUMBER",gradeNumber);
                    getDegree.putExtra("USERNAME",username);
                    getDegree.putExtra("USER_TYPE",userType);
                    getDegree.putExtra("PUBLIC_PHONE",phoneNumberPublic);
                    getDegree.putExtra("MODE_SWITCHED",modeSwitched);
//                    getDegree.putExtra("PASSWORD",password);
                    startActivity(getDegree);
                    tmp = false;
                }
                getBoard.putExtra("GRADE_NUMBER",gradeNumber);
                if (grade!=-1 && grade!=7 && tmp) {
                    startActivity(getBoard);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tmp=true;
    }
}
