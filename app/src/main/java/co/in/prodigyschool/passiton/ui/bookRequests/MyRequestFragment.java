package co.in.prodigyschool.passiton.ui.bookRequests;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.in.prodigyschool.passiton.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyRequestFragment extends Fragment {


    public MyRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_request, container, false);
    }

}
