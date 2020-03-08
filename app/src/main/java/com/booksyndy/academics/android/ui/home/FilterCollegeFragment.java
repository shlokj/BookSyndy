package com.booksyndy.academics.android.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.booksyndy.academics.android.Data.OnFilterSelectionListener;
import com.booksyndy.academics.android.R;
import com.booksyndy.academics.android.util.Filters;

import java.util.ArrayList;
import java.util.List;


public class FilterCollegeFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "COLLEGE_FILTER_DIALOG";

    private View mRootView;
    private CheckBox freeOnly, filterTextbook, filterNotes, filterDegreeBtech, filterDegreeBsc, filterDegreeBcom, filterDegreeBa, filterDegreeBba, filterDegreeBca, filterDegreeBed, filterDegreeLlb, filterDegreeMbbs, filterDegreeOther;
    private TextView openSchoolFilters;
    private HomeViewModel homeViewModel;
    private OnFilterSelectionListener mOnFilterSelectedListener;
    private FilterDialogFragment parentFragment;
    private Spinner distSpinner, sortBySpinner;
    private ArrayAdapter<String> distAdapter, sortByAdapter;


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
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_filter_college_dialog, container, false);
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

        distSpinner = mRootView.findViewById(R.id.distanceSpinner);
        sortBySpinner = mRootView.findViewById(R.id.sortBySpinner);

        distAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.distancesBy5));
        distAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distSpinner.setAdapter(distAdapter);


        sortByAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.sorts));
        sortByAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(sortByAdapter);

        setFilterView(homeViewModel.getFilters());
        setFilterOptionsFontToRobotoLight();


        return mRootView;
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

        if (filters.hasBookBoard()) {
            for (int i : filters.getBookBoard()) {
                switch (i) {
                    case 7:
                        filterDegreeBtech.setChecked(true);
                        break;
                    case 8:
                        filterDegreeBsc.setChecked(true);
                        break;
                    case 9:
                        filterDegreeBcom.setChecked(true);
                        break;
                    case 10:
                        filterDegreeBa.setChecked(true);
                        break;
                    case 11:
                        filterDegreeBba.setChecked(true);
                        break;
                    case 12:
                        filterDegreeBca.setChecked(true);
                        break;
                    case 13:
                        filterDegreeBed.setChecked(true);
                        break;
                    case 14:
                        filterDegreeLlb.setChecked(true);
                        break;
                    case 15:
                        filterDegreeMbbs.setChecked(true);
                        break;
                    case 16:
                        filterDegreeOther.setChecked(true);

                    default:
                }
            }

        }

        if (filters.hasSortBy()) {
            switch (filters.getSortBy()) {
                case "time":
                    sortBySpinner.setSelection(1);
                    break;
                case "distance":
                    sortBySpinner.setSelection(2);
                    break;
                default:
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
        distSpinner.setSelection(distAdapter.getCount() - 2);
        sortBySpinner.setSelection(1);
    }


    public void onFilterApplied() {

        Log.d(TAG, "onFilterApplied: search clicked:" + getFilters().getPrice());
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
            filters.setBookBoard(getselectedBoard());
            filters.setBookDistance(getSelectedDistance());
            filters.setSortBy(getSortBy());
        }
        return filters;
    }

    private String getSortBy() {
        //default  sortBy = "Relevance";
        switch (sortBySpinner.getSelectedItemPosition()) {
            case 1:
                return "time";

            case 2:
                return "distance";

            default:
                return "Relevance";
        }


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
        if (filterDegreeBtech.isChecked()) {
            selectedBoard.add(7);
        }
        if (filterDegreeBsc.isChecked()) {
            selectedBoard.add(8);
        }
        if (filterDegreeBcom.isChecked()) {
            selectedBoard.add(9);
        }
        if (filterDegreeBa.isChecked()) {
            selectedBoard.add(10);
        }
        if (filterDegreeBba.isChecked()) {
            selectedBoard.add(11);
        }
        if (filterDegreeBca.isChecked()) {
            selectedBoard.add(12);
        }
        if (filterDegreeBed.isChecked()) {
            selectedBoard.add(13);
        }
        if (filterDegreeLlb.isChecked()) {
            selectedBoard.add(14);
        }
        if (filterDegreeMbbs.isChecked()) {
            selectedBoard.add(15);
        }
        if (filterDegreeOther.isChecked()) {
            selectedBoard.add(16);
        }
        return selectedBoard;
    }


    public void setFilterOptionsFontToRobotoLight() {
        filterDegreeBtech.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterDegreeBsc.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterDegreeBcom.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterDegreeBa.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterDegreeBba.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterDegreeBca.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterDegreeBed.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterDegreeLlb.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterDegreeMbbs.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterDegreeOther.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        freeOnly.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterTextbook.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
        filterNotes.setTypeface(ResourcesCompat.getFont(getActivity(), R.font.roboto_light));
    }
}
