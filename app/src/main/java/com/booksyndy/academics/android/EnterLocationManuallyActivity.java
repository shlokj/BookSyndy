package com.booksyndy.academics.android;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EnterLocationManuallyActivity extends AppCompatActivity {
// This activi
    EditText locSearchField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_location_manually);
        locSearchField = (EditText) findViewById(R.id.searchLocationEditText);
        locSearchField.requestFocus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }
}
