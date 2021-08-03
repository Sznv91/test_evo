package ru.softvillage.fiscalizer.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.evotor.framework.system.SystemStateApi;
import ru.softvillage.fiscalizer.EvoApp;
import ru.softvillage.fiscalizer.MainActivity;
import ru.softvillage.fiscalizer.R;
import ru.softvillage.fiscalizer.liveDataHolder.OrderLiveData;
import ru.softvillage.fiscalizer.liveDataHolder.States;
import ru.softvillage.fiscalizer.network.OrderInterface;
import ru.softvillage.fiscalizer.network.entity.NetworkAnswer;
import ru.softvillage.fiscalizer.network.entity.Order;
import ru.softvillage.fiscalizer.roomDb.Entity.fiscalized.GoodEntity;
import ru.softvillage.fiscalizer.roomDb.Entity.fiscalized.ReceiptEntity;
import ru.softvillage.fiscalizer.roomDb.Entity.fiscalized.ReceiptWithGoodEntity;
import ru.softvillage.fiscalizer.roomDb.Entity.fromNetwork.GoodDb;
import ru.softvillage.fiscalizer.roomDb.Entity.fromNetwork.OrderDbWithGoods;
import ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter;
import ru.softvillage.fiscalizer.utils.PositionCreator;
// Примеры:
// https://gist.github.com/sunmeat/c7e824f9c1e83c85e987c70e1ef8bb35

public class ForegroundServiceDispatcher extends Service {
    OrderInterface orderInterface = EvoApp.getInstance().getOrderInterface();
    public static Notification notification = null;
    public static NotificationManager manager;
    static NotificationCompat.Builder builder;
    static String info = "foreground Service";
    static String title = "Фискализатор Soft-Village";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       /* String info = "foreground Service";
        String title = "Фискализатор Soft-Village";*/
        Context context = getApplicationContext();

        PendingIntent action = PendingIntent.getActivity(context,
                0, new Intent(context, MainActivity.class),
                PendingIntent.FLAG_CANCEL_CURRENT); // Flag indicating that if the described PendingIntent already exists, the current one should be canceled before generating a new one.

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationCompat.Builder builder;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            //todo запросить разрешение на работу "android.permission.FOREGROUND_SERVICE"
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String CHANNEL_ID = "soft_village_channel";

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "SoftVillageChanel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("SV fiscalizer notify channel");
            manager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(getApplicationContext());
        }


        builder.setContentIntent(action)
                .setContentTitle(title/*info*/)
                .setTicker(info)
//                .setContentText("Фискализированно чеков за смену: " + SessionPresenter.getInstance().getSessionData().getCountReceipt()/*info*/)
                .setSmallIcon(R.drawable.ic_menu_2)
                .setContentIntent(action)
                .setOngoing(true)/*.build()*/;
        if (SystemStateApi.isSessionOpened(EvoApp.getInstance())) {
            builder.setContentIntent(action).setContentText("Фискализированно чеков за смену: " + SessionPresenter.getInstance().getSessionData().getCountReceipt()/*info*/);
        } else {
            Long sessionNum = SystemStateApi.getLastSessionNumber(EvoApp.getInstance());
            if (sessionNum != null && sessionNum > 0) {
                builder.setContentIntent(action).setContentText(String.format("Смена №%03d закрыта", sessionNum));
            } else {
                builder.setContentIntent(action).setContentText("Смена закрыта");
            }
        }

        notification = builder.build();

        startForeground(1, notification/*builder.build()*/);
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

        Intent SessionCloseWatcher = new Intent(context, SessionCloseWatcher.class);
//        serviceIntent.setAction("cidadaos.cidade.data.UpdaterServiceManager");
        context.startService(SessionCloseWatcher);

        Intent PrintDispatcher = new Intent(context, PrintDispatcher.class);
        context.startService(PrintDispatcher);


        new Thread(() -> {

            while (true) {
                if (SessionPresenter.getInstance().getIsCheckedUserAgreement()) {
                    orderInterface.getMainRequest().enqueue(new Callback<NetworkAnswer>() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onResponse(Call<NetworkAnswer> call, Response<NetworkAnswer> response) {
                            if (response.code() == 200) {
                                if (response.body().getSuccess()) {
                                    Log.d(EvoApp.TAG, "Received TRUE Order");

                                    List<Order> orders = response.body().getOrderList();
                                    /**
                                     * Добавление данных в БД для очереди печати
                                     */
                                    for (Order order : orders) {
                                        OrderDbWithGoods orderDbWithGoods = new OrderDbWithGoods(order);
                                        EvoApp.getInstance().getDbHelper().createOrderDbWithGoods(orderDbWithGoods);
                                    }

                                    PositionCreator.OrderTo toProcessing = PositionCreator.makeOrderList(new ArrayList<>(response.body().getOrderList()));
                                    for (PositionCreator.OrderTo.PositionTo orderTo : toProcessing.getOrderList()) {
                                        ReceiptEntity dataToDb = new ReceiptEntity(orderTo);
                                        ReceiptWithGoodEntity receiptWithGoodEntity = new ReceiptWithGoodEntity();
                                        receiptWithGoodEntity.setReceiptEntity(dataToDb);
//                                    EvoApp.getInstance().getDbHelper().insertReceiptToDb(dataToDb);
                                        List<GoodEntity> goodsToDB = new ArrayList<>();
                                        for (GoodDb good : orderTo.getOrderData().goodDbEntities) {
                                            GoodEntity tGoodEntity = new GoodEntity(good, orderTo.getOrderData().getOrderDb().sv_id);
                                            Log.d(EvoApp.TAG + "_good_db", tGoodEntity.toString());
                                            goodsToDB.add(tGoodEntity);

                                        }
                                        receiptWithGoodEntity.setGoodEntities(goodsToDB);
                                        EvoApp.getInstance().getDbHelper().insertReceiptWithGoods(receiptWithGoodEntity);
//                                    EvoApp.getInstance().getDbHelper().insertAllGood(goodsToDB);
                                        //todo Вынести в отдельный метод.

                                        EvoApp.getInstance().getDbHelper().insertReceiptToDb(dataToDb);
//                                    PrintUtil.getInstance().printOrder(getApplicationContext(), orderTo, printCallback);
                                    }

                                } else {
                                    Log.d(EvoApp.TAG, response.body().getErrorMessage() + " : -> Received error message");
                                }
                            } else {
                                Log.d(EvoApp.TAG, "Unexpected code: " + response.code());
                            }
                        }

                        @SuppressLint("LongLogTag")
                        @Override
                        public void onFailure(Call<NetworkAnswer> call, Throwable t) {
                            Log.d(EvoApp.TAG, "Network error: " + t.getMessage());
                        }
                    });
                }

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

    @SuppressLint("DefaultLocale")
    public static void updateNotificationCounter(/*int count*/) {
        synchronized (notification) {
            if (SystemStateApi.isSessionOpened(EvoApp.getInstance()) != null && SystemStateApi.isSessionOpened(EvoApp.getInstance())) {
                ForegroundServiceDispatcher.notification = builder
                        .setContentText("Фискализированно чеков за смену: " + SessionPresenter.getInstance().getSessionData().getCountReceipt()/*info*/).build();
            } else {
                ForegroundServiceDispatcher.notification = builder
                        .setContentText(String.format("Смена №%03d закрыта", SystemStateApi.getLastSessionNumber(EvoApp.getInstance()))).build();
            }

        }
        synchronized (manager) {
            manager.notify(1, notification);
        }
    }

    /*PrintUtil.PrintCallback printCallback = new PrintUtil.PrintCallback() {
        @Override
        public void printSuccess() {

        }

        @Override
        public void printFailure(PositionCreator.OrderTo.PositionTo order*//*List<Position> list, BigDecimal receiptCost*//*) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> PrintUtil.getInstance().printOrder(
                    getApplicationContext(),
                    order,
                    this), 1000);
        }
    };*/

}
