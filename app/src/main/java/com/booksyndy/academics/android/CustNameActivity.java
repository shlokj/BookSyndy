package com.booksyndy.academics.android;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.booksyndy.academics.android.Data.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CustNameActivity extends AppCompatActivity {

    private boolean isParent, isValidUsername, isAvailableUsername=true, phoneNumberPublic;
    private EditText firstNameField, lastNameField, userIdField;
    private TextInputLayout fnf,lnf,uif;
    private String firstName, lastName, username;
    private int userType;
    private TextWatcher pUsername;

    private static String TAG = "CUSTNAMEACTIVITY";
    private FirebaseFirestore mFireStore;
    private List<String> userNamesList;
    private View parentLayout;
    private CheckBox pnpcb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_name);
        getSupportActionBar().setTitle("Sign up");

        parentLayout = findViewById(android.R.id.content);
        isParent = getIntent().getBooleanExtra("IS_PARENT",false);
        userType = getIntent().getIntExtra("USER_TYPE",1);

        userIdField = (EditText) findViewById(R.id.usernameField);
        firstNameField = (EditText) findViewById(R.id.firstName);
        lastNameField = (EditText) findViewById(R.id.lastName);
        uif = findViewById(R.id.usernameTIL);
        pnpcb = findViewById(R.id.publicPhoneCB);

        pnpcb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                phoneNumberPublic = isChecked;
            }
        });
        pUsername = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userIdField.setText(firstNameField.getText().toString().toLowerCase()+lastNameField.getText().toString().toLowerCase());
                userIdField.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        userIdField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userIdField.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    lastNameField.requestFocus();
                }
                return false;
            }
        });
        lastNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    userIdField.requestFocus();
                }
                return false;
            }
        });

        firstNameField.addTextChangedListener(pUsername);
        lastNameField.addTextChangedListener(pUsername);

        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab4);
        userNamesList = new ArrayList<>();
        initFireStore();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"Type: "+userType,Toast.LENGTH_SHORT).show();

                firstName = firstNameField.getText().toString().trim();
                lastName = lastNameField.getText().toString().trim();
                username = userIdField.getText().toString().trim().toLowerCase();
                phoneNumberPublic = pnpcb.isChecked();

                // todo: last char can't be a dot.
                isValidUsername = false;
                isValidUsername = (username != null) && username.matches("[A-Za-z0-9_.]+");
                isAvailableUsername = checkUserName(username);
                if (firstName.length()==0 || lastName.length()==0) {
                    showSnackbar("Please fill in both name fields");
                }

                else if (!(isValidUsername && isAvailableUsername)) {
//                    userIdField.getBackground().setColorFilter("#EE0000", PorterDuff.Mode.SRC_IN);
                    if (!isValidUsername) {
                        uif.setError("The username you entered is not valid.");
//                        showSnackbar("The username you entered is not valid.");
                    }
                    else if (!isAvailableUsername) {
                        uif.setError("This username is taken. Please try another.");
//                        showSnackbar("This username is taken. Please try another.");
                    }
                }

                else if (username.length()<4) {
                    uif.setError("This username is too short.");
//                    showSnackbar("This username is too short.");
                }
/*                else if(!passwordsMatch) {
                    showSnackbar("The entered passwords don't match.");
                }
                else if (password.length()==0) {
                    showSnackbar("Please enter a password");
                }
                else if (password.length()<6) {
                    showSnackbar("Your password must be at least 6 characters long.");
                }*/
                else {
                    if (userType==4) {

                        Intent completeSignUp = new Intent(CustNameActivity.this, GetJoinPurposeActivity.class);
                        completeSignUp.putExtra("IS_PARENT", isParent);
                        completeSignUp.putExtra("FIRST_NAME", firstName);
                        completeSignUp.putExtra("LAST_NAME", lastName);
                        completeSignUp.putExtra("USERNAME", username);
                        completeSignUp.putExtra("USER_TYPE", userType);
                        completeSignUp.putExtra("GRADE_NUMBER",4);
                        completeSignUp.putExtra("BOARD_NUMBER",1);
                        completeSignUp.putExtra("PUBLIC_PHONE",phoneNumberPublic);
                        startActivity(completeSignUp);
                    }
                    else {
                        Intent getGrade = new Intent(CustNameActivity.this, GetGradeActivity.class);
                        getGrade.putExtra("IS_PARENT", isParent);
                        getGrade.putExtra("FIRST_NAME", firstName);
                        getGrade.putExtra("LAST_NAME", lastName);
                        getGrade.putExtra("USERNAME", username);
                        getGrade.putExtra("USER_TYPE", userType);
                        getGrade.putExtra("PUBLIC_PHONE",phoneNumberPublic);
                        startActivity(getGrade);
                    }
                }
            }
        });
    }

    private boolean checkUserName(String username) {
        if(username != null){
            for(String userId:userNamesList){
                if(userId.equalsIgnoreCase(username)){
                    return false;
                }
            }
        }
        return true;
    }

    private void initFireStore() {
        mFireStore = FirebaseFirestore.getInstance();
        mFireStore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.e(TAG, "onEvent: usernames fetch error",e );
                }
                if(queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()){
                    for (User user:queryDocumentSnapshots.toObjects(User.class)){
                        userNamesList.add(user.getUserId());
                    }

                }

            }
        });
    }
    private void showSnackbar(String message) {
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT)
                .setAction("OKAY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }
}