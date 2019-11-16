package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button signOut = findViewById(R.id.btnSignOut);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            //new user or session expired
            Intent signInIntent = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(signInIntent);
            finish();
        }
        else{
            // user session exist so populate his details here
            populateUserDetails();
        }
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent signout = new Intent(MainActivity.this,SignInActivity.class);
                signout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signout);

            }
        });
    }

    private void populateUserDetails() {
        try{
            Toast.makeText(getApplicationContext(),"Login Successful"+FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(),Toast.LENGTH_LONG).show();
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
}
