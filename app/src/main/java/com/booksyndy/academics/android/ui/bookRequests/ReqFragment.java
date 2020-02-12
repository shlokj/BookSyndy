package com.booksyndy.academics.android.ui.bookRequests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.booksyndy.academics.android.R;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class ReqFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private static String TAG = "REQFRAGMENT";

    private ReqViewModel sendViewModel;
    private TextView textView;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabItem tab_available,tab_sold;
    private PageAdapter mPageAdapter;




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(ReqViewModel.class);
        View root = inflater.inflate(R.layout.fragment_book_requests, container, false);

        mViewPager = root.findViewById(R.id.requests_viewpager);
        mTabLayout = root.findViewById(R.id.requests_tab);
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