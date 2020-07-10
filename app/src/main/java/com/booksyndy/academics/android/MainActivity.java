package com.booksyndy.academics.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.booksyndy.academics.android.Data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;



public class MainActivity extends AppCompatActivity {

    private static String TAG = "MAINACTIVITY";
    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;
    private String dynamicBookId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        try {
            if (!FirebaseApp.getApps(this).isEmpty())
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            Log.d("MainActivity", "onCreate: persistance error");
        }
        userPref = this.getSharedPreferences(getString(R.string.UserPref), 0);

        receiveDynamicLink();

        Window window = MainActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.white));
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!checkConnection(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    private void doesSessionExist() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            try {
                final String userId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .document(userId).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot snapshot) {
                                if (snapshot != null && snapshot.exists()) {
                                    //returning user
                                    User user = snapshot.toObject(User.class);
                                    String userPhone = userPref.getString(getString(R.string.p_userphone), null);
                                    Intent homeActivity;
                                    if (userPhone == null) {
                                        homeActivity = new Intent(MainActivity.this, WelcomeActivity.class);
                                    }
                                    else if ((user.getUserType()==0 || user.getGradeNumber()==0 || user.getBoardNumber()==0) && !user.isPreferGeneral()) {
                                        homeActivity = new Intent(MainActivity.this,ParOrStudActivity.class);
                                        homeActivity.putExtra("MODE_SWITCHED",true);
                                    }

                                    else {
                                        homeActivity = new Intent(MainActivity.this, HomeActivity.class); //changed to welcome activity
                                        homeActivity.putExtra("FIRST_NAME", user.getFirstName());
                                        homeActivity.putExtra("LAST_NAME", user.getLastName());
                                        homeActivity.putExtra("USERNAME", user.getUserId());
                                        homeActivity.putExtra("PUBLIC_PHONE",user.isPhoneNumberPublic());
                                    }

                                    editor = userPref.edit();
                                    editor.putString(getString(R.string.p_userphone), user.getPhone());
                                    editor.putString(getString(R.string.p_userid), user.getUserId());
                                    editor.putString(getString(R.string.p_firstname), user.getFirstName());
                                    editor.putString(getString(R.string.p_lastname), user.getLastName());
                                    editor.putString(getString(R.string.p_username), user.getUserId());
                                    editor.putString(getString(R.string.p_imageurl), user.getImageUrl());
                                    editor.putInt(getString(R.string.p_grade), user.getGradeNumber());
                                    editor.putInt(getString(R.string.p_board), user.getBoardNumber());
                                    editor.putInt(getString(R.string.p_year), user.getYear());
                                    editor.putBoolean(getString(R.string.p_competitive), user.isCompetitiveExam());
                                    editor.putInt(getString(R.string.p_usertype),user.getUserType());
                                    editor.putBoolean(getString(R.string.phoneNumberPublic),user.isPhoneNumberPublic());
                                    editor.putBoolean(getString(R.string.preferGeneral),user.isPreferGeneral());
                                    editor.putInt(getString(R.string.p_uservolstatus),user.getVolunteerStatus());
                                    editor.apply();

                                    homeActivity.putExtra("username", user.getFirstName() + " " + user.getLastName());
                                    homeActivity.putExtra("userphone", user.getPhone());
                                    homeActivity.putExtra("showGPS", true);
                                    homeActivity.putExtra("dynamicBookId", dynamicBookId);
                                    startActivity(homeActivity);
                                    MainActivity.this.finish();

                                } else {
                                    //new user
                                    Intent startposact = new Intent(MainActivity.this, CustNameActivity.class);
                                    startposact.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(startposact);
                                    MainActivity.this.finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error getting user data", Toast.LENGTH_LONG).show();
                        // Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {

            Intent startposact = new Intent(MainActivity.this, SignIn2Activity.class);
            startposact.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(startposact);
            finish();
        }
    }


    private void receiveDynamicLink() {
        try {
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                            // Get deep link from result (may be null if no link is found)
                            Uri deepLink = null;
                            String deepLinkStr = null;
                            if (pendingDynamicLinkData != null) {
                                deepLink = pendingDynamicLinkData.getLink();
                                if (deepLink != null)
                                    deepLinkStr = deepLink.toString();
                                Log.d(TAG, "onSuccess: deepLink " + deepLink);
                                dynamicBookId = deepLinkStr.substring(deepLinkStr.lastIndexOf('/'));
                                //goToActivity(String documentId);

                            }

                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "getDynamicLink:onFailure", e);

                        }
                    }).addOnCompleteListener(new OnCompleteListener<PendingDynamicLinkData>() {
                @Override
                public void onComplete(@NonNull Task<PendingDynamicLinkData> task) {
                    doesSessionExist();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
