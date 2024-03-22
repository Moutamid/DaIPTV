package com.moutamid.daiptv.utilis;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class CustomArrayListLiveData<T> extends MutableLiveData<List<T>> {

    public void setValue(List<T> value) {
        super.setValue(value);
    }

    public void postValue(List<T> value) {
        super.postValue(value);
    }
}

