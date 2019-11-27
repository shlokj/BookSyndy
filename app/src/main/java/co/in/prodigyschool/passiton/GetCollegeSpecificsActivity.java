package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class GetCollegeSpecificsActivity extends AppCompatActivity {

    boolean isParent;
    TextView degreeQuestion;
    RadioGroup degrees;
    String firstName, lastName;
    int gradeNumber, degree, degreeNumber; // degree number is same as board number
    Intent getFinalAnswer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_college_specifics);

        isParent = getIntent().getBooleanExtra("IS_PARENT", false);
        firstName = getIntent().getStringExtra("FIRST_NAME");
        lastName = getIntent().getStringExtra("LAST_NAME");
        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER",4);
        degreeQuestion = (TextView) findViewById(R.id.degreeQuestionTV);
        degrees = (RadioGroup) findViewById(R.id.degreesButtonList);
        getFinalAnswer = new Intent(GetCollegeSpecificsActivity.this, GetJoinPurposeActivity.class);
        getFinalAnswer.putExtra("IS_PARENT", isParent);
        getFinalAnswer.putExtra("FIRST_NAME",firstName);
        getFinalAnswer.putExtra("LAST_NAME",lastName);
        getFinalAnswer.putExtra("GRADE_NUMBER",gradeNumber);
        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab8);
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
                else if (degree==R.id.otherDegree) {
                    degreeNumber=16;
                }
                getFinalAnswer.putExtra("DEGREE_NUMBER",degreeNumber);
                if (degree!=-1) {
                    startActivity(getFinalAnswer);
                }
            }
        });
        
    }
}
