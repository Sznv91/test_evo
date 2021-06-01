package ru.softvillage.test_evo.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.evotor.devices.commons.ConnectionWrapper;
import ru.evotor.devices.commons.DeviceServiceConnector;
import ru.evotor.devices.commons.exception.DeviceServiceException;
import ru.evotor.devices.commons.printer.PrinterDocument;
import ru.evotor.devices.commons.printer.printable.PrintableText;
import ru.evotor.devices.commons.services.IPrinterServiceWrapper;
import ru.evotor.devices.commons.services.IScalesServiceWrapper;
import ru.evotor.framework.users.UserApi;
import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.network.entity.FiscalizationRequest;
import ru.softvillage.test_evo.roomDb.DbHelper;
import ru.softvillage.test_evo.roomDb.Entity.fromNetwork.OrderDbWithGoods;
import ru.softvillage.test_evo.utils.PositionCreator;
import ru.softvillage.test_evo.utils.PrintUtil;

public class PrintDispatcher extends Service {
    /**
     * Многопоточный ArrayList
     */
    private final CopyOnWriteArrayList<OrderDbWithGoods> receiptQueue = new CopyOnWriteArrayList<>();
    private static final AtomicBoolean printerAccess = new AtomicBoolean();
    private static final int LATENCY_UPDATE_QUEUE = 30000;
    private static final int LATENCY_PRINT = 5000;
    private static final int RETRY_AFTER_FAILED = 50000;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate() {
        super.onCreate();

       /* *//**
         * Инициализация принтера
         *//*
        DeviceServiceConnector.startInitConnections(getApplicationContext());
        DeviceServiceConnector.addConnectionWrapper(new ConnectionWrapper() {
            @Override
            public void onPrinterServiceConnected(IPrinterServiceWrapper printerService) {
                Log.e(getClass().getSimpleName(), "onPrinterServiceConnected");
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            //Печать сообщения об успешной инициализации принтера
                            DeviceServiceConnector.getPrinterService().printDocument(
                                    //В настоящий момент печать возможна только на ККМ, встроенной в смарт-терминал,
                                    //поэтому вместо номера устройства всегда следует передавать константу
                                    ru.evotor.devices.commons.Constants.DEFAULT_DEVICE_INDEX,
                                    new PrinterDocument(
                                            new PrintableText("PRINTER INIT OK")));
                        } catch (DeviceServiceException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();
            }

            @Override
            public void onPrinterServiceDisconnected() {
                Log.e(getClass().getSimpleName(), "onPrinterServiceDisconnected");
            }

            @Override
            public void onScalesServiceConnected(IScalesServiceWrapper scalesService) {
                Log.e(getClass().getSimpleName(), "onScalesServiceConnected");
            }

            @Override
            public void onScalesServiceDisconnected() {
                Log.e(getClass().getSimpleName(), "onScalesServiceDisconnected");
            }
        });
*/


        /**
         * При запуске сервиса предпологаем что принтер свободен.
         */
        printerAccess.set(true);

        /**
         * Поток для обновления очереди печати
         */
        new Thread(() -> {
            while (true) {
                /**
                 * ArrayList нефискализированных чеков полученный из локальной БД
                 */

                List<OrderDbWithGoods> notFiscalizedFromDb = EvoApp.getInstance().getDbHelper().getOrderDbWithGoods();
//                Log.d(EvoApp.TAG + "_" + getClass().getSimpleName() + "_Thread_Queue_updater", "Успешно извлекли из БД: " + notFiscalizedFromDb.toString());
                for (OrderDbWithGoods entity : notFiscalizedFromDb) {
                    if (!receiptQueue.contains(entity)) {
                        Log.d(EvoApp.TAG + "_" + getClass().getSimpleName() + "_Thread_Queue_updater", "Добавили в очередь печати: " + entity.toString());
                        receiptQueue.add(entity);
                    }
                }

                try {
                    Thread.sleep(LATENCY_UPDATE_QUEUE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        /**
         * Поток для запуска печати
         */
        new Thread(() -> {
            while (true) {
                if (UserApi.getAuthenticatedUser(getApplicationContext()) != null) {
                    if (receiptQueue.size() > 0) {
                        OrderDbWithGoods entity = receiptQueue.get(0);
                        EvoApp.getInstance().getOrderInterface().postIsNeedPrint(entity.getOrderDb().getSv_id()).enqueue(new Callback<FiscalizationRequest>() {
                            @Override
                            public void onResponse(Call<FiscalizationRequest> call, Response<FiscalizationRequest> response) {
                                Log.d(EvoApp.TAG + "_" + getClass().getSimpleName() + "_Thread_Queue_networker", "Получили ответ из сети. Entity id: " + entity.getOrderDb().getSv_id() + " результат: " + response.body().isNeedPrint());
                                if (!response.body().isNeedPrint()) {
                                    receiptQueue.remove(entity);
                                    EvoApp.getInstance().getDbHelper().removeOrderDbWithGoods(entity.getOrderDb(), null);
                                    return;
                                } else {
                                    if (printerAccess.get()) {
                                        PositionCreator.OrderTo.PositionTo toProcessing = PositionCreator.makeSinglePositionTo(entity);
                                        printerAccess.set(false);
                                        PrintUtil.getInstance().printOrder(getApplicationContext(), toProcessing, new PrintUtil.PrintCallback() {
                                            @Override
                                            public void printSuccess() {
                                                Log.d(EvoApp.TAG + "_" + getClass().getSimpleName() + "_Thread_Queue_printSuccess", "Получили положительный коллбек о печати чека ");
                                                receiptQueue.remove(entity);
                                                EvoApp.getInstance().getDbHelper().removeOrderDbWithGoods(entity.getOrderDb(), () -> printerAccess.set(true));
                                            }

                                            @Override
                                            public void printFailure(PositionCreator.OrderTo.PositionTo order, String errorMessage) {
                                                Log.d(EvoApp.TAG + "_" + getClass().getSimpleName() + "_Thread_Queue_printFailure", "Получили Отрицательный коллбек о печати чека errorMessage: " + errorMessage);
                                                Handler handler = new Handler(Looper.getMainLooper());
                                                handler.postDelayed(() -> printerAccess.set(true), RETRY_AFTER_FAILED);
                                            }
                                        });
                                    }

                                }
                            }

                            @Override
                            public void onFailure(Call<FiscalizationRequest> call, Throwable t) {
                                Log.d(EvoApp.TAG + "_" + getClass().getSimpleName() + "_Thread_Queue_networker", "Ошибка обращения к сети " + t.getMessage());
                            }
                        });
                    }
                }
                try {
                    Thread.sleep(LATENCY_PRINT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
