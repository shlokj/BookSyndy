package co.in.prodigyschool.passiton.ui.bookRequests;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import co.in.prodigyschool.passiton.ui.home.FilterCollegeFragment;
import co.in.prodigyschool.passiton.ui.home.FilterSchoolFragment;

public class FilterAdapterRequest extends FragmentPagerAdapter {

    private int numOfTabs;

    public FilterAdapterRequest(@NonNull FragmentManager fm, int numOfTabs) {
        super(fm, numOfTabs);
        this.numOfTabs = numOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new FilterSchoolRequest();
            case 1:
                return new FilterCollegeRequest();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
