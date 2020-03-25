package com.example.covid19alerts.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SlideshowViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SlideshowViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment. Log of daily activities, kinda like journal to keep up with motivation.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}