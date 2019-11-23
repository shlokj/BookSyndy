package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class GetBookClassActivity extends AppCompatActivity {

    boolean isTextbook;
    TextView gradeQuestion;
    RadioGroup grades;
    String bookName, bookDescription;
    int grade;
    int gradeNumber;
    boolean tmp=true;
    Intent getBookBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_book_class);
        isTextbook = getIntent().getBooleanExtra("IS_TEXTBOOK", true);
        bookName = getIntent().getStringExtra("BOOK_NAME");
        bookDescription = getIntent().getStringExtra("BOOK_DESCRIPTION");
        gradeQuestion = (TextView) findViewById(R.id.bookGradeQuestionTV);
        if (!isTextbook) {
            gradeQuestion.setText(R.string.notes_grade_question);
        }
        grades = (RadioGroup) findViewById(R.id.bookGradesButtonList);
        grades.clearCheck();
        getBookBoard = new Intent(GetBookClassActivity.this, GetBookBoardActivity.class);
        getBookBoard.putExtra("IS_TEXTBOOK", isTextbook);
        getBookBoard.putExtra("BOOK_NAME",bookName);
        getBookBoard.putExtra("BOOK_DESCRIPTION",bookDescription);
        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab14);
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
                else if (grade==R.id.grade5OrBelowBook) {
                    gradeNumber=1;
                }
                else if (grade==R.id.grade6to8Book) {
                    gradeNumber=2;
                }
                else if (grade==R.id.grade9Book) {
                    gradeNumber=3;
                }
                else if (grade==R.id.grade10Book) {
                    gradeNumber=4;
                }
                else if (grade==R.id.grade11Book) {
                    gradeNumber=5;
                }
                else if (grade==R.id.grade12Book) {
                    gradeNumber=6;
                }
                else if (grade==R.id.universityBook) {
                    gradeNumber=7;
                    Intent getBookDegree = new Intent(GetBookClassActivity.this, GetBookDegreeActivity.class);
                    getBookDegree.putExtra("IS_PARENT", isTextbook);
                    getBookDegree.putExtra("BOOK_NAME",bookName);
                    getBookDegree.putExtra("BOOK_DESCRIPTION",bookDescription);
                    getBookDegree.putExtra("GRADE_NUMBER",gradeNumber);
                    startActivity(getBookDegree);
                    tmp = false;
                }
                getBookBoard.putExtra("GRADE_NUMBER",gradeNumber);
                if (grade!=-1 && grade!=7 && tmp) {
                    startActivity(getBookBoard);
                }
            }
        });
    }
}
