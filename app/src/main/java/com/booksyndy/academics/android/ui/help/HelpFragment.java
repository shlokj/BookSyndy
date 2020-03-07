package com.booksyndy.academics.android.ui.help;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.booksyndy.academics.android.R;
import com.booksyndy.academics.android.SendFeedbackActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HelpFragment extends Fragment {

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> questionsList,answersList;
    private HashMap<String,String> hashMap;
    private int cc = 1;

    private Menu menu;
    private String[] questions;
    private String[] answers;

    private ShareViewModel shareViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel =
                ViewModelProviders.of(this).get(ShareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        expandableListView = root.findViewById(R.id.expandable_list_faq);
        initData();
        expandableListAdapter = new FaqAdapter(getContext(),questionsList,hashMap);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView arrow = view.findViewById(R.id.dropdownDummyButton);
                arrow.animate().rotation(cc*180).setDuration(100);
                cc = cc+1;
                // TODO: put this code in the right place
            }
        });
        setHasOptionsMenu(true);
        return root;
    }

    private void initData() {
        questionsList = new ArrayList<>();
        answersList = new ArrayList<>();
        hashMap = new HashMap<>();
        questions = getResources().getStringArray(R.array.questions);
        answers = getResources().getStringArray(R.array.answers);
        questionsList = Arrays.asList(questions);
        answersList = Arrays.asList(answers);
        for(int i=0;i<questionsList.size();i++){
            hashMap.put(questionsList.get(i),"\n"+answersList.get(i)+"\n");
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_helpandfaq,menu);
//        this.menu = menu;
        final MenuItem sfbItem = menu.findItem(R.id.sendFeedback);
        sfbItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                startActivity(new Intent(getActivity(), SendFeedbackActivity.class));
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.sendFeedback) {
            startActivity(new Intent(getActivity(), SendFeedbackActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}