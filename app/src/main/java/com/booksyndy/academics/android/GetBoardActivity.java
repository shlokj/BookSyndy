package com.booksyndy.academics.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class GetBoardActivity extends AppCompatActivity {

    boolean isParent;
    TextView boardQuestion;
    RadioGroup boards;
    String firstName, lastName, username;
    int gradeNumber, board, boardNumber;
    Intent getFinalAnswer;
    CheckBox competitiveExam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_board);
        getSupportActionBar().setTitle("Sign up");
        isParent = getIntent().getBooleanExtra("IS_PARENT", false);
        firstName = getIntent().getStringExtra("FIRST_NAME");
        lastName = getIntent().getStringExtra("LAST_NAME");
        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER",4);
        username = getIntent().getStringExtra("USERNAME");
        boardQuestion =  findViewById(R.id.boardQuestionTV);
        if (isParent) {
            boardQuestion.setText("Which board is your child studying under?");
        }
        boards = findViewById(R.id.boardsButtonList);
        getFinalAnswer = new Intent(GetBoardActivity.this, GetJoinPurposeActivity.class);
        getFinalAnswer.putExtra("IS_PARENT", isParent);
        getFinalAnswer.putExtra("FIRST_NAME",firstName);
        getFinalAnswer.putExtra("LAST_NAME",lastName);
        getFinalAnswer.putExtra("GRADE_NUMBER",gradeNumber);
        getFinalAnswer.putExtra("USERNAME",username);
        competitiveExam =  findViewById(R.id.competitiveExam);
        competitiveExam.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        if (gradeNumber<3 || gradeNumber>6) {
            competitiveExam.setVisibility(View.GONE);
        }
        FloatingActionButton next =  findViewById(R.id.fab6);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                board = boards.getCheckedRadioButtonId();
                if (board==-1) {
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
                else if (board==R.id.cbseBoard) {
                    boardNumber=1;
                }
                else if (board==R.id.icseBoard) {
                    boardNumber=2;
                }
                else if (board==R.id.ibBoard) {
                    boardNumber=3;
                }
                else if (board==R.id.igcseBoard) {
                    boardNumber=4;
                }
                else if (board==R.id.stateBoard) {
                    boardNumber=5;
                }
                else if (board==R.id.otherBoard) {
                    boardNumber=6;
                }
                getFinalAnswer.putExtra("BOARD_NUMBER",boardNumber);
                getFinalAnswer.putExtra("COMPETITIVE_EXAM",competitiveExam.isChecked());
                if (board!=-1) {
                    startActivity(getFinalAnswer);
                }
            }
        });
    }
}