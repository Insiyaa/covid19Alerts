package com.example.covid19alerts.ui.send;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SendViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SendViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Request for a daily necessity in the neighbourhood");
    }

    public LiveData<String> getText() {
        return mText;
    }
}