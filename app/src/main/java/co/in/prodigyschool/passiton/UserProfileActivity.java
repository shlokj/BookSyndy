package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UserProfileActivity extends AppCompatActivity {

    ImageView profilePic;
    EditText fName, lName, year, phoneNumber;
    Spinner gradeSpinner, boardSpinner, degreeSpinner;
    FloatingActionButton saveChanges;
    TextWatcher checkChange;
    private Menu menu;
    private int clickCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        if(getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle("Profile");

        profilePic = findViewById(R.id.profilePic);

        fName = findViewById(R.id.firstNameProfile);
        lName = findViewById(R.id.lastNameProfile);
        year = findViewById(R.id.profileYearField);
        phoneNumber = findViewById(R.id.profilePhoneNumberField);

        gradeSpinner = findViewById(R.id.gradeSpinner);
        boardSpinner = findViewById(R.id.boardSpinner);
        degreeSpinner = findViewById(R.id.degreeSpinner);

        saveChanges = findViewById(R.id.fab_save);
        saveChanges.hide();

        checkChange = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                saveChanges.show();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        fName.addTextChangedListener(checkChange);
        lName.addTextChangedListener(checkChange);
        year.addTextChangedListener(checkChange);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile:
                if (clickCount%2==0) {
                    fName.setEnabled(true);
                    lName.setEnabled(true);
                    year.setEnabled(true);
                    // phoneNumber.setEnabled(true);
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_check_24px))
                            .setTitle("Save changes");
                    clickCount = clickCount + 1;
                }
                else {
                    fName.setEnabled(false);
                    lName.setEnabled(false);
                    year.setEnabled(false);
                    // phoneNumber.setEnabled(false);
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_edit_24px))
                            .setTitle("Edit profile");
                    clickCount = clickCount + 1;
                    //TODO: save profile changes and exit to home
                }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
