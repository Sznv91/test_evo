package ru.softvillage.fiscalizer.services.receiver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import ru.evotor.pushNotifications.PushNotificationReceiver;
import ru.softvillage.fiscalizer.EvoApp;
import ru.softvillage.fiscalizer.liveDataHolder.OrderLiveData;
import ru.softvillage.fiscalizer.liveDataHolder.States;
import ru.softvillage.fiscalizer.services.ForegroundServiceDispatcher;

public class PushReceiver extends PushNotificationReceiver {
    MutableLiveData<States> data = OrderLiveData.getInstance().getOrderMutableLiveData();

    @SuppressLint("LongLogTag")
    @Override
    public void onReceivePushNotification(@NotNull Context context, @NotNull Bundle bundle, long l) {
        String action = bundle.getString("action");
        Log.d(EvoApp.TAG+"_push_received", "push_received: " + action);
        Toast.makeText(context, "Received Push", Toast.LENGTH_LONG).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, ForegroundServiceDispatcher.class));
        } else {
            context.startService(new Intent(context, ForegroundServiceDispatcher.class));
        }


        switch (action) {
            case "ALERT":
                Log.d(EvoApp.TAG, action);
//                data.postValue(States.PRINT);

                break;
            case "NEW_WORK":
                data.postValue(States.CHECK_ORDER_ON_NETWORK);
                break;
            default:
                Log.d(EvoApp.TAG, "Unknown action");
        }
    }

}
