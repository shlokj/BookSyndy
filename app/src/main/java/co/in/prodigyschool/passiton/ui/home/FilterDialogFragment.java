package co.in.prodigyschool.passiton.ui.home;

import androidx.core.content.res.ResourcesCompat;
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
    private CheckBox filterGrade5orBelow, filterGrade6to8, filterGrade9, filterGrade10, filterGrade11, filterGrade12;
    private CheckBox filterBoardCbse, filterBoardIcse, filterBoardIb, filterBoardIgcse, filterBoardState, filterBoardOther, filterBoardCompetitiveExams;
    private CheckBox freeOnly, filterTextbook, filterNotes;
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

        filterGrade5orBelow = mRootView.findViewById(R.id.filterGrade5orBelow);
        filterGrade6to8 = mRootView.findViewById(R.id.filterGrade6to8);
        filterGrade9 = mRootView.findViewById(R.id.filterGrade9);
        filterGrade10 = mRootView.findViewById(R.id.filterGrade10);
        filterGrade11 = mRootView.findViewById(R.id.filterGrade11);
        filterGrade12 = mRootView.findViewById(R.id.filterGrade12);

        filterBoardCbse = mRootView.findViewById(R.id.filterBoardCbse);
        filterBoardIcse = mRootView.findViewById(R.id.filterBoardIcse);
        filterBoardIb = mRootView.findViewById(R.id.filterBoardIb);
        filterBoardIgcse = mRootView.findViewById(R.id.filterBoardIgcse);
        filterBoardState = mRootView.findViewById(R.id.filterBoardState);
        filterBoardOther = mRootView.findViewById(R.id.filterBoardOther);
        filterBoardCompetitiveExams = mRootView.findViewById(R.id.filterBoardCompetitiveExams);

        filterTextbook = mRootView.findViewById(R.id.filterTextbook);
        filterNotes = mRootView.findViewById(R.id.filterNotes);

        setFilterOptionsFontToRobotoLight();

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


    public void setFilterOptionsFontToRobotoLight() {
        filterGrade5orBelow.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterGrade6to8.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterGrade9.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterGrade10.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterGrade11.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterGrade12.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterBoardCbse.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterBoardIcse.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterBoardIb.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterBoardIgcse.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterBoardState.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterBoardOther.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterBoardCompetitiveExams.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        freeOnly.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterTextbook.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterNotes.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
    }

}
