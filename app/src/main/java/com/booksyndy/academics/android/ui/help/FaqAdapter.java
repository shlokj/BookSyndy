package com.booksyndy.academics.android.ui.help;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.booksyndy.academics.android.R;

import java.util.HashMap;
import java.util.List;

public class FaqAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listQuestions ;
    private HashMap<String,String> hashMap;

    public FaqAdapter(Context context, List<String> listQuestions, HashMap<String, String> hashMap) {
        this.context = context;
        this.listQuestions = listQuestions;
        this.hashMap = hashMap;
    }

    @Override
    public int getGroupCount() {
        return listQuestions.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(TextUtils.isEmpty(hashMap.get(listQuestions.get(groupPosition)))){
            return 0;
        }
        else{
            return 1;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listQuestions.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return hashMap.get(listQuestions.get(groupPosition));
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String question = (String)getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.faq_questions,null);
        }
        TextView questionView = convertView.findViewById(R.id.text_helpquestion);
        questionView.setTypeface(null, Typeface.NORMAL);
        questionView.setText(question);


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String answer = (String)getChild(groupPosition,childPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.faq_answer,null);
        }
        TextView questionView = (TextView)convertView.findViewById(R.id.text_helpanswer);
        questionView.setText(answer);


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
