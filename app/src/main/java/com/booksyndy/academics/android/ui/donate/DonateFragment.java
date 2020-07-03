package com.booksyndy.academics.android.ui.donate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.booksyndy.academics.android.CreateBundleListingActivity;
import com.booksyndy.academics.android.GetDonorAddressActivity;
import com.booksyndy.academics.android.MyDonationsActivity;
import com.booksyndy.academics.android.R;
import com.booksyndy.academics.android.ui.shareApp.ShareAppViewModel;

public class DonateFragment extends Fragment {

    private DonateViewModel donateViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        ((AppCompatActivity) getActivity()).getSupportActionBar().hide(); TODO: uncomment this

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        donateViewModel =
                ViewModelProviders.of(this).get(DonateViewModel.class);

        View root = inflater.inflate(R.layout.screen_loading, container, false);

        final SharedPreferences userPref = getActivity().getSharedPreferences(getString(R.string.UserPref), 0);
        boolean startedDonate = userPref.getBoolean(getString(R.string.p_useddon), false);

        Intent goToDonate = new Intent(getActivity(), MyDonationsActivity.class);

        if (startedDonate) {
            startActivity(goToDonate);
        }

        else {
            root = inflater.inflate(R.layout.fragment_donate_initial, container, false);

            root.findViewById(R.id.donateGetStarted).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putBoolean(getString(R.string.p_useddon),true);
                    editor.apply();
                    startActivity(new Intent(getActivity(), CreateBundleListingActivity.class));
                }
            });
        }





        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==12) {
            startActivity(getActivity().getIntent());
            getActivity().finish();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}