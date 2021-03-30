package ru.softvillage.test_evo.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;

import ru.softvillage.test_evo.MainActivity;
import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.liveDataHolder.OrderLiveData;
import ru.softvillage.test_evo.liveDataHolder.States;
import ru.softvillage.test_evo.utils.PrintUtil;
// Примеры:
// https://gist.github.com/sunmeat/c7e824f9c1e83c85e987c70e1ef8bb35

public class ForegroundServiceDispatcher extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String info = "foreground Service";
        Context context = getApplicationContext();

        PendingIntent action = PendingIntent.getActivity(context,
                0, new Intent(context, MainActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT); // Flag indicating that if the described PendingIntent already exists, the current one should be canceled before generating a new one.

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            //todo запросить разрешение на работу "android.permission.FOREGROUND_SERVICE"
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "alex_channel";

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "SoftVillageChanel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("SV channel description");
            manager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }

        builder.setContentIntent(action)
                .setContentTitle(info)
                .setTicker(info)
                .setContentText(info)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(action)
                .setOngoing(true).build();

        startForeground(1, builder.build());
//        return super.onStartCommand(intent, flags, startId);

        /*Логика диспетчерезации*/
        //////////////////////////
        LiveData<States> data = OrderLiveData.getInstance().getOrderLiveData();
        data.observeForever(s -> {
            if (s.equals(States.PRINT)) {
                PrintUtil.getInstance().printDemoOrder(getApplicationContext());
            }

        });
        /////////////////////////

        return Service.START_STICKY;
    }


}
