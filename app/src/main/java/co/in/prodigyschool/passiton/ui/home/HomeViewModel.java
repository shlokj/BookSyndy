package co.in.prodigyschool.passiton.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import co.in.prodigyschool.passiton.Data.Book;
import co.in.prodigyschool.passiton.util.Filters;

public class HomeViewModel extends ViewModel {

    public static  String TAG = "HOMEVIEWMODEL";
    private MutableLiveData<String> mText;
    private Filters mFilters;
    private List<Book> bookList;

    public HomeViewModel() {
       mFilters = Filters.getDefault();
       bookList = new ArrayList<>();
    }

    public Filters getFilters() {
        Log.d(TAG, "getFilters: "+mFilters.getPrice());
        return mFilters;
    }

    public void setFilters(Filters mFilters) {
        Log.d(TAG, "setFilters: "+mFilters.getPrice());
        this.mFilters = mFilters;
    }

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {
        this.bookList.clear();
        this.bookList.addAll(bookList);
    }
}