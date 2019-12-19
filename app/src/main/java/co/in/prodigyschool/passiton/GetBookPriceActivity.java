package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class GetBookPriceActivity extends AppCompatActivity {

    boolean isTextbook, collegeStudent;
    String bookName, bookDescription;
    int gradeNumber, boardNumber;
    Switch forFree;
    TextView pricingInstructions;
    EditText priceField;
    int price=0;
    Intent getLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_book_price);
        getSupportActionBar().setTitle("List a book");
        pricingInstructions = (TextView) findViewById(R.id.pricingInstructions);
        pricingInstructions.setVisibility(View.INVISIBLE);
        priceField = (EditText) findViewById(R.id.bookPriceField);
        priceField.setEnabled(false);
        priceField.setFocusable(false);
        forFree = (Switch) findViewById(R.id.switchFree);
        isTextbook = getIntent().getBooleanExtra("IS_TEXTBOOK", true);
        bookName = getIntent().getStringExtra("BOOK_NAME");
        bookDescription = getIntent().getStringExtra("BOOK_DESCRIPTION");
        gradeNumber = getIntent().getIntExtra("GRADE_NUMBER",4);
        collegeStudent = getIntent().getBooleanExtra("COLLEGE_STUDENT",false);
        if (collegeStudent) {
//            Toast.makeText(getApplicationContext(),"College student",Toast.LENGTH_SHORT).show();
            boardNumber = getIntent().getIntExtra("DEGREE_NUMBER", 7);
        }
        else {
//            Toast.makeText(getApplicationContext(),"School student",Toast.LENGTH_SHORT).show();
            boardNumber = getIntent().getIntExtra("BOARD_NUMBER", 1);
        }
//        Toast.makeText(getApplicationContext(),"Board number: "+boardNumber,Toast.LENGTH_SHORT).show();

        if(!isTextbook) {
            pricingInstructions.setText(R.string.material_pricing_instructions);
        }

        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab17);

        forFree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!(forFree.isChecked())) {
                    priceField.setEnabled(true);
                    priceField.setFocusableInTouchMode(true);
                    priceField.setFocusable(true);
                    priceField.setText("");
                    pricingInstructions.setVisibility(View.VISIBLE);
                    priceField.requestFocus();
                }
                else {
                    priceField.setEnabled(false);
                    priceField.setFocusableInTouchMode(false);
                    priceField.setFocusable(false);
                    priceField.setText("0");
                    pricingInstructions.setVisibility(View.INVISIBLE);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (priceField.getText().toString().length()>0) {
                    try {
                        price = Integer.parseInt(priceField.getText().toString());
                        getLocation = new Intent(GetBookPriceActivity.this, GetBookSellerLocationActivity.class);
                        getLocation.putExtra("IS_TEXTBOOK", isTextbook);
                        getLocation.putExtra("BOOK_NAME",bookName);
                        getLocation.putExtra("BOOK_DESCRIPTION",bookDescription);
                        getLocation.putExtra("GRADE_NUMBER",gradeNumber);
                        getLocation.putExtra("BOARD_NUMBER",boardNumber);
                        getLocation.putExtra("BOOK_PRICE",price);
                        startActivity(getLocation);
                    } catch (Exception e) {
                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout, "Please enter a valid price", Snackbar.LENGTH_SHORT)
                                .setAction("OKAY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                .show();
                    }
                }
                else {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Please enter a valid price or be generous :)", Snackbar.LENGTH_SHORT)
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
}
