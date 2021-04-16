package com.example.douglasbookstore.ui.bookshelf;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BookShelfViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BookShelfViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}