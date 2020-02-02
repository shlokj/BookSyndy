package co.in.prodigyschool.passiton;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import co.in.prodigyschool.passiton.Data.User;


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
        doesSessionExist();

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
            return;
        }
    }

    private void doesSessionExist() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            try {
                final String userId = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        //.whereEqualTo("phone", userId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.getId().equalsIgnoreCase(userId)) {
                                            //user session exist
                                            User user = document.toObject(User.class);
                                            String userPhone = userPref.getString(getString(R.string.p_userphone), null);
                                            Intent homeActivity;
                                            if (userPhone == null || !userPhone.equalsIgnoreCase(user.getPhone())) {
                                                homeActivity = new Intent(MainActivity.this, WelcomeActivity.class);
                                            } else {
                                                homeActivity = new Intent(MainActivity.this, HomeActivity.class); //changed to welcome activity
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
                                            editor.apply();

//                                            Intent homeActivity = new Intent(MainActivity.this,HomeActivity.class);
                                            homeActivity.putExtra("username", user.getFirstName() + " " + user.getLastName());
                                            homeActivity.putExtra("userphone", user.getPhone());
                                            homeActivity.putExtra("showGPS", true);
                                            homeActivity.putExtra("dynamicBookId", dynamicBookId);
                                            if (getIntent().getExtras() != null) {
                                                homeActivity.putExtra("openChat", true);
                                            }
                                            startActivity(homeActivity);
                                            finish();
                                            return;
                                        }


                                        //Log.d(TAG, document.getId() + " => " + document.getData());
                                    }

                                    //new user or session expired
                                    Intent startposact = new Intent(MainActivity.this, ParOrStudActivity.class);
                                    startposact.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(startposact);
                                    finish();

                                } else {
                                    Toast.makeText(getApplicationContext(), "Error getting user data", Toast.LENGTH_LONG).show();
                                    // Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            //new user or session expired
            Intent signInIntent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(signInIntent);
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
