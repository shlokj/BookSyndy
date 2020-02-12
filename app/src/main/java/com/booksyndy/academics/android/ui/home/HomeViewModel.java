package com.booksyndy.academics.android.ui.home;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.booksyndy.academics.android.util.Filters;

public class HomeViewModel extends ViewModel {

    public static  String TAG = "HOMEVIEWMODEL";
    private MutableLiveData<String> mText;
    private Filters mFilters;


    public HomeViewModel() {
       mFilters = Filters.getDefault();

    }

    public Filters getFilters() {
        Log.d(TAG, "getFilters: "+mFilters.getPrice());
        return mFilters;
    }

    public void setFilters(Filters mFilters) {
        Log.d(TAG, "setFilters: "+mFilters.getPrice());
        this.mFilters = mFilters;
    }

}