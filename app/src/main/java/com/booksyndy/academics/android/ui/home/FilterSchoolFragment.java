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

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.booksyndy.academics.android.Data.OnFilterSelectionListener;
import com.booksyndy.academics.android.R;
import com.booksyndy.academics.android.util.Filters;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterSchoolFragment extends Fragment implements View.OnClickListener {

    /*How default filters are to be set:
     * For grade - check two boxes: the grade registered with AND the grade above it. For example, check 10 and 11 if the user is regd with 10
     * If the user has regd with 12, check only 12 here, and we'll add an option to view college book filters. Else college filters are all unchecked.
     * For board - check only the board the user has chosen during registration, and check competitive exams only if the user has checked it during registration
     * i.e., upto two board checks by default
     * For free/not - leave unchecked by default. Do not save prefs.
     * For textbook/notes - textbook by default. minimum one and maximum one selection. Logic will be added
     * For sort - relevance by default; don't save prefs
     * */

    private static String TAG = "FILTER_SCHOOL";
    private View mRootView;
    private CheckBox filterGrade5orBelow, filterGrade6to8, filterGrade9, filterGrade10, filterGrade11, filterGrade12;
    private CheckBox filterBoardCbse, filterBoardIcse, filterBoardIb, filterBoardIgcse, filterBoardState, filterBoardOther, filterBoardCompetitiveExams;
    private CheckBox freeOnly, filterTextbook, filterNotes;
    private OnFilterSelectionListener mOnFilterSelectedListener;
    private TextView openCollegeFilters;
    private HomeViewModel homeViewModel;
    private FilterDialogFragment parentFragment;
    private Spinner distSpinner, sortBySpinner;
    private ArrayAdapter<String> distAdapter, sortByAdapter;


    public FilterSchoolFragment() {
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
        // Inflate the layout for this fragment

        mRootView = inflater.inflate(R.layout.fragment_filter_school_dialog, container, false);
        freeOnly = mRootView.findViewById(R.id.freeOnlyCB);
        mRootView.findViewById(R.id.button_apply).setOnClickListener(this);
        mRootView.findViewById(R.id.button_cancel).setOnClickListener(this);
        mRootView.findViewById(R.id.clear_filter_button).setOnClickListener(this);

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
/*
        filterBoardCbse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filterBoardCompetitiveExams.setChecked(false);
                filterBoardCbse.setChecked(true);
                filterBoardIb.setChecked(false);
                filterBoardIcse.setChecked(false);
                filterBoardOther.setChecked(false);
                filterBoardIgcse.setChecked(false);
                filterBoardState.setChecked(false);
            }
        });

        filterBoardIcse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filterBoardCompetitiveExams.setChecked(false);
                filterBoardCbse.setChecked(false);
                filterBoardIb.setChecked(false);
                filterBoardIcse.setChecked(true);
                filterBoardOther.setChecked(false);
                filterBoardIgcse.setChecked(false);
                filterBoardState.setChecked(false);
            }
        });

        filterBoardIb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filterBoardCompetitiveExams.setChecked(false);
                filterBoardCbse.setChecked(false);
                filterBoardIb.setChecked(true);
                filterBoardIcse.setChecked(false);
                filterBoardOther.setChecked(false);
                filterBoardIgcse.setChecked(false);
                filterBoardState.setChecked(false);
            }
        });

        filterBoardIgcse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filterBoardCompetitiveExams.setChecked(false);
                filterBoardCbse.setChecked(false);
                filterBoardIb.setChecked(false);
                filterBoardIcse.setChecked(false);
                filterBoardOther.setChecked(false);
                filterBoardIgcse.setChecked(true);
                filterBoardState.setChecked(false);
            }
        });

        filterBoardState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filterBoardCompetitiveExams.setChecked(false);
                filterBoardCbse.setChecked(false);
                filterBoardIb.setChecked(false);
                filterBoardIcse.setChecked(false);
                filterBoardOther.setChecked(false);
                filterBoardIgcse.setChecked(false);
                filterBoardState.setChecked(true);
            }
        });

        filterBoardOther.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                filterBoardCompetitiveExams.setChecked(false);
                filterBoardCbse.setChecked(false);
                filterBoardIb.setChecked(false);
                filterBoardIcse.setChecked(false);
                filterBoardOther.setChecked(true);
                filterBoardIgcse.setChecked(false);
                filterBoardState.setChecked(false);
            }
        });
        */
        filterBoardCbse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterBoardCompetitiveExams.setChecked(false);
                filterBoardCbse.setChecked(true);
                filterBoardIb.setChecked(false);
                filterBoardIcse.setChecked(false);
                filterBoardOther.setChecked(false);
                filterBoardIgcse.setChecked(false);
                filterBoardState.setChecked(false);
            }
        });

        filterBoardIcse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterBoardCompetitiveExams.setChecked(false);
                filterBoardCbse.setChecked(false);
                filterBoardIb.setChecked(false);
                filterBoardIcse.setChecked(true);
                filterBoardOther.setChecked(false);
                filterBoardIgcse.setChecked(false);
                filterBoardState.setChecked(false);
            }
        });

        filterBoardIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterBoardCompetitiveExams.setChecked(false);
                filterBoardCbse.setChecked(false);
                filterBoardIb.setChecked(true);
                filterBoardIcse.setChecked(false);
                filterBoardOther.setChecked(false);
                filterBoardIgcse.setChecked(false);
                filterBoardState.setChecked(false);
            }
        });

        filterBoardIgcse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterBoardCompetitiveExams.setChecked(false);
                filterBoardCbse.setChecked(false);
                filterBoardIb.setChecked(false);
                filterBoardIcse.setChecked(false);
                filterBoardOther.setChecked(false);
                filterBoardIgcse.setChecked(true);
                filterBoardState.setChecked(false);
            }
        });

        filterBoardState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterBoardCompetitiveExams.setChecked(false);
                filterBoardCbse.setChecked(false);
                filterBoardIb.setChecked(false);
                filterBoardIcse.setChecked(false);
                filterBoardOther.setChecked(false);
                filterBoardIgcse.setChecked(false);
                filterBoardState.setChecked(true);
            }
        });

        filterBoardOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterBoardCompetitiveExams.setChecked(false);
                filterBoardCbse.setChecked(false);
                filterBoardIb.setChecked(false);
                filterBoardIcse.setChecked(false);
                filterBoardOther.setChecked(true);
                filterBoardIgcse.setChecked(false);
                filterBoardState.setChecked(false);
            }
        });

        filterBoardCompetitiveExams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterBoardCompetitiveExams.setChecked(true);
                filterBoardCbse.setChecked(false);
                filterBoardIb.setChecked(false);
                filterBoardIcse.setChecked(false);
                filterBoardOther.setChecked(false);
                filterBoardIgcse.setChecked(false);
                filterBoardState.setChecked(false);
            }
        });

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
                    case 1:
                        filterBoardCbse.setChecked(true);
                        break;
                    case 2:
                        filterBoardIcse.setChecked(true);
                        break;
                    case 3:
                        filterBoardIb.setChecked(true);
                        break;
                    case 4:
                        filterBoardIgcse.setChecked(true);
                        break;
                    case 5:
                        filterBoardState.setChecked(true);
                        break;
                    case 6:
                        filterBoardOther.setChecked(true);
                        break;
                    case 20:
                        filterBoardCompetitiveExams.setChecked(true);
                    default:
                }
            }

        }

        if (filters.hasBookGrade()) {
            for (int i : filters.getBookGrade()) {
                switch (i) {
                    case 1:
                        filterGrade5orBelow.setChecked(true);
                        break;
                    case 2:
                        filterGrade6to8.setChecked(true);
                        break;
                    case 3:
                        filterGrade9.setChecked(true);
                        break;
                    case 4:
                        filterGrade10.setChecked(true);
                        break;
                    case 5:
                        filterGrade11.setChecked(true);
                        break;
                    case 6:
                        filterGrade12.setChecked(true);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_apply:
                onFilterApplied();
                break;
            case R.id.button_cancel:
                onCancelClicked();
                break;
            case R.id.clear_filter_button:
                onClearFilter();
                break;

        }
    }

    private void onClearFilter() {
        freeOnly.setChecked(false);
        filterTextbook.setChecked(false);
        filterNotes.setChecked(false);
        filterGrade5orBelow.setChecked(false);
        filterGrade6to8.setChecked(false);
        filterGrade9.setChecked(false);
        filterGrade10.setChecked(false);
        filterGrade11.setChecked(false);
        filterGrade12.setChecked(false);

//        filterBoardCompetitiveExams.setChecked(false);
//        filterBoardCbse.setChecked(false);
//        filterBoardIb.setChecked(false);
//        filterBoardIcse.setChecked(false);
//        filterBoardOther.setChecked(false);
//        filterBoardIgcse.setChecked(false);
//        filterBoardState.setChecked(false);

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
            filters.setBookBoard(getselectedBoard());
            filters.setBookGrade(getselectedGrade());
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
        return filterTextbook.isChecked();
    }

    private boolean isNotes() {
        return filterNotes.isChecked();
    }

    private List<Integer> getselectedBoard() {
        ArrayList<Integer> selectedBoard = new ArrayList<>();
        if (filterBoardCbse.isChecked()) {
            selectedBoard.add(1);
        }
        if (filterBoardIcse.isChecked()) {
            selectedBoard.add(2);
        }
        if (filterBoardIb.isChecked()) {
            selectedBoard.add(3);
        }
        if (filterBoardIgcse.isChecked()) {
            selectedBoard.add(4);
        }
        if (filterBoardState.isChecked()) {
            selectedBoard.add(5);
        }
        if (filterBoardOther.isChecked()) {
            selectedBoard.add(6);
        }
        if (filterBoardCompetitiveExams.isChecked()) {
            selectedBoard.add(20);
        }
        return selectedBoard;
    }

    private List<Integer> getselectedGrade() {
        ArrayList<Integer> selectedGrade = new ArrayList<>();
        if (filterGrade5orBelow.isChecked()) {
            selectedGrade.add(1);
        }
        if (filterGrade6to8.isChecked()) {
            selectedGrade.add(2);
        }
        if (filterGrade9.isChecked()) {
            selectedGrade.add(3);
        }
        if (filterGrade10.isChecked()) {
            selectedGrade.add(4);
        }
        if (filterGrade11.isChecked()) {
            selectedGrade.add(5);
        }
        if (filterGrade12.isChecked()) {
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
