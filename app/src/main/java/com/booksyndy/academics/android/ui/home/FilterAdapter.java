package com.booksyndy.academics.android.ui.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FilterAdapter extends FragmentPagerAdapter {

    private int numOfTabs;
    private boolean onlyCollege;

    FilterAdapter(@NonNull FragmentManager fm, int numOfTabs,boolean onlyCollege) {
        super(fm, numOfTabs);
        this.numOfTabs = numOfTabs;
        this.onlyCollege = onlyCollege;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                if(onlyCollege) {
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
