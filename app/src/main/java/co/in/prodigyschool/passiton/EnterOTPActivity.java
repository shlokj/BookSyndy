package co.in.prodigyschool.passiton;

import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EnterOTPActivity extends AppCompatActivity {
    String userPhoneNumber;
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
                //temporary code just for testing
                Intent startposact = new Intent(EnterOTPActivity.this, ParOrStudActivity.class);
                startActivity(startposact);
            }
        });
    }
}
