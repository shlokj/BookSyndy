package com.booksyndy.academics.android;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;
import java.util.List;

public class SignIn2Activity extends AppCompatActivity {

    private TextView welcomeTV;
    private static final int RC_SIGN_IN = 123;
    private static String TAG = "SIGNIN2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in2);

        welcomeTV = findViewById(R.id.welcomeTV);

        welcomeTV.setMovementMethod(new ScrollingMovementMethod());

        Button signInButton = findViewById(R.id.signInButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // temporary
                createSignInIntent();
            }
        });
    }


    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().setDefaultCountryIso("IN").build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                //check for user details
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null && user.getPhoneNumber() != null)
                    saveToken(user.getPhoneNumber());
                Intent startmainact = new Intent(SignIn2Activity.this, MainActivity.class);
                startmainact.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startmainact);
                finish();

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...

                Log.d(TAG, "onActivityResult: Error Signing in");
//                Toast.makeText(getApplicationContext(), "Error Signing In", Toast.LENGTH_LONG).show();

            }
        }
    }
    // [END auth_fui_result]


    private void saveToken(final String userId) {
        try {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            FirebaseFirestore.getInstance().collection("users").document(userId).update("token", token);
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "saveToken: ", e);
        }

    }
    //[ END save TOKEN]

}
