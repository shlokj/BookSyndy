package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class GetBookClassActivity extends AppCompatActivity {

    boolean isTextbook;
    private TextView gradeQuestion;
    private RadioGroup grades;
    private String bookName, bookDescription,selectedImage;
    int grade;
    int gradeNumber, boardNumber;
    boolean tmp=true;
    Intent getBookBoard, getPrice;
    private CheckBox compExamBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_book_class);
        getSupportActionBar().setTitle("List a book");
        selectedImage = getIntent().getStringExtra("BOOK_IMAGE_URI");
        isTextbook = getIntent().getBooleanExtra("IS_TEXTBOOK", true);
        bookName = getIntent().getStringExtra("BOOK_NAME");
        bookDescription = getIntent().getStringExtra("BOOK_DESCRIPTION");
        gradeQuestion = (TextView) findViewById(R.id.bookGradeQuestionTV);
        if (!isTextbook) {
            gradeQuestion.setText(R.string.notes_grade_question);
        }
        grades = (RadioGroup) findViewById(R.id.bookGradesButtonList);
        compExamBook = findViewById(R.id.competitiveExamBookCB);
        compExamBook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compExamBook.isChecked()) {
                    grades.clearCheck();
                    grades.setEnabled(false);
                    for(int i = 0; i < grades.getChildCount(); i++){
                        ((RadioButton)grades.getChildAt(i)).setEnabled(false);
                    }
                    gradeNumber=0;
                    boardNumber = 20;
                }
                else {
                    grades.setEnabled(true);
                    for(int i = 0; i < grades.getChildCount(); i++){
                        ((RadioButton)grades.getChildAt(i)).setEnabled(true);
                    }
                    boardNumber = 0;
                }
            }
        });
        getBookBoard = new Intent(GetBookClassActivity.this, GetBookBoardActivity.class);
        getBookBoard.putExtra("BOOK_IMAGE_URI", selectedImage);
        getBookBoard.putExtra("IS_TEXTBOOK", isTextbook);
        getBookBoard.putExtra("BOOK_NAME",bookName);
        getBookBoard.putExtra("BOOK_DESCRIPTION",bookDescription);
        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab14);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grade = grades.getCheckedRadioButtonId();
                if (grade==-1 && boardNumber!=20) {
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
                    getBookDegree.putExtra("BOOK_IMAGE_URI", selectedImage);
                    getBookDegree.putExtra("IS_TEXTBOOK", isTextbook);
                    getBookDegree.putExtra("BOOK_NAME",bookName);
                    getBookDegree.putExtra("BOOK_DESCRIPTION",bookDescription);
                    getBookDegree.putExtra("GRADE_NUMBER",gradeNumber);
                    startActivity(getBookDegree);
                    tmp = false;
                }
                else if (boardNumber==20) {
                    getPrice = new Intent(GetBookClassActivity.this, GetBookPriceActivity.class);
                    getPrice.putExtra("BOOK_IMAGE_URI", selectedImage);
                    getPrice.putExtra("IS_TEXTBOOK", isTextbook);
                    getPrice.putExtra("BOOK_NAME",bookName);
                    getPrice.putExtra("BOOK_DESCRIPTION",bookDescription);
                    getPrice.putExtra("GRADE_NUMBER",gradeNumber);
                    getPrice.putExtra("BOARD_NUMBER",boardNumber);
                    startActivity(getPrice);
                }
                getBookBoard.putExtra("GRADE_NUMBER",gradeNumber);
                if (grade!=-1 && grade!=7 && tmp && boardNumber!=20) {
                    startActivity(getBookBoard);
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
