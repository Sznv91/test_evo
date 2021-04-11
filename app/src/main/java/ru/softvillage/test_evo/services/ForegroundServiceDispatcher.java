package ru.softvillage.test_evo.services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.evotor.framework.receipt.Position;
import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.MainActivity;
import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.liveDataHolder.OrderLiveData;
import ru.softvillage.test_evo.liveDataHolder.States;
import ru.softvillage.test_evo.network.OrderInterface;
import ru.softvillage.test_evo.network.entity.Good;
import ru.softvillage.test_evo.network.entity.NetworkAnswer;
import ru.softvillage.test_evo.roomDb.Entity.GoodEntity;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptEntity;
import ru.softvillage.test_evo.utils.PositionCreator;
import ru.softvillage.test_evo.utils.PrintUtil;
// Примеры:
// https://gist.github.com/sunmeat/c7e824f9c1e83c85e987c70e1ef8bb35

public class ForegroundServiceDispatcher extends Service {
    OrderInterface orderInterface = EvoApp.getInstance().getOrderInterface();

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
//        initRetrofit();
        LiveData<States> data = OrderLiveData.getInstance().getOrderLiveData();
        /*data.observeForever(s -> {
            if (s.equals(States.PRINT)) {
                PrintUtil.getInstance().printDemoOrder(getApplicationContext());
            }

        });*/
        new Thread(() -> {

            while (true) {
                orderInterface.getMainRequest().enqueue(new Callback<NetworkAnswer>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onResponse(Call<NetworkAnswer> call, Response<NetworkAnswer> response) {
                        if (response.code() == 200) {
                            if (response.body().getSuccess()) {
                                Log.d(EvoApp.TAG, "Received TRUE Order");
                                PositionCreator.OrderTo toProcessing = PositionCreator.makeOrderList(response.body().getOrderList());
                                for (PositionCreator.OrderTo.PositionTo orderTo : toProcessing.getOrderList()) {
                                    List <GoodEntity> goodsToDB = new ArrayList<>();
                                    orderTo.getOrderData().goods.forEach(good -> {
                                        goodsToDB.add(new GoodEntity(good, orderTo.getOrderData().id));
                                    });
                                    //todo Вынести в отдельный метод.
                                    ReceiptEntity dataToDb = new ReceiptEntity(orderTo);
                                    EvoApp.getInstance().getDbHelper().insertReceiptToDb(dataToDb);
                                    PrintUtil.getInstance().printOrder(getApplicationContext(), orderTo, printCallback);
                                }

                            } else {
                                Log.d(EvoApp.TAG, response.body().getErrorMessage() + " : -> Received error message");
                            }
                        } else {
                            Log.d(EvoApp.TAG, "Unexpected code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<NetworkAnswer> call, Throwable t) {
                        Log.d(EvoApp.TAG, "Network error: " + t.getMessage());
                    }
                });
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }).start();
        /////////////////////////

        return Service.START_STICKY;
    }

    PrintUtil.PrintCallback printCallback = new PrintUtil.PrintCallback() {
        @Override
        public void printSuccess() {

        }

        @Override
        public void printFailure(PositionCreator.OrderTo.PositionTo order/*List<Position> list, BigDecimal receiptCost*/) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> PrintUtil.getInstance().printOrder(
                    getApplicationContext(),
                    order,
                    this), 1000);
        }
    };

}
