package co.in.prodigyschool.passiton.ui.help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import co.in.prodigyschool.passiton.R;

public class HelpFragment extends Fragment {

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> questionsList,answersList;
    private HashMap<String,String> hashMap;

    /*
    todo: add or remove question here
     */
    private String[] questions = new String[]{"How Does BookSyndy works?","question 2","question 3"};
    private String[] answers = new String[]{"BookSyndy works like charm.","answer 2","answer 3"};


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
        return root;
    }

    private void initData() {
        questionsList = new ArrayList<>();
        answersList = new ArrayList<>();
        hashMap = new HashMap<>();

        questionsList = Arrays.asList(questions);
        answersList = Arrays.asList(answers);
        for(int i=0;i<questionsList.size();i++){
            hashMap.put(questionsList.get(i),answersList.get(i));
        }
    }
}