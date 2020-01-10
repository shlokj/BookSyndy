package co.in.prodigyschool.passiton.ui.bookRequests;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import co.in.prodigyschool.passiton.R;
import co.in.prodigyschool.passiton.SignInActivity;
import co.in.prodigyschool.passiton.ui.bookRequests.PageAdapter;

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