package ru.softvillage.fiscalizer.liveDataHolder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class OrderLiveData {
    private static OrderLiveData instance;
    private final MutableLiveData<States> orderLiveData = new MutableLiveData<>();

    public static OrderLiveData getInstance() {
        if (instance == null) {
            instance = new OrderLiveData();
        }
        return instance;
    }

    public LiveData<States> getOrderLiveData() {
        return orderLiveData;
    }

    public MutableLiveData<States> getOrderMutableLiveData() {
        return orderLiveData;
    }
}
