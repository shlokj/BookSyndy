package com.booksyndy.academics.android.ui.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FilterAdapter extends FragmentPagerAdapter {

    private int numOfTabs;
    private boolean onlyCollege;
    private boolean generalMode;

    FilterAdapter(@NonNull FragmentManager fm, int numOfTabs,boolean onlyCollege) {
        super(fm, numOfTabs);
        this.numOfTabs = numOfTabs;
        this.onlyCollege = onlyCollege;
        this.generalMode = false;
    }

    FilterAdapter(@NonNull FragmentManager fm,boolean generalMode, int numOfTabs) {
        super(fm, numOfTabs);
        this.numOfTabs = numOfTabs;
        this.generalMode = generalMode;
        this.onlyCollege = false;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                if(generalMode){
                    return new FilterGeneralFragment();
                }
                else if(onlyCollege) {
                    return new FilterCollegeFragment();
                }
                return new FilterSchoolFragment();
            case 1:
                return new FilterCollegeFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
