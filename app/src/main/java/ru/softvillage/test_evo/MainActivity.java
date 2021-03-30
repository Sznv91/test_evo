package ru.softvillage.test_evo;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.evotor.framework.receipt.Position;
import ru.softvillage.test_evo.network.entity.NetworkAnswer;
import ru.softvillage.test_evo.network.entity.Order;
import ru.softvillage.test_evo.services.ForegroundServiceDispatcher;
import ru.softvillage.test_evo.utils.PositionCreator;
import ru.softvillage.test_evo.utils.PrintUtil;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Callback<NetworkAnswer> callback = new Callback<NetworkAnswer>() {
            @Override
            public void onResponse(Call<NetworkAnswer> call, Response<NetworkAnswer> response) {
                if (response.code() == 200) {
                    Log.d(EvoApp.TAG, "Received body: " + response.body());
                    if (response.body().getSuccess()) {
                        PositionCreator.OrderTo toProcessing = PositionCreator.makeOrderList(response.body().getOrderList());
                        for (List<Position> order : toProcessing.getPositions().keySet()) {
//                            PrintUtil.getInstance().printOrder(getApplicationContext(), order, toProcessing.getPositions().get(order));
                        }
                    }
                } else {
                    Log.d(EvoApp.TAG, "Unexpected HTTP Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<NetworkAnswer> call, Throwable t) {
                Log.d(EvoApp.TAG, "Error Message:" + t.getMessage());
            }
        };

        Button orderButton = findViewById(R.id.sell_button);
        orderButton.setOnClickListener(v -> PrintUtil.getInstance().printDemoOrder(getApplicationContext()));

        Button networkSuccessButton = findViewById(R.id.network_success_button);
        Button networkFailButton = findViewById(R.id.network_fail_button);

        networkSuccessButton.setOnClickListener(v -> EvoApp.getInstance().getOrderInterface().getOrder().enqueue(callback));
        networkFailButton.setOnClickListener(v -> EvoApp.getInstance().getOrderInterface().getFalse().enqueue(callback));


        Button preparePrintBut = findViewById(R.id.sell_prepare_button);
        preparePrintBut.setOnClickListener(v -> printOrder());

        Button startService = findViewById(R.id.start_service_button);
        startService.setOnClickListener(v -> startForegroundService());


    }

    @SuppressLint("LongLogTag")
    private void printOrder() {
        String string = "[ { \"id\": 2660, \"email\": \"89508543356@mail.ru\", \"phone\": \"89508543356\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 44028)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 11111)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 22222)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за поддержку СКТВ (Л/С 44028)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 50, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] }, { \"id\": 2661, \"email\": \"89281246694@mail.ru\", \"phone\": \"89281246694\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 44597)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за поддержку СКТВ (Л/С 44597)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 50, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] }, { \"id\": 2662, \"email\": \"89054282494@mail.ru\", \"phone\": \"89054282494\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 43668)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за поддержку СКТВ (Л/С 43668)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 50, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] }, { \"id\": 2663, \"email\": \"89528508056@mail.ru\", \"phone\": \"89528508056\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 41581)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 11112)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 22221)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 11113)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 1)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 2)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 3)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 1)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 2)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 3)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 1)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 2)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 3)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 22224)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за поддержку СКТВ (Л/С 41581)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 50, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] }, { \"id\": 2664, \"email\": \"41245@multi-m.ru\", \"phone\": \"\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата за услуги СКТВ (Л/С 41245)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 87.5, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата за поддержку СКТВ (Л/С 41245)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 62.5, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] }, { \"id\": 2665, \"email\": \"89515034974@mail.ru\", \"phone\": \"89515034974\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 42333)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 1)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 2)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 3)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 1)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 2)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 3)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за поддержку СКТВ (Л/С 42333)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 50, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] }, { \"id\": 2666, \"email\": \"89185844276@mail.ru\", \"phone\": \"89185844276\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 43662)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за поддержку СКТВ (Л/С 43662)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 50, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] }, { \"id\": 2667, \"email\": \"42437@multi-m.ru\", \"phone\": \"\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 42437)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 1)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 2)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 3)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за поддержку СКТВ (Л/С 42437)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 50, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] }, { \"id\": 2668, \"email\": \"89508538081@mail.ru\", \"phone\": \"89508538081\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 41328)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 70, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за поддержку СКТВ (Л/С 41328)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 50, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] }, { \"id\": 2669, \"email\": \"89081811301@mail.ru\", \"phone\": \"89081811301\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 43420)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 1)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 2)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 3)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за поддержку СКТВ (Л/С 43420)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 50, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] } ]";

        java.lang.reflect.Type listType = new TypeToken<ArrayList<Order>>() {
        }.getType();
        List<Order> fromJsonString = new Gson().fromJson(string, listType);
        PositionCreator.OrderTo toProcessing = PositionCreator.makeOrderList(fromJsonString);
        Log.d(EvoApp.TAG, "FROM JSON STRING: " + fromJsonString.toString());

        PrintUtil.PrintCallback printCallback = new PrintUtil.PrintCallback() {
            @Override
            public void printSuccess() {

            }

            @Override
            public void printFailure(List<Position> list, BigDecimal receiptCost) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> PrintUtil.getInstance().printOrder(
                        getApplicationContext(),
                        list,
                        receiptCost,
                        this), 1000);
            }
        };

        for (Map.Entry<List<Position>, BigDecimal> entry : toProcessing.getPositions().entrySet()) {
            PrintUtil.getInstance().printOrder(
                    getApplicationContext(),
                    entry.getKey(),
                    entry.getValue(),
                    printCallback
            );
            Log.d(EvoApp.TAG + "_Handler", String.format("List: %s", entry.getKey()));
        }
    }

    private void startForegroundService() {
        if (isMyServiceRunning(ForegroundServiceDispatcher.class)) {
            Toast.makeText(getApplicationContext(), "Service already running", Toast.LENGTH_LONG).show();
            return;
        }
        Intent startIntent = new Intent(this, ForegroundServiceDispatcher.class);
        startIntent.setAction("start");
        startService(startIntent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

