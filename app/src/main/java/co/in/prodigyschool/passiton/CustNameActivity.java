package co.in.prodigyschool.passiton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class CustNameActivity extends AppCompatActivity {

    boolean isParent, isValidUsername, isAvailableUsername=true;
    EditText firstNameField, lastNameField, userIdField;
    String firstName, lastName, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_name);
        final View parentLayout = findViewById(android.R.id.content);
        isParent = getIntent().getBooleanExtra("IS_PARENT",false);
        userIdField = (EditText) findViewById(R.id.usernameField);
        firstNameField = (EditText) findViewById(R.id.firstName);
        lastNameField = (EditText) findViewById(R.id.lastName);
        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab4);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName = firstNameField.getText().toString();
                lastName = lastNameField.getText().toString();
                username = userIdField.getText().toString();
                isValidUsername = false;
                isValidUsername = (username != null) && username.matches("[A-Za-z0-9_]+");

                if (firstName.length()==0 || lastName.length()==0) {
                    Snackbar.make(parentLayout, "Please fill in both name fields", Snackbar.LENGTH_SHORT)
                            .setAction("OKAY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                }
                else if (!(isValidUsername && isAvailableUsername)) {
//                    userIdField.getBackground().setColorFilter("#EE0000", PorterDuff.Mode.SRC_IN);
                    if (!isValidUsername) {
                        Snackbar.make(parentLayout, "The username you entered is not valid."/* A valid username has only letters (a-z) and numbers (0-9) and is at least 5 characters long.*/ , Snackbar.LENGTH_SHORT)
                                .setAction("OKAY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                .show();
                    }
                    else if (!isAvailableUsername) {
                        Snackbar.make(parentLayout, "This username is taken. Please try another.", Snackbar.LENGTH_SHORT)
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
                    Intent getGrade = new Intent(CustNameActivity.this, GetGradeActivity.class);
                    getGrade.putExtra("IS_PARENT", isParent);
                    getGrade.putExtra("FIRST_NAME",firstName);
                    getGrade.putExtra("LAST_NAME",lastName);
                    getGrade.putExtra("USERNAME",username);//TODO: username pass on implementation for other registration info activities
                    startActivity(getGrade);
                }
            }
        });
    }
}
