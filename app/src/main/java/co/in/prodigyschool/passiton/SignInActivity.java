package co.in.prodigyschool.passiton;

import android.content.DialogInterface;
import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
        FloatingActionButton next = findViewById(R.id.fab1);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userPhoneNumber = phnofield.getText().toString();
                if (userPhoneNumber.length()==10) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                        builder.setTitle("Number Verification");
                        builder.setMessage("We will be verifying the phone number you entered.\n\nPlease confirm that this is the correct number: "+userPhoneNumber);
                        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent takeOTP = new Intent(SignInActivity.this, EnterOTPActivity.class);
                                takeOTP.putExtra("USER_MOB", userPhoneNumber);
                                startActivity(takeOTP);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                        builder.show();
                }
                else {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Please enter a valid phone number", Snackbar.LENGTH_SHORT)
                            .setAction("OKAY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                }
            }
        });
    }
}
