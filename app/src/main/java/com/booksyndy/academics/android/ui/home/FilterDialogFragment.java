package com.booksyndy.academics.android.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.booksyndy.academics.android.R;
import com.google.android.material.tabs.TabLayout;

public class FilterDialogFragment extends DialogFragment implements TabLayout.OnTabSelectedListener{


    public static final String TAG = "FILTER_DIALOG";

    private View mRootView;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private FilterAdapter mFilterAdapter;
    private int curGrade;

    // TODO: save filters for the same session

    public FilterDialogFragment(){

    }

    public FilterDialogFragment(int grade){
        curGrade = grade;
    }

    public static FilterDialogFragment newInstance() {
        return new FilterDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView =  inflater.inflate(R.layout.filter_dialog_fragment, container, false);
        mViewPager = mRootView.findViewById(R.id.filters_viewpager);
        mTabLayout = mRootView.findViewById(R.id.filters_tab);
        if(curGrade <= 5){
            mFilterAdapter = new FilterAdapter(getChildFragmentManager(),1,false);
            mViewPager.setAdapter(mFilterAdapter);
            //mViewPager.setCurrentItem(0);
            mTabLayout.removeTabAt(1);
        }
        else if(curGrade == 6){
            mFilterAdapter = new FilterAdapter(getChildFragmentManager(),mTabLayout.getTabCount(),false);
            mViewPager.setAdapter(mFilterAdapter);
            mTabLayout.addOnTabSelectedListener(this);
            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        }
        else{
            mFilterAdapter = new FilterAdapter(getChildFragmentManager(),1,true);
            mViewPager.setAdapter(mFilterAdapter);
//            mViewPager.setCurrentItem(1);
            mTabLayout.removeTabAt(0);
        }

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
