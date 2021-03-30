package ru.softvillage.test_evo.services.receiver;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import ru.evotor.pushNotifications.PushNotificationReceiver;
import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.liveDataHolder.OrderLiveData;
import ru.softvillage.test_evo.liveDataHolder.States;

public class PushReceiver extends PushNotificationReceiver {
    MutableLiveData<States> data = OrderLiveData.getInstance().getOrderMutableLiveData();

    @Override
    public void onReceivePushNotification(@NotNull Context context, @NotNull Bundle bundle, long l) {
        String action = bundle.getString("action");
        switch (action) {
            case "ALERT":
                Log.d(EvoApp.TAG, action);
                data.postValue(States.PRINT);
                break;
            case "NEW_WORK":
                data.postValue(States.CHECK_ORDER_ON_NETWORK);
                break;
            default:
                Log.d(EvoApp.TAG, "Unknown action");
        }
    }

}
