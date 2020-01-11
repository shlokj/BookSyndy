package co.in.prodigyschool.passiton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import co.in.prodigyschool.passiton.Data.User;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences userPref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        userPref = this.getSharedPreferences(getString(R.string.UserPref),0);
        doesSessionExist();

        Window window = MainActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.white));
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!checkConnection(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_LONG).show();
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
                                            Intent homeActivity = new Intent(MainActivity.this,WelcomeActivity.class); //changed to welcome activity
                                            editor = userPref.edit();
                                            editor.putString(getString(R.string.p_userphone),user.getPhone());
                                            editor.putString(getString(R.string.p_userid),user.getUserId());
                                            editor.putString(getString(R.string.p_firstname),user.getFirstName());
                                            editor.putString(getString(R.string.p_lastname),user.getLastName());
                                            editor.putString(getString(R.string.p_imageurl),user.getImageUrl());
                                            editor.putInt(getString(R.string.p_grade),user.getGradeNumber());
                                            editor.putInt(getString(R.string.p_board),user.getBoardNumber());
                                            editor.putBoolean(getString(R.string.p_competitive),user.isCompetitiveExam());
                                            editor.apply();
//                                            Intent homeActivity = new Intent(MainActivity.this,HomeActivity.class);
                                            homeActivity.putExtra("username",user.getFirstName() + " " + user.getLastName());
                                            homeActivity.putExtra("userphone",user.getPhone());
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
                                    Toast.makeText(getApplicationContext(), "error getting user Data", Toast.LENGTH_LONG).show();
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
