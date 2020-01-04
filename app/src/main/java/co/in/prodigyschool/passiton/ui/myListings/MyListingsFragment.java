package co.in.prodigyschool.passiton.ui.myListings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import co.in.prodigyschool.passiton.R;

public class MyListingsFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private static String TAG = "MY_LISTINGS";
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabItem tab_available,tab_sold;
    private PageAdapter mPageAdapter;

    private GalleryViewModel galleryViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mylistings_tabs, container, false);
        mViewPager = root.findViewById(R.id.listings_viewpager);
        mTabLayout = root.findViewById(R.id.listings_tab);
        tab_available = root.findViewById(R.id.tab_available);
        tab_sold = root.findViewById(R.id.tab_sold);

        mPageAdapter = new PageAdapter(getChildFragmentManager(),mTabLayout.getTabCount());
        mViewPager.setAdapter(mPageAdapter);
        mTabLayout.addOnTabSelectedListener(this);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        return root;
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