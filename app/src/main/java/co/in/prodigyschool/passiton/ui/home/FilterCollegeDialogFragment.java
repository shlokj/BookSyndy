package co.in.prodigyschool.passiton.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import co.in.prodigyschool.passiton.R;
import co.in.prodigyschool.passiton.util.Filters;


public class FilterCollegeDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "COLLEGE_FILTER_DIALOG";

    private View mRootView;
    private CheckBox filterGrade5orBelow, filterGrade6to8, filterGrade9, filterGrade10, filterGrade11, filterGrade12;
    private CheckBox filterBoardCbse, filterBoardIcse, filterBoardIb, filterBoardIgcse, filterBoardState, filterBoardOther, filterBoardCompetitiveExams;
    private CheckBox freeOnly, filterTextbook, filterNotes;
    private FilterCollegeDialogFragment.OnFilterSelectionListener mOnFilterSelectedListener_c;
    private TextView openSchoolFilters;
    FilterDialogFragment mFilterDialog;


    public interface OnFilterSelectionListener
    {
        public void onFilter(Filters filters);
    }

    public void onAttachToParentFragment(Fragment fragment)
    {
        mOnFilterSelectedListener_c = (OnFilterSelectionListener)fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        onAttachToParentFragment(getParentFragment());
    }


    public static FilterCollegeDialogFragment newInstance() {
        return new FilterCollegeDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView =  inflater.inflate(R.layout.filter_dialog_fragment, container, false);
        freeOnly = mRootView.findViewById(R.id.freeOnlyCB);
        mRootView.findViewById(R.id.button_apply_c).setOnClickListener(this);
        mRootView.findViewById(R.id.button_cancel_c).setOnClickListener(this);


        filterTextbook = mRootView.findViewById(R.id.filterTextbook_c);
        filterNotes = mRootView.findViewById(R.id.filterNotes_c);

        setFilterOptionsFontToRobotoLight();

        mFilterDialog = new FilterDialogFragment();
        openSchoolFilters = mRootView.findViewById(R.id.collegeFiltersButton);

        openSchoolFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFilterDialog.show(getChildFragmentManager(),FilterDialogFragment.TAG);
            }
        });

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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_apply_c:
                onFilterApplied();
                break;
            case R.id.button_cancel_c:
                onCancelClicked();
                break;
        }
    }

    public void onFilterApplied() {

        Log.d(TAG, "onFilterApplied: search clicked:"+getFilters().getPrice());
        mOnFilterSelectedListener_c.onFilter(getFilters());
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


    public void setFilterOptionsFontToRobotoLight() {

    }


}
