package com.booksyndy.academics.android;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private Button signInButton;
    private LinearLayout logo_view;
    private static final int RC_SIGN_IN = 123;
    private static String TAG = "SIGNIN2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in2);
        welcomeTV = findViewById(R.id.welcomeTV);

        welcomeTV.setMovementMethod(new ScrollingMovementMethod());
        logo_view = findViewById(R.id.view_booksyndy);
        signInButton = findViewById(R.id.signInButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // temporary
                if (!checkConnection(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                showView();
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
                hideView();
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


    private void showView() {
        signInButton.setVisibility(View.GONE);
        welcomeTV.setVisibility(View.GONE);
        logo_view.setVisibility(View.VISIBLE);
        getSupportActionBar().hide();

    }

    private void hideView() {
        signInButton.setVisibility(View.VISIBLE);
        welcomeTV.setVisibility(View.VISIBLE);
        logo_view.setVisibility(View.GONE);
        getSupportActionBar().show();
    }

}
