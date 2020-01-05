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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;

import co.in.prodigyschool.passiton.Data.OnFilterSelectionListener;
import co.in.prodigyschool.passiton.R;
import co.in.prodigyschool.passiton.util.Filters;


public class FilterCollegeFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "COLLEGE_FILTER_DIALOG";

    private View mRootView;
    private CheckBox freeOnly, filterTextbook, filterNotes,filterDegreeBtech,filterDegreeBsc
            ,filterDegreeBcom,filterDegreeBa, filterDegreeBba,filterDegreeBca,filterDegreeBed
            ,filterDegreeLlb,filterDegreeMbbs,filterDegreeOther;
    private TextView openSchoolFilters;
    private HomeViewModel homeViewModel;
    private OnFilterSelectionListener mOnFilterSelectedListener;
    private FilterDialogFragment parentFragment;


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
        parentFragment = (FilterDialogFragment)getParentFragment();
        onAttachToParentFragment(parentFragment.getParentFragment());
        homeViewModel = ViewModelProviders.of(parentFragment.getParentFragment()).get(HomeViewModel.class);
        // ...
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView =  inflater.inflate(R.layout.fragment_filter_college_dialog, container, false);
        freeOnly = mRootView.findViewById(R.id.freeOnlyCB);
        mRootView.findViewById(R.id.button_apply_c).setOnClickListener(this);
        mRootView.findViewById(R.id.button_cancel_c).setOnClickListener(this);
        mRootView.findViewById(R.id.clear_filter_button_c).setOnClickListener(this);

        filterDegreeBtech = mRootView.findViewById(R.id.filterBtech);
        filterDegreeBsc = mRootView.findViewById(R.id.filterBsc);
        filterDegreeBcom = mRootView.findViewById(R.id.filterBcom);
        filterDegreeBa = mRootView.findViewById(R.id.filterBa);
        filterDegreeBba = mRootView.findViewById(R.id.filterBba);
        filterDegreeBca = mRootView.findViewById(R.id.filterBca);
        filterDegreeBed = mRootView.findViewById(R.id.filterBed);
        filterDegreeLlb = mRootView.findViewById(R.id.filterLlb);
        filterDegreeMbbs = mRootView.findViewById(R.id.filterMbbs);
        filterDegreeOther = mRootView.findViewById(R.id.filterOtherDegree);

        freeOnly = mRootView.findViewById(R.id.freeOnlyCB);
        filterTextbook = mRootView.findViewById(R.id.filterTextbook_c);
        filterNotes = mRootView.findViewById(R.id.filterNotes_c);

        setFilterView(homeViewModel.getFilters());
        setFilterOptionsFontToRobotoLight();


        return mRootView;
    }

    private void setFilterView(Filters filters) {
        if(filters.IsNotes()){
            filterNotes.setChecked(true);
        }
        if(filters.IsText()){
            filterTextbook.setChecked(true);
        }
        if(filters.hasPrice()){
            freeOnly.setChecked(true);
        }

        if(filters.hasBookBoard()){
            for(int i:filters.getBookBoard()){
                switch (i){
                    case 7:filterDegreeBtech.setChecked(true);break;
                    case 8:filterDegreeBsc.setChecked(true);break;
                    case 9:filterDegreeBcom.setChecked(true);break;
                    case 10:filterDegreeBa.setChecked(true);break;
                    case 11:filterDegreeBba.setChecked(true);break;
                    case 12:filterDegreeBca.setChecked(true);break;
                    case 13:filterDegreeBed.setChecked(true);break;
                    case 14:filterDegreeLlb.setChecked(true);break;
                    case 15:filterDegreeMbbs.setChecked(true);break;
                    case 16:filterDegreeOther.setChecked(true);

                    default:
                }
            }

        }



    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_apply_c:
                onFilterApplied();
                break;
            case R.id.button_cancel_c:
                onCancelClicked();
                break;
            case R.id.clear_filter_button_c:
                onClearFilter();
                break;
        }
    }

    private void onClearFilter() {
        freeOnly.setChecked(false);
        filterTextbook.setChecked(false);
        filterNotes.setChecked(false);
        filterDegreeBtech.setChecked(false);
        filterDegreeBsc.setChecked(false);
        filterDegreeBcom.setChecked(false);
        filterDegreeBa.setChecked(false);
        filterDegreeBba.setChecked(false);
        filterDegreeBca.setChecked(false);
        filterDegreeBed.setChecked(false);
        filterDegreeLlb.setChecked(false);
        filterDegreeMbbs.setChecked(false);
        filterDegreeOther.setChecked(false);
    }


    public void onFilterApplied() {

        Log.d(TAG, "onFilterApplied: search clicked:"+getFilters().getPrice());
        if(mOnFilterSelectedListener != null)
            mOnFilterSelectedListener.onFilter(getFilters());
        if(parentFragment != null)
            parentFragment.dismiss();

    }

    public void onCancelClicked(){
        if(parentFragment != null)
            parentFragment.dismiss();
    }

    public Filters getFilters() {
        Filters filters = new Filters();

        if (mRootView != null) {
            filters.setPrice(getSelectedPrice());
            filters.setIsText(isTextBook());
            filters.setIsNotes(isNotes());
            filters.setBookBoard(getselectedBoard());

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
        if(filterDegreeBtech.isChecked()){
            selectedBoard.add(7);
        }
        if(filterDegreeBsc.isChecked()){
            selectedBoard.add(8);
        }
        if(filterDegreeBcom.isChecked()){
            selectedBoard.add(9);
        }
        if(filterDegreeBa.isChecked()){
            selectedBoard.add(10);
        }
        if(filterDegreeBba.isChecked()){
            selectedBoard.add(11);
        }
        if(filterDegreeBca.isChecked()){
            selectedBoard.add(12);
        }
        if(filterDegreeBed.isChecked()){
            selectedBoard.add(13);
        }
        if(filterDegreeLlb.isChecked()){
            selectedBoard.add(14);
        }
        if(filterDegreeMbbs.isChecked()){
            selectedBoard.add(15);
        }
        if(filterDegreeOther.isChecked()){
            selectedBoard.add(16);
        }
        return selectedBoard;
    }


    public void setFilterOptionsFontToRobotoLight() {

    }


}
