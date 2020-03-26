package com.booksyndy.academics.android.ui.shareApp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.booksyndy.academics.android.R;

public class ShareAppFragment extends Fragment {

    private ShareAppViewModel shareAppViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareAppViewModel =
                ViewModelProviders.of(this).get(ShareAppViewModel.class);
        View root = inflater.inflate(R.layout.fragment_shareapp, container, false);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://booksyndy.com/download");
        shareIntent.setType("text/plain");
        startActivityForResult(shareIntent,12);


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