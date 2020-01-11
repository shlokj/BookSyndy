package co.in.prodigyschool.passiton.ui.bookRequests;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import co.in.prodigyschool.passiton.R;
import co.in.prodigyschool.passiton.RequestBookActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingRequestFragment extends Fragment implements View.OnClickListener {

    public static String TAG = "PENDINGREQUEST";

    private FloatingActionButton fab;


    public PendingRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_pending_request, container, false);
        fab = rootView.findViewById(R.id.fab_request);
        fab.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.fab_request){
            Intent newBookRequest = new Intent(getActivity(), RequestBookActivity.class);
            startActivity(newBookRequest);
        }

    }
}
