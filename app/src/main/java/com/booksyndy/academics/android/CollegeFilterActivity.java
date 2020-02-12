package com.booksyndy.academics.android;

import android.os.Bundle;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class CollegeFilterActivity extends AppCompatActivity {

    CheckBox filterBtech, filterBsc, filterBcom, filterBa, filterBba, filterBca, filterBed, filterLlb, filterMbbs, filterOtherDegree;
    CheckBox freeOnly, filterTextbook, filterNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_filter);

        getSupportActionBar().setTitle("Filters");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        filterBtech = findViewById(R.id.filterBtech);
        filterBsc = findViewById(R.id.filterBsc);
        filterBcom = findViewById(R.id.filterBcom);
        filterBa = findViewById(R.id.filterBa);
        filterBba = findViewById(R.id.filterBba);
        filterBca = findViewById(R.id.filterBca);
        filterBed = findViewById(R.id.filterBed);
        filterLlb = findViewById(R.id.filterLlb);
        filterMbbs = findViewById(R.id.filterMbbs);
        filterOtherDegree = findViewById(R.id.filterOtherDegree);

        freeOnly = findViewById(R.id.freeOnlyCB);

        filterTextbook = findViewById(R.id.filterTextbook);
        filterNotes = findViewById(R.id.filterNotes);

        setFilterOptionsFontToRobotoLight();
    }

    public void setFilterOptionsFontToRobotoLight() {
        filterBtech.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterBsc.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterBcom.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterBa.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterBba.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterBca.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterBed.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterLlb.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterMbbs.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterOtherDegree.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        freeOnly.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterTextbook.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterNotes.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
    }

}
