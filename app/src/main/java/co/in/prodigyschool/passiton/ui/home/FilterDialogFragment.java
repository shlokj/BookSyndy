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
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;

import co.in.prodigyschool.passiton.R;
import co.in.prodigyschool.passiton.util.Filters;

public class FilterDialogFragment extends DialogFragment implements View.OnClickListener {


    public static final String TAG = "FILTER_DIALOG";

    private View mRootView;
    private CheckBox filterGrade5orBelow, filterGrade6to8, filterGrade9, filterGrade10, filterGrade11, filterGrade12;
    private CheckBox filterBoardCbse, filterBoardIcse, filterBoardIb, filterBoardIgcse, filterBoardState, filterBoardOther, filterBoardCompetitiveExams;
    private CheckBox freeOnly, filterTextbook, filterNotes;
    private OnFilterSelectionListener mOnFilterSelectedListener;
    private TextView openCollegeFilters;
    private HomeViewModel homeViewModel;
    FilterCollegeDialogFragment mCFdialog;


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
        homeViewModel = ViewModelProviders.of(getParentFragment()).get(HomeViewModel.class);
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
        mRootView.findViewById(R.id.button_apply).setOnClickListener(this);
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

        openCollegeFilters = mRootView.findViewById(R.id.collegeFiltersButton);

        mCFdialog = new FilterCollegeDialogFragment();

/*
        openCollegeFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCFdialog.show(getChildFragmentManager(),FilterCollegeDialogFragment.TAG);
            }
        });
*/
        //setDefaultFilters();

        return mRootView;
    }

//    private void setDefaultFilters() {
//
//        Filters defaultFilters = homeViewModel.getFilters();
//
//        if(defaultFilters.IsText()){
//            filterTextbook.setChecked(true);
//        }
//        if(defaultFilters.hasBookBoard()){
//            int bookBoard = defaultFilters.getBookBoard();
//            if(bookBoard == 1){
//                filterBoardCbse.setChecked(true);
//            }
//            else if(bookBoard == 2){
//                filterBoardIcse.setChecked(true);
//            }
//            else if(bookBoard == 3){
//                filterBoardIb.setChecked(true);
//            }
//            else if(bookBoard == 4){
//                filterBoardIgcse.setChecked(true);
//            }
//            else if(bookBoard == 5){
//                filterBoardState.setChecked(true);
//            }
//            else if(bookBoard == 6){
//                filterBoardOther.setChecked(true);
//            }
//        }
//
//        if(defaultFilters.hasBookGrade()){
//            int bookGrade = defaultFilters.getBookGrade();
//            if(bookGrade == 1){
//                filterGrade5orBelow.setChecked(true);
//            }
//            else if(bookGrade == 2){
//                filterGrade6to8.setChecked(true);
//            }
//            else if(bookGrade == 3){
//                filterGrade9.setChecked(true);
//            }
//            else if(bookGrade == 4){
//                filterGrade10.setChecked(true);
//            }
//            else if(bookGrade == 5){
//                filterGrade11.setChecked(true);
//            }
//            else if(bookGrade == 6){
//                filterGrade12.setChecked(true);
//            }
//        }
//
//
//    }

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
            case R.id.button_apply:
                onFilterApplied();
                break;
            case R.id.button_cancel:
                onCancelClicked();
                break;
        }
    }

    public void onFilterApplied() {

        Log.d(TAG, "onFilterApplied: search clicked:"+getFilters().getPrice());
        if(mOnFilterSelectedListener != null)
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
            filters.setIsText(isTextBook());
            filters.setIsNotes(isNotes());
            filters.setBookBoard(getselectedBoard());
            filters.setBookGrade(getselectedGrade());


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

    //is text or notes
    private boolean isTextBook() {
        if (filterTextbook.isChecked()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isNotes() {
        if (filterNotes.isChecked()) {
            return true;
        } else {
            return false;
        }
    }

    private List<Integer> getselectedBoard() {
        ArrayList<Integer> selectedBoard = new ArrayList<>();
        if(filterBoardCbse.isChecked()){
            selectedBoard.add(1);
        }
        if(filterBoardIcse.isChecked()){
            selectedBoard.add(2);
        }
        if(filterBoardIb.isChecked()){
            selectedBoard.add(3);
        }
        if(filterBoardIgcse.isChecked()){
            selectedBoard.add(4);
        }
        if(filterBoardState.isChecked()){
            selectedBoard.add(5);
        }
        if(filterBoardOther.isChecked()){
            selectedBoard.add(6);
        }
        if(filterBoardCompetitiveExams.isChecked()){
            selectedBoard.add(20);
        }
        return selectedBoard;
    }

    private List<Integer> getselectedGrade() {
        ArrayList<Integer> selectedGrade = new ArrayList<>();
        if(filterGrade5orBelow.isChecked()){
            selectedGrade.add(1);
        }
        if(filterGrade6to8.isChecked()){
            selectedGrade.add(2);
        }
        if(filterGrade9.isChecked()){
            selectedGrade.add(3);
        }
        if(filterGrade10.isChecked()){
            selectedGrade.add(4);
        }
        if(filterGrade11.isChecked()){
            selectedGrade.add(5);
        }
        if(filterGrade12.isChecked()){
            selectedGrade.add(6);
        }
        return selectedGrade;
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
