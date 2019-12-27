package co.in.prodigyschool.passiton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import co.in.prodigyschool.passiton.Data.User;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "USERPROFILEACTIVITY" ;

    private ImageView profilePic;
    private EditText fName, lName, year, phoneNo, uName;
    private Spinner gradeSpinner, boardSpinner, degreeSpinner;
    private FloatingActionButton saveChanges;
    private TextWatcher checkChange;
    private Menu menu;
    private int clickCount,gradeNumber,boardNumber;
    private CheckBox compExams;
    private boolean detailsChanged = false, newUNameOK=true;//TODO: add code to check whether new username is OK

    private String firstName, lastName, phoneNumber;
    private FirebaseFirestore mFirestore;
    private User curUser;

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


        fName.setEnabled(false);
        lName.setEnabled(false);
        uName.setEnabled(false);
        year.setEnabled(false);
        compExams.setEnabled(false);
        gradeSpinner.setEnabled(false);
        boardSpinner.setEnabled(false);
        degreeSpinner.setEnabled(false);

        populateUserDetails();
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
                    //uName.setEnabled(true);
                    year.setEnabled(true);
                    compExams.setEnabled(true);
                    gradeSpinner.setEnabled(true);
                    boardSpinner.setEnabled(true);
                    degreeSpinner.setEnabled(true);
                    // phoneNumber.setEnabled(true);
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_check_24px))
                            .setTitle("Save changes");
                    clickCount = clickCount + 1;
                }
                else {

                    //show progress
                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Updating...");
                    progressDialog.show();

                    fName.setEnabled(false);
                    lName.setEnabled(false);
                    uName.setEnabled(false);
                    year.setEnabled(false);
                    compExams.setEnabled(false);
                    gradeSpinner.setEnabled(false);
                    boardSpinner.setEnabled(false);
                    degreeSpinner.setEnabled(false);
                    // phoneNumber.setEnabled(false);
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_edit_24px))
                            .setTitle("Edit profile");
                    clickCount = clickCount + 1;

                    //TODO: save profile changes to firebase

                    if (!(fName.getText().toString().length()==0 || lName.getText().toString().length()==0 || uName.getText().toString().length()==0 /*|| year.getText().toString().length()==0*/)) {

                        User updatedUser = curUser;
                        updatedUser.setFirstName(fName.getText().toString());
                        updatedUser.setLastName(lName.getText().toString());
                        //updatedUser.setBoardNumber(boardSpinner.getSelectedItemPosition());
                        //updatedUser.setGradeNumber(gradeSpinner.getSelectedItemPosition());
                        updatedUser.setCompetitiveExam(compExams.isChecked());
                        DocumentReference userReference =  mFirestore.collection("users").document(phoneNumber);
                        userReference.set(updatedUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(progressDialog.isShowing())
                                    progressDialog.dismiss();
                                Intent homeIntent = new Intent(UserProfileActivity.this, HomeActivity.class);
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                homeIntent.putExtra("SNACKBAR_MSG", "Your profile has been saved");
                                startActivity(homeIntent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if(progressDialog.isShowing())
                                    progressDialog.dismiss();
                                Log.d(TAG, "onFailure: update user",e);
                                Toast.makeText(getApplicationContext(), "Update Failed", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    else {
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();

                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout, "Please fill in all fields", Snackbar.LENGTH_SHORT)
                                .setAction("OKAY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                .show();
                    }
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


    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) { // connected to the internet
                // connected to the mobile provider's data plan
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }

    private void populateUserDetails() {
        if (!checkConnection(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),"Internet Required",Toast.LENGTH_LONG).show();
            return;
        }
        mFirestore = FirebaseFirestore.getInstance();
        try{
            phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            DocumentReference userReference =  mFirestore.collection("users").document(phoneNumber);
            userReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    User user = snapshot.toObject(User.class);
                    curUser = user;
                    if(user != null) {
                        gradeNumber = user.getGradeNumber();
                        boardNumber = user.getBoardNumber();
                        ArrayAdapter<String> gradeAdapter = new ArrayAdapter<String>(UserProfileActivity.this,
                                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.grades));
                        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        gradeSpinner.setAdapter(gradeAdapter);
                        gradeSpinner.setSelection(gradeNumber-1);

                        if (gradeNumber>=1 && gradeNumber<=6) {

                            findViewById(R.id.boardLL).setVisibility(View.VISIBLE);
                            findViewById(R.id.collegeDegreeAndYearLL).setVisibility(View.GONE);

                            ArrayAdapter<String> boardAdapter = new ArrayAdapter<String>(UserProfileActivity.this,
                                    android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.boards));
                            boardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            boardSpinner.setAdapter(boardAdapter);
                            boardSpinner.setSelection(boardNumber-1);
                        }

                        else {

                            findViewById(R.id.boardLL).setVisibility(View.GONE);
                            findViewById(R.id.collegeDegreeAndYearLL).setVisibility(View.VISIBLE);

                            ArrayAdapter<String> degreeAdapter = new ArrayAdapter<String>(UserProfileActivity.this,
                                    android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.boards));
                            degreeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            degreeSpinner.setAdapter(degreeAdapter);
                            degreeSpinner.setSelection(boardNumber-7);
                        }
                        fName.setText(user.getFirstName());
                        lName.setText(user.getLastName());
                        uName.setText(user.getUserId());
                        phoneNo.setText(user.getPhone().substring(3));
                        compExams.setChecked(user.isCompetitiveExam());
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: ",e );
                }
            });

        }
        catch(Exception e){
            Log.e(TAG, "PopulateUserDetails method failed with  ",e);
        }
    }
}
