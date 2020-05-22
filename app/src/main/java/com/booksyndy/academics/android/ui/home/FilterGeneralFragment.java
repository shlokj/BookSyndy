package com.booksyndy.academics.android.ui.home;

import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.booksyndy.academics.android.Data.OnFilterSelectionListener;
import com.booksyndy.academics.android.R;
import com.booksyndy.academics.android.util.Filters;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterGeneralFragment extends Fragment implements View.OnClickListener {


    private static String TAG = "FILTER_GENERAL";
    private View mRootView;
    private CheckBox freeOnly, filterTextbook, filterNotes;
    private OnFilterSelectionListener mOnFilterSelectedListener;
    private TextView openCollegeFilters;
    private HomeViewModel homeViewModel;
    private FilterDialogFragment parentFragment;
    private Spinner distSpinner, sortBySpinner;
    private ArrayAdapter<String> distAdapter, sortByAdapter;


    public FilterGeneralFragment() {
        // Required empty public constructor
    }

    public void onAttachToParentFragment(Fragment fragment) {
        try {
            mOnFilterSelectedListener = (OnFilterSelectionListener) fragment;

        } catch (ClassCastException e) {
            throw new ClassCastException(
                    fragment.toString() + " must implement OnFilterSelectionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        parentFragment = (FilterDialogFragment) getParentFragment();
        onAttachToParentFragment(parentFragment.getParentFragment());
        homeViewModel = ViewModelProviders.of(parentFragment.getParentFragment()).get(HomeViewModel.class);
        // ...
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_filter_general_dialog, container, false);
        freeOnly = mRootView.findViewById(R.id.freeOnlyCB_g);
        mRootView.findViewById(R.id.button_apply_g).setOnClickListener(this);
        mRootView.findViewById(R.id.button_cancel_g).setOnClickListener(this);
        mRootView.findViewById(R.id.clear_filter_button_g).setOnClickListener(this);

        filterTextbook = mRootView.findViewById(R.id.filterTextbook_g);
        filterNotes = mRootView.findViewById(R.id.filterNotes_g);

        distSpinner = mRootView.findViewById(R.id.distanceSpinner_g);


        distAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.distancesBy5));
        distAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distSpinner.setAdapter(distAdapter);

        setFilterView(homeViewModel.getFilters());
        setFilterOptionsFontToRobotoLight();

        return mRootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_apply_g:
                onFilterApplied();
                break;
            case R.id.button_cancel_g:
                onCancelClicked();
                break;
            case R.id.clear_filter_button_g:
                onClearFilter();
                break;

        }
    }

    private void onClearFilter() {
        freeOnly.setChecked(false);
        filterTextbook.setChecked(false);
        filterNotes.setChecked(false);
        distSpinner.setSelection(distAdapter.getCount() - 2);
        sortBySpinner.setSelection(1);

    }

    public void onFilterApplied() {

        Log.d(TAG, "onFilterApplied: search clicked:");
        if (mOnFilterSelectedListener != null)
            mOnFilterSelectedListener.onFilter(getFilters());
        if (parentFragment != null)
            parentFragment.dismiss();
    }

    public void onCancelClicked() {
        if (parentFragment != null)
            parentFragment.dismiss();
    }

    public Filters getFilters() {
        Filters filters = new Filters();

        if (mRootView != null) {
            filters.setPrice(getSelectedPrice());
            filters.setIsText(isTextBook());
            filters.setIsNotes(isNotes());
            filters.setBookDistance(getSelectedDistance());

        }
        return filters;
    }

    private int getSelectedDistance() {
        switch (distSpinner.getSelectedItemPosition()) {
            case 0:
                return 5;
            case 1:
                return 10;
            case 2:
                return 15;
            case 3:
                return 20;
            case 4:
                return 25;
            case 5:
                return 30;
            default:
                return -1;
        }
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
        return filterTextbook.isChecked();
    }

    private boolean isNotes() {
        return filterNotes.isChecked();
    }


    private void setFilterView(Filters filters) {
        if (filters.IsNotes()) {
            filterNotes.setChecked(true);
        }
        if (filters.IsText()) {
            filterTextbook.setChecked(true);
        }
        if (filters.hasPrice()) {
            freeOnly.setChecked(true);
        }

        if (filters.hasBookDistance()) {
            switch (filters.getBookDistance()) {
                case 5:
                    distSpinner.setSelection(0);
                    break;
                case 10:
                    distSpinner.setSelection(1);
                    break;
                case 15:
                    distSpinner.setSelection(2);
                    break;
                case 20:
                    distSpinner.setSelection(3);
                    break;
                case 25:
                    distSpinner.setSelection(4);
                    break;
                case 30:
                    distSpinner.setSelection(5);
                    break;
                default:
                    distSpinner.setSelection(6);
            }
        } else {
            distSpinner.setSelection(6);
        }
    }

    public void setFilterOptionsFontToRobotoLight() {
        freeOnly.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterTextbook.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterNotes.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
    }
}
