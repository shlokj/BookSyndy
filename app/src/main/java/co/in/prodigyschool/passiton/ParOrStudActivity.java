package co.in.prodigyschool.passiton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

public class ParOrStudActivity extends AppCompatActivity {

    boolean isParent = false;
    int studOrPar;
    RadioGroup studOrParButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_par_or_stud);

        studOrParButtons = (RadioGroup) findViewById(R.id.studentOrParentRadioGroup);
        FloatingActionButton next = findViewById(R.id.fab3);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    Intent getName = new Intent(ParOrStudActivity.this, CustNameActivity.class);
                    getName.putExtra("IS_PARENT", isParent);
                    startActivity(getName);
                }
                else {
                    isParent=true;
                    Intent getName = new Intent(ParOrStudActivity.this, CustNameActivity.class);
                    getName.putExtra("IS_PARENT", isParent);
                    startActivity(getName);
                }
            }
        });

    }
}
