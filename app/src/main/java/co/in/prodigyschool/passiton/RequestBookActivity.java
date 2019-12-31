package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class RequestBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_book);
        getSupportActionBar().setTitle("Request a book");
    }
}
