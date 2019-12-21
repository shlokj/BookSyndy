package co.in.prodigyschool.passiton.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import co.in.prodigyschool.passiton.util.Filters;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private Filters mFilters;

    public HomeViewModel() {
       mFilters = Filters.getDefault();
    }

    public Filters getFilters() {
        return mFilters;
    }

    public void setFilters(Filters mFilters) {
        this.mFilters = mFilters;
    }
}