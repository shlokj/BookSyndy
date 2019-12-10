package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.widget.CheckBox;

public class FilterActivity extends AppCompatActivity {

    CheckBox filterGrade5orBelow, filterGrade6to8, filterGrade9, filterGrade10, filterGrade11, filterGrade12;
    CheckBox filterBoardCbse, filterBoardIcse, filterBoardIb, filterBoardIgcse, filterBoardState, filterBoardOther, filterBoardCompetitiveExams;
    CheckBox freeOnly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_school);
        filterGrade5orBelow = findViewById(R.id.filterGrade5orBelow);
        filterGrade6to8 = findViewById(R.id.filterGrade6to8);
        filterGrade9 = findViewById(R.id.filterGrade9);
        filterGrade10 = findViewById(R.id.filterGrade10);
        filterGrade11 = findViewById(R.id.filterGrade11);
        filterGrade12 = findViewById(R.id.filterGrade12);

        filterBoardCbse = findViewById(R.id.filterBoardCbse);
        filterBoardIcse = findViewById(R.id.filterBoardIcse);
        filterBoardIb = findViewById(R.id.filterBoardIb);
        filterBoardIgcse = findViewById(R.id.filterBoardIgcse);
        filterBoardState = findViewById(R.id.filterBoardState);
        filterBoardOther = findViewById(R.id.filterBoardOther);
        filterBoardCompetitiveExams = findViewById(R.id.filterBoardCompetitiveExams);

        freeOnly = findViewById(R.id.freeOnlyCB);

        setFilterOptionsFontToRobotoLight();

    }

    public void setFilterOptionsFontToRobotoLight() {
        filterGrade5orBelow.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterGrade6to8.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterGrade9.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterGrade10.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterGrade11.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterGrade12.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterBoardCbse.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterBoardIcse.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterBoardIb.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterBoardIgcse.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterBoardState.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterBoardOther.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterBoardCompetitiveExams.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        freeOnly.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
    }
}
