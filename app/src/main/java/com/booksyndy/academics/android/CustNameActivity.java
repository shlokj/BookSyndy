package com.booksyndy.academics.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.booksyndy.academics.android.Data.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CustNameActivity extends AppCompatActivity {

    private boolean isParent, isValidUsername, isAvailableUsername=true, isValidPassword, passwordsMatch;
    private EditText firstNameField, lastNameField, userIdField, passwordField, confirmPasswordField;
    private String firstName, lastName, username, password, cPassword;

    private static String TAG = "CUSTNAMEACTIVITY";
    private FirebaseFirestore mFireStore;
    private List<String> userNamesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_name);
        getSupportActionBar().setTitle("Sign up");

        final View parentLayout = findViewById(android.R.id.content);
        isParent = getIntent().getBooleanExtra("IS_PARENT",false);
        userIdField = (EditText) findViewById(R.id.usernameField);
        firstNameField = (EditText) findViewById(R.id.firstName);
        lastNameField = (EditText) findViewById(R.id.lastName);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);
//        userIdField.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD|InputType.TYPE_TEXT_VARIATION_PERSON_NAME|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

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
        passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    passwordField.requestFocus();
                }
                return false;
            }
        });

        FloatingActionButton next = (FloatingActionButton) findViewById(R.id.fab4);
        userNamesList = new ArrayList<>();
        initFireStore();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName = firstNameField.getText().toString().trim();
                lastName = lastNameField.getText().toString().trim();
                username = userIdField.getText().toString().trim().toLowerCase();
                password = passwordField.getText().toString();
                cPassword = confirmPasswordField.getText().toString();
                passwordsMatch = password.equals(cPassword);
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
                        showSnackbar("The username you entered is not valid.");
                    }
                    else if (!isAvailableUsername) {
                        showSnackbar("This username is taken. Please try another.");
                    }

                }
                else if(!passwordsMatch) {
                    showSnackbar("The entered passwords don't match.");
                }
                else if (password.length()==0) {
                    showSnackbar("Please enter a password");
                }
                else if (password.length()<6) {
                    showSnackbar("Your password must be at least 6 characters long.");
                }
                else {
                    Intent getGrade = new Intent(CustNameActivity.this, GetGradeActivity.class);
                    getGrade.putExtra("IS_PARENT", isParent);
                    getGrade.putExtra("FIRST_NAME",firstName);
                    getGrade.putExtra("LAST_NAME",lastName);
                    getGrade.putExtra("USERNAME",username);
                    getGrade.putExtra("PASSWORD",password);
                    startActivity(getGrade);
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
    public void showSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
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