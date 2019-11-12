package co.in.prodigyschool.passiton;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SignInActivity extends AppCompatActivity {
    String userPhoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        final EditText phnofield = (EditText) findViewById(R.id.phoneNumberField);
        FloatingActionButton next = findViewById(R.id.fab);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPhoneNumber = phnofield.getText().toString();
                Intent takeOTP = new Intent(SignInActivity.this, EnterOTPActivity.class);
                takeOTP.putExtra("USER_MOB",userPhoneNumber);
                startActivity(takeOTP);
            }
        });
    }
}
