package co.in.prodigyschool.passiton.ui.home;

import androidx.fragment.app.DialogFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import co.in.prodigyschool.passiton.R;
import co.in.prodigyschool.passiton.util.Filters;

public class FilterDialogFragment extends DialogFragment implements View.OnClickListener {


    public static final String TAG = "FILTER_DIALOG";

    private View mRootView;
    private CheckBox freeOnly;
    private OnFilterSelectionListener mOnFilterSelectedListener;


    public interface OnFilterSelectionListener
    {
        public void onFilter(Filters filters);
    }
    public void onAttachToParentFragment(Fragment fragment)
    {
        try
        {
            mOnFilterSelectedListener = (OnFilterSelectionListener)fragment;

        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(
                    fragment.toString() + " must implement OnFilterSelectionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        onAttachToParentFragment(getParentFragment());

        // ...
    }


    public static FilterDialogFragment newInstance() {
        return new FilterDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView =  inflater.inflate(R.layout.filter_dialog_fragment, container, false);
        freeOnly = mRootView.findViewById(R.id.freeOnlyCB);
        mRootView.findViewById(R.id.button_search).setOnClickListener(this);
        mRootView.findViewById(R.id.button_cancel).setOnClickListener(this);
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_search:
                onFilterApplied();
                break;
            case R.id.button_cancel:
                onCancelClicked();
                break;
        }
    }

    public void onFilterApplied() {

        Log.d(TAG, "onFilterApplied: search clicked:"+getFilters().getPrice());
        mOnFilterSelectedListener.onFilter(getFilters());
        dismiss();
    }

    public void onCancelClicked() {
        dismiss();
    }

    public Filters getFilters() {
        Filters filters = new Filters();

        if (mRootView != null) {

            filters.setPrice(getSelectedPrice());

        }

        return filters;
    }

    private int getSelectedPrice() {
        if (freeOnly.isChecked()) {
            return 1;
        } else {
            return -1;
        }
    }

}
