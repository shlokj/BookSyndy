package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class GetJoinPurposeActivity extends AppCompatActivity {

    boolean isParent, toSell;
    TextView reasonsQuestion;
    RadioGroup reasons;
    String firstName, lastName;
    int gradeNumber, reason, boardNumber;
    Intent startMainActivity;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_join_purpose);


        isParent = getIntent().getBooleanExtra("IS_PARENT", false);
        firstName = getIntent().getStringExtra("FIRST_NAME");
        lastName = getIntent().getStringExtra("LAST_NAME");
        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER",4);
        boardNumber = getIntent().getIntExtra("BOARD_NUMBER",1);
        reasonsQuestion = (TextView) findViewById(R.id.reasonQuestionTV);
        reasons = (RadioGroup) findViewById(R.id.reasonsButtonList);
        reasons.clearCheck();
        startMainActivity = new Intent(GetJoinPurposeActivity.this, MainActivity.class);
//        startMainActivity.putExtra("IS_PARENT", isParent);
//        startMainActivity.putExtra("FIRST_NAME",firstName);
//        startMainActivity.putExtra("LAST_NAME",lastName);
//        startMainActivity.putExtra("GRADE_NUMBER",gradeNumber);
        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab7);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // This is the last activity in which we ask the user for information.
                // After this, on clicking the next button, the user account creation is completed and the user is taken
                // to the main activity.
                // Firebase implementation is required here.
                reason = reasons.getCheckedRadioButtonId();
                if (reason==-1) {
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
                else if (reason==R.id.toSell) {
                    toSell=true;
                }
                else if (reason==R.id.toBuy) {
                    toSell=false;
                }
//                 put firebase-related code here
            }
        });
    }
}
