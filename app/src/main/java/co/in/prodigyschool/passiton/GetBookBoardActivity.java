package co.in.prodigyschool.passiton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class GetBookBoardActivity extends AppCompatActivity {

    boolean isTextbook;
    TextView bookBoardQuestion;
    RadioGroup boards;
    String bookName, bookDescription,selectedImage;
    int board;
    int gradeNumber, boardNumber;
    Intent getPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_book_board);
        getSupportActionBar().setTitle("List a book");
        selectedImage = getIntent().getStringExtra("BOOK_IMAGE_URI");
        isTextbook = getIntent().getBooleanExtra("IS_TEXTBOOK", true);
        bookName = getIntent().getStringExtra("BOOK_NAME");
        bookDescription = getIntent().getStringExtra("BOOK_DESCRIPTION");
        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER",4);
        boardNumber = getIntent().getIntExtra("BOARD_NUMBER", 1);

        bookBoardQuestion = (TextView) findViewById(R.id.bookBoardQuestionTV);
        boards = (RadioGroup) findViewById(R.id.bookBoardsButtonList);

        if (!isTextbook) {
            bookBoardQuestion.setText(R.string.material_board_q);
        }
        if (boardNumber==1) {
            boards.check(R.id.cbseBook);
        }
        else if (boardNumber==2) {
            boards.check(R.id.icseBook);
        }
        else if (boardNumber==3) {
            boards.check(R.id.ibBook);
        }
        else if (boardNumber==4) {
            boards.check(R.id.igcseBook);
        }
        else if (boardNumber==5) {
            boards.check(R.id.stateBook);
        }
        else if (boardNumber==6) {
            boards.check(R.id.otherBoardBook);
        }
        getPrice = new Intent(GetBookBoardActivity.this, GetBookPriceActivity.class);
        getPrice.putExtra("BOOK_IMAGE_URI",selectedImage);
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
