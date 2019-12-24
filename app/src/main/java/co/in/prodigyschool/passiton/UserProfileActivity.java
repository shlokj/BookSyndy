package co.in.prodigyschool.passiton;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView profilePic;
    private EditText fName, lName, year, phoneNo, uName;
    private Spinner gradeSpinner, boardSpinner, degreeSpinner;
    private FloatingActionButton saveChanges;
    private TextWatcher checkChange;
    private Menu menu;
    private int clickCount;
    private CheckBox compExams;
    private boolean detailsChanged = false;

    private String firstName, lastName, phoneNumber;
    private FirebaseFirestore mFirestore;



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
        uName = findViewById(R.id.usernameField);
        year = findViewById(R.id.profileYearField);
        phoneNo = findViewById(R.id.profilePhoneNumberField);

        gradeSpinner = findViewById(R.id.gradeSpinner);
        boardSpinner = findViewById(R.id.boardSpinner);
        degreeSpinner = findViewById(R.id.degreeSpinner);

        saveChanges = findViewById(R.id.fab_save);
        saveChanges.hide();

        compExams = findViewById(R.id.profileCompetitiveExams);

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
        uName.addTextChangedListener(checkChange);
        year.addTextChangedListener(checkChange);


//        mFirestore = FirebaseFirestore.getInstance();

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
                    uName.setEnabled(true);
                    year.setEnabled(true);
                    compExams.setEnabled(true);
                    // phoneNumber.setEnabled(true);
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_check_24px))
                            .setTitle("Save changes");
                    clickCount = clickCount + 1;
                }
                else {
                    fName.setEnabled(false);
                    lName.setEnabled(false);
                    uName.setEnabled(false);
                    year.setEnabled(false);
                    compExams.setEnabled(false);
                    // phoneNumber.setEnabled(false);
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_edit_24px))
                            .setTitle("Edit profile");
                    clickCount = clickCount + 1;

                    //TODO: save profile changes to firebase

                    Intent homeIntent = new Intent(UserProfileActivity.this,HomeActivity.class);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    homeIntent.putExtra("SNACKBAR_MSG", "Your profile has been saved");
                    startActivity(homeIntent);
                }
                break;
            case android.R.id.home:
                if (detailsChanged) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
                    builder.setTitle("Save your changes?");
                    builder.setMessage("Would you like to save the changes you made to your profile?");
                    builder.setPositiveButton("Save and exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent homeIntent = new Intent(UserProfileActivity.this, HomeActivity.class);
                            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            homeIntent.putExtra("SNACKBAR_MSG", "Your profile has been saved");
                            startActivity(homeIntent);
                        }
                    });
                    builder.setNegativeButton("Exit without saving", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishActivity();
                        }
                    });
                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
                else {
                    finishActivity();
                }
                break;

        }
        return true;
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
    public void finishActivity () {
        this.finish();
    }


}
