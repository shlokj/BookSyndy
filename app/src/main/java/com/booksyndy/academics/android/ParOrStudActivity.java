package com.booksyndy.academics.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class ParOrStudActivity extends AppCompatActivity {

    boolean isParent = false;
    int studOrPar;
    RadioGroup studOrParButtons;
    private int type;

//    Student: 1
//    Parent: 2
//    Teacher: 3
//    Vendor: 4

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_par_or_stud);
        getSupportActionBar().setTitle("Sign up");

        studOrParButtons = (RadioGroup) findViewById(R.id.studentOrParentRadioGroup);
        FloatingActionButton next = findViewById(R.id.fab3);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getName = new Intent(ParOrStudActivity.this, CustNameActivity.class);
                studOrPar = studOrParButtons.getCheckedRadioButtonId();
                if (studOrPar==-1) {
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Please select an option", Snackbar.LENGTH_SHORT)
                            .setAction("OKAY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                }
                else if (studOrPar==R.id.studentoption) {
                    isParent=false;
                    type = 1;
                    getName.putExtra("IS_PARENT", isParent);
                }
                else if (studOrPar==R.id.parentoption){
                    isParent=true;
                    type = 2;
                    getName.putExtra("IS_PARENT", isParent);
                }
                else if (studOrPar==R.id.teacherOption){
                    type = 3;
                }
                else if (studOrPar==R.id.vendorOption){
                    type = 4;
                }
                getName.putExtra("USER_TYPE",type);
//                Toast.makeText(getApplicationContext(),"Type: "+type,Toast.LENGTH_SHORT).show();

                startActivity(getName);
            }
        });

    }
}
