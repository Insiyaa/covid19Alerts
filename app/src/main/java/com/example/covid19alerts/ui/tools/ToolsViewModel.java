package com.example.covid19alerts.ui.tools;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ToolsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ToolsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Requests of people for daily necessities, sorted by location. Help the close by ppl.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}