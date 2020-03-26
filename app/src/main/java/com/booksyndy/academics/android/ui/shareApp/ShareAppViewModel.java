package com.booksyndy.academics.android.ui.shareApp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShareAppViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ShareAppViewModel() {

    }

    public LiveData<String> getText() {
        return mText;
    }
}