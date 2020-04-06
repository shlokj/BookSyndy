package com.booksyndy.academics.android.ui.switchMode;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SwitchModeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SwitchModeViewModel() {

    }

    public LiveData<String> getText() {
        return mText;
    }
}