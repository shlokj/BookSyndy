package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class GetBookBoardActivity extends AppCompatActivity {

    boolean isTextbook;
    TextView bookBoardQuestion;
    RadioGroup boards;
    String bookName, bookDescription;
    int board;
    int gradeNumber, boardNumber;
    Intent getPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_book_board);

        isTextbook = getIntent().getBooleanExtra("IS_TEXTBOOK", true);
        bookName = getIntent().getStringExtra("BOOK_NAME");
        bookDescription = getIntent().getStringExtra("BOOK_DESCRIPTION");
        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER",4);
        bookBoardQuestion = (TextView) findViewById(R.id.bookBoardQuestionTV);
        if (!isTextbook) {
            bookBoardQuestion.setText("Which educational board is your material for?");
        }
        boards = (RadioGroup) findViewById(R.id.bookBoardsButtonList);
        getPrice = new Intent(GetBookBoardActivity.this, GetBookPriceActivity.class);
        getPrice.putExtra("IS_TEXTBOOK", isTextbook);
        getPrice.putExtra("BOOK_NAME",bookName);
        getPrice.putExtra("BOOK_DESCRIPTION",bookDescription);
        getPrice.putExtra("GRADE_NUMBER",gradeNumber);
        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab15);
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
                else if (board==R.id.cbseBook) {
                    boardNumber=1;
                }
                else if (board==R.id.icseBook) {
                    boardNumber=2;
                }
                else if (board==R.id.ibBook) {
                    boardNumber=3;
                }
                else if (board==R.id.igcseBook) {
                    boardNumber=4;
                }
                else if (board==R.id.stateBook) {
                    boardNumber=5;
                }
                else if (board==R.id.otherBoardBook) {
                    boardNumber=6;
                }
                getPrice.putExtra("BOARD_NUMBER",boardNumber);
                if (board!=-1) {
                    startActivity(getPrice);
                }
            }
        });
    }
}
