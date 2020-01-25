package co.in.prodigyschool.passiton.ui.bookRequests;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import co.in.prodigyschool.passiton.util.Filters;

public class ReqViewModel extends ViewModel {

    public static  String TAG = "RequestViewModel";
    private MutableLiveData<String> mText;
    private Filters mFilters;


    public ReqViewModel() {
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