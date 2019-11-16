package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class CustNameActivity extends AppCompatActivity {

    boolean isParent;
    EditText firstNameField, lastNameField;
    String firstName, lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_name);
        isParent = getIntent().getBooleanExtra("IS_PARENT",false);
        firstNameField = (EditText) findViewById(R.id.firstName);
        lastNameField = (EditText) findViewById(R.id.lastName);
        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab4);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName = firstNameField.getText().toString();
                lastName = lastNameField.getText().toString();
                if (firstName.length()==0 || lastName.length()==0) {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Please fill in both fields above", Snackbar.LENGTH_SHORT)
                            .setAction("OKAY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                }
                else {
                    Intent getGrade = new Intent(CustNameActivity.this, GetGradeActivity.class);
                    getGrade.putExtra("IS_PARENT", isParent);
                    getGrade.putExtra("FIRST_NAME",firstName);
                    getGrade.putExtra("LAST_NAME",lastName);
                    startActivity(getGrade);
                }
            }
        });
    }
}
