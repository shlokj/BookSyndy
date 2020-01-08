package co.in.prodigyschool.passiton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class FilterActivity extends AppCompatActivity {

    CheckBox filterGrade5orBelow, filterGrade6to8, filterGrade9, filterGrade10, filterGrade11, filterGrade12;
    CheckBox filterBoardCbse, filterBoardIcse, filterBoardIb, filterBoardIgcse, filterBoardState, filterBoardOther, filterBoardCompetitiveExams;
    CheckBox freeOnly, filterTextbook, filterNotes;

    //TODO: set default filters
    /*How default filters are to be set:
    * For grade - check two boxes: the grade registered with AND the grade above it. For example, check 10 and 11 if the user is regd with 10
    * If the user has regd with 12, check only 12 here, and we'll add an option to view college book filters. Else college filters are all unchecked.
    * For board - check only the board the user has chosen during registration, and check competitive exams only if the user has checked it during registration
    * i.e., upto two board checks by default
    * For free/not - leave unchecked by default. Do not save prefs.
    * For textbook/notes - textbook by default. minimum one and maximum one selection. Logic will be added
    * For sort - relevance by default; don't save prefs
    * */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_school);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Filters");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        filterTextbook = findViewById(R.id.filterTextbook);
        filterNotes = findViewById(R.id.filterNotes);

        //TODO: get user details and auto-check filters

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
        filterTextbook.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
        filterNotes.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_light));
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                Toast.makeText(getApplicationContext(),"Saved filters",Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(getApplicationContext(),"Saved filters",Toast.LENGTH_SHORT).show();
    }
}
