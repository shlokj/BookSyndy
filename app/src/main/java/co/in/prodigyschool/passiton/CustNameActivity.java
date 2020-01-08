package co.in.prodigyschool.passiton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import co.in.prodigyschool.passiton.Data.User;

public class CustNameActivity extends AppCompatActivity {

    boolean isParent, isValidUsername, isAvailableUsername=true;
    EditText firstNameField, lastNameField, userIdField;
    String firstName, lastName, username;

    private static String TAG = "CUSTNAMEACTIVITY";
    private FirebaseFirestore mFireStore;
    private List<String> userNamesList;

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
        userNamesList = new ArrayList<>();
        initFireStore();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName = firstNameField.getText().toString();
                lastName = lastNameField.getText().toString();
                username = userIdField.getText().toString();
                isValidUsername = false;
                isValidUsername = (username != null) && username.matches("[A-Za-z0-9_]+");
                isAvailableUsername = checkUserName(username);
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

    private boolean checkUserName(String username) {
        boolean flag = true;
        if(username != null){
           for(String userId:userNamesList){
               if(userId.equalsIgnoreCase(username)){
                   flag = false;
               }
           }
        }
        return flag;
    }

    private void initFireStore() {
        mFireStore = FirebaseFirestore.getInstance();
        mFireStore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                     if(e != null){
                         Log.e(TAG, "onEvent: user names feth error",e );
                     }
                     if(!queryDocumentSnapshots.isEmpty()){
                         for (User user:queryDocumentSnapshots.toObjects(User.class)){
                             userNamesList.add(user.getUserId());
                         }

                     }

            }
        });
    }
}
