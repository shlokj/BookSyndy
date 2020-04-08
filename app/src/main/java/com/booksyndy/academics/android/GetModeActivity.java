package com.booksyndy.academics.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class GetModeActivity extends AppCompatActivity {

    private boolean preferGeneral, temp1, phoneNumberPublic;
    private String firstName, lastName, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_mode);

        final RadioGroup genOrAcad = findViewById(R.id.modesRadioGroup);

        getSupportActionBar().setTitle("Sign up");

        firstName = getIntent().getStringExtra("FIRST_NAME");
        lastName = getIntent().getStringExtra("LAST_NAME");
        username = getIntent().getStringExtra("USERNAME");
        phoneNumberPublic = getIntent().getBooleanExtra("PUBLIC_PHONE",true);

        findViewById(R.id.fab2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int goa = genOrAcad.getCheckedRadioButtonId();
                if (goa==-1) {
                    temp1 = false;
                    showSnackbar("Please select an option");
                }
                else if (goa==R.id.genOption) {
                    preferGeneral = true;
                    temp1 = true;
                }
                else if (goa==R.id.acadsOption){
                    preferGeneral = false;
                    temp1 = true;
                }

                if (temp1) {
                    if (preferGeneral) {
                        Intent finishReg = new Intent(GetModeActivity.this,GetJoinPurposeActivity.class);
                        finishReg.putExtra("FIRST_NAME", firstName);
                        finishReg.putExtra("LAST_NAME", lastName);
                        finishReg.putExtra("USERNAME", username);
                        finishReg.putExtra("USER_TYPE", 0);
                        finishReg.putExtra("PREF_GEN", true);
                        finishReg.putExtra("PUBLIC_PHONE",phoneNumberPublic);
                        finishReg.putExtra("GRADE_NUMBER",0);
                        finishReg.putExtra("BOARD_NUMBER",0);
                        finishReg.putExtra("YEAR_NUMBER",0);
                        finishReg.putExtra("USER_TYPE",0);
                        startActivity(finishReg);
                    }

                    else {
                        Intent getParOrStud = new Intent(GetModeActivity.this, ParOrStudActivity.class);
                        getParOrStud.putExtra("FIRST_NAME", firstName);
                        getParOrStud.putExtra("LAST_NAME", lastName);
                        getParOrStud.putExtra("USERNAME", username);
//                        getParOrStud.putExtra("PREF_GEN", preferGeneral);
                        getParOrStud.putExtra("PUBLIC_PHONE",phoneNumberPublic);
                        startActivity(getParOrStud);
                    }
                }
            }
        });
    }

    private void showSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT)
                .setAction("OKAY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }
}
