package co.in.prodigyschool.passiton.ui.bookRequests;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReqViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ReqViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Book requests");
    }

    public LiveData<String> getText() {
        return mText;
    }
}