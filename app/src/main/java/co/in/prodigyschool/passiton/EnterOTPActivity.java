package co.in.prodigyschool.passiton;

import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EnterOTPActivity extends AppCompatActivity {
    String userPhoneNumber;
    boolean otpCorrect=true;
    boolean isRegisteredUser=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        final EditText otpField = (EditText) findViewById(R.id.editTextOtp);
        userPhoneNumber = getIntent().getStringExtra("USER_MOB");

        FloatingActionButton next = findViewById(R.id.fab2);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if otp is correct here
                // Jitendra - I've made your work easier here. Just check if the OTP is correct and assign that to otpCorrect
                if (otpField.getText().toString().length()==4) {
                    if (otpCorrect) {
                        if (!isRegisteredUser) {
                            Intent startposact = new Intent(EnterOTPActivity.this, ParOrStudActivity.class);
                            startActivity(startposact);
                        }
                        else {
                            Intent startmainact = new Intent(EnterOTPActivity.this, MainActivity.class);
                            startActivity(startmainact);
                        }
                    }
                    else {
                        View parentLayout = findViewById(android.R.id.content);
                        Snackbar.make(parentLayout, "Incorrect verification code", Snackbar.LENGTH_SHORT)
                                .setAction("RE-ENTER", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        otpField.setText("");
                                    }
                                })
                                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                .show();
                    }
                }
                else {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "A valid verification code has 4 digits", Snackbar.LENGTH_SHORT)
                            .setAction("OKAY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                }
            }
        });
    }
}
