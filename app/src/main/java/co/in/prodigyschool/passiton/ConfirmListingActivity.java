package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;


public class ConfirmListingActivity extends AppCompatActivity {


    boolean isTextbook;
    String bookName, bookDescription, phoneNumber, userId, bookAddress, bookImageUrl;
    int gradeNumber, boardNumber;
    private int bookPrice;
    private TextView bookNameTV, bookDescriptionTV, bookTypeTV, bookCategoryTV, bookPriceTV, bookLocTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_listing);

        getSupportActionBar().setTitle("Confirm your listing");
/*
        ScrollView detailsSV = findViewById(R.id.scrollView2);
        ViewGroup.LayoutParams params = detailsSV.getLayoutParams();
        params.height = params.height-112;
        params.width = params.width;
        detailsSV.setLayoutParams(params);*/
        isTextbook = getIntent().getBooleanExtra("IS_TEXTBOOK", true);
        bookName = getIntent().getStringExtra("BOOK_NAME");
        bookDescription = getIntent().getStringExtra("BOOK_DESCRIPTION");
        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER", 4);
        boardNumber = getIntent().getIntExtra("BOARD_NUMBER", 1);
        bookPrice = getIntent().getIntExtra("BOOK_PRICE", 0);
        bookImageUrl = getIntent().getStringExtra("BOOK_IMAGE_URL");
        bookAddress = getIntent().getStringExtra("BOOK_LOCATION");

        bookNameTV = findViewById(R.id.bookTitleFinal);
        bookDescriptionTV = findViewById(R.id.bookDescriptionFinal);
        bookTypeTV = findViewById(R.id.bookTypeFinal);
        bookCategoryTV = findViewById(R.id.bookCategoryFinal);
        bookPriceTV = findViewById(R.id.bookPriceFinal);
        bookLocTV = findViewById(R.id.bookLocFinal);

        bookNameTV.setText(bookName);
        bookDescriptionTV.setText(bookDescription);
        if (isTextbook) {
            bookTypeTV.setText("Textbook");
        } else {
            bookTypeTV.setText("Notes / other material");
        }
        if (boardNumber == 20) {
            bookCategoryTV.setText("Competitive exams");
        } else {
            if (gradeNumber == 1) {
                bookCategoryTV.setText("Grade 5 or below");
            } else if (gradeNumber == 2) {
                bookCategoryTV.setText("Grade 6 to 8");
            } else if (gradeNumber == 3) {
                bookCategoryTV.setText("Grade 9");
            } else if (gradeNumber == 4) {
                bookCategoryTV.setText("Grade 10");
            } else if (gradeNumber == 5) {
                bookCategoryTV.setText("Grade 11");
            } else if (gradeNumber == 6) {
                bookCategoryTV.setText("Grade 12");
            } else if (gradeNumber == 7) {
                bookCategoryTV.setText("Undergraduate");
//                Toast.makeText(getApplicationContext(),"Grade number: 7\nBoard number: "+boardNumber,Toast.LENGTH_SHORT).show();

                if (boardNumber == 7) {
                    bookCategoryTV.append(", B. Tech");
                } else if (boardNumber == 8) {
                    bookCategoryTV.append(", B. Sc");
                } else if (boardNumber == 9) {
                    bookCategoryTV.append(", B. Com");
                } else if (boardNumber == 10) {
                    bookCategoryTV.append(", BA");
                } else if (boardNumber == 11) {
                    bookCategoryTV.append(", BBA");
                } else if (boardNumber == 12) {
                    bookCategoryTV.append(", BCA");
                } else if (boardNumber == 13) {
                    bookCategoryTV.append(", B. Ed");
                } else if (boardNumber == 14) {
                    bookCategoryTV.append(", LLB");
                } else if (boardNumber == 15) {
                    bookCategoryTV.append(", MBBS");
                } else if (boardNumber == 16) {
                    bookCategoryTV.append(", other degree");
                }

                if (boardNumber == 1) {
                    bookCategoryTV.append(", CBSE");
                } else if (boardNumber == 2) {
                    bookCategoryTV.append(", ICSE//ISC");
                } else if (boardNumber == 3) {
                    bookCategoryTV.append(", IB");
                } else if (boardNumber == 4) {
                    bookCategoryTV.append(", IGCSE");
                } else if (boardNumber == 5) {
                    bookCategoryTV.append(", state board");
                } else if (boardNumber == 6) {
                    bookCategoryTV.append(", other board");
                }
                //TODO: append degree
            }
            bookPriceTV.setText("₹" + bookPrice);
            bookLocTV.setText(bookAddress);
        }
    }
}
