package com.booksyndy.academics.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmVolunteeringSignUpActivity extends AppCompatActivity {

    private TextView volName, volPhone, volAddress;
    private String name, phone, hnbn, street, pincode;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_volunteering_sign_up);

        getSupportActionBar().setTitle("Confirm your details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        volName = findViewById(R.id.volNameFinal);
        volPhone = findViewById(R.id.volPhoneFinal);
        volAddress = findViewById(R.id.volAddressFinal);
        confirmButton = findViewById(R.id.confirmVolReg);

        name = getIntent().getStringExtra("VOL_NAME");
        phone = getIntent().getStringExtra("VOL_PHONE");
        hnbn = getIntent().getStringExtra("VOL_HNBN");
        street = getIntent().getStringExtra("VOL_STREET");
        pincode = getIntent().getStringExtra("VOL_PINCODE");

        volName.setText(name);
        volPhone.setText(phone.substring(0,3) + " " + phone.substring(3));
        volAddress.setText(hnbn + ", " + street + ", " + pincode);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: firebase
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }
}
