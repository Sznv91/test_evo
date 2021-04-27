package ru.softvillage.test_evo.tabs.fragments;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.action.command.print_z_report_command.PrintZReportCommand;
import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.network.entity.Order;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptEntity;
import ru.softvillage.test_evo.services.ForegroundServiceDispatcher;
import ru.softvillage.test_evo.tabs.viewModel.StatisticViewModel;
import ru.softvillage.test_evo.utils.PositionCreator;
import ru.softvillage.test_evo.utils.PrintUtil;

public class StatisticFragment extends Fragment {

    private StatisticViewModel mViewModel;
    private PrintUtil printUtil;
    TextInputEditText editText;
    private EditText dateField;

    public static StatisticFragment newInstance() {
        return new StatisticFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.statistic_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ((Button) view.findViewById(R.id.button_start_service)).setOnClickListener(v -> startForegroundService());
        ((Button) view.findViewById(R.id.print_example)).setOnClickListener(v -> printExample());
        ((Button) view.findViewById(R.id.print_calculate_recipient)).setOnClickListener(v -> printOrder());
        ((Button) view.findViewById(R.id.add_fake_receipt)).setOnClickListener(v -> addFakeReceipt());
        ((Button) view.findViewById(R.id.close_session)).setOnClickListener(v -> closeSession());
        editText = view.findViewById(R.id.discount_all_order);

        dateField = getView().findViewById(R.id.edit_text_add_fake_data);
        dateField.setText(LocalDate.now().toString());

        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("LongLogTag")
    private void closeSession() {
        new PrintZReportCommand().process(getContext(), future -> {
            try {
                Log.d(EvoApp.TAG + "_z_report", future.getResult().getData().toString());
            } catch (IntegrationException e) {
                e.printStackTrace();
            }
        });
        //SessionClosedEvent close = new ru.evotor.framework.core.action.event.session.SessionClosedEvent();

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        printUtil = PrintUtil.getInstance();
        mViewModel = new ViewModelProvider(this).get(StatisticViewModel.class);
        // TODO: Use the ViewModel
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void startForegroundService() {
        if (isMyServiceRunning(ForegroundServiceDispatcher.class)) {
            Toast.makeText(getActivity().getApplicationContext(), "Service already running", Toast.LENGTH_LONG).show();
            return;
        }
        Intent startIntent = new Intent(getActivity(), ForegroundServiceDispatcher.class);
        startIntent.setAction("start");
        getActivity().startService(startIntent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    //////////////////////////

    @SuppressLint("LongLogTag")
    private void printOrder() {
        BigDecimal allDiscount = new BigDecimal(editText.getText().toString());
//        String string = "[ { \"id\": 2660, \"email\": \"89508543356@mail.ru\", \"phone\": \"89508543356\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 44028)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 11111)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 22222)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за поддержку СКТВ (Л/С 44028)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 50, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] }, { \"id\": 2661, \"email\": \"89281246694@mail.ru\", \"phone\": \"89281246694\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 44597)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за поддержку СКТВ (Л/С 44597)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 50, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] }, { \"id\": 2662, \"email\": \"89054282494@mail.ru\", \"phone\": \"89054282494\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 43668)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за поддержку СКТВ (Л/С 43668)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 50, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] }, { \"id\": 2663, \"email\": \"89528508056@mail.ru\", \"phone\": \"89528508056\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 41581)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 11112)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 22221)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 11113)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 1)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 2)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 3)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 1)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 2)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 3)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 1)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 2)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 3)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 22224)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за поддержку СКТВ (Л/С 41581)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 50, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] }, { \"id\": 2664, \"email\": \"41245@multi-m.ru\", \"phone\": \"\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата за услуги СКТВ (Л/С 41245)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 87.5, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата за поддержку СКТВ (Л/С 41245)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 62.5, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] }, { \"id\": 2665, \"email\": \"89515034974@mail.ru\", \"phone\": \"89515034974\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 42333)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 1)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 2)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 3)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 1)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 2)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 3)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за поддержку СКТВ (Л/С 42333)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 50, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] }, { \"id\": 2666, \"email\": \"89185844276@mail.ru\", \"phone\": \"89185844276\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 43662)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за поддержку СКТВ (Л/С 43662)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 50, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] }, { \"id\": 2667, \"email\": \"42437@multi-m.ru\", \"phone\": \"\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 42437)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 1)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 2)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 3)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за поддержку СКТВ (Л/С 42437)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 50, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] }, { \"id\": 2668, \"email\": \"89508538081@mail.ru\", \"phone\": \"89508538081\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 41328)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 70, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за поддержку СКТВ (Л/С 41328)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 50, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] }, { \"id\": 2669, \"email\": \"89081811301@mail.ru\", \"phone\": \"89081811301\", \"userUUID\": null, \"paymant_system\": 3, \"check_discount\": 0, \"goods\": [ { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 43420)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 1)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 2)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за услуги СКТВ (Л/С 3)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 100, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } }, { \"productUUID\": null, \"name\": \"Оплата долга за поддержку СКТВ (Л/С 43420)\", \"measure_name\": \"кг.\", \"measure_precision\": 2, \"price\": 50, \"discount\": 0, \"quantity\": 1, \"nds\": -1, \"type\": { \"number\": 1, \"mark_info\": null } } ] } ]";
        String string = "[{\"id\":2660,\"email\":\"89508543356@mail.ru\",\"phone\":\"89508543356\",\"userUUID\":null,\"paymant_system\":3,\"check_discount\":" + allDiscount.toString() + ",\"goods\":[{\"productUUID\":null,\"name\":\"Оплата долга за услуги СКТВ (Л/С 44028)\",\"measure_name\":\"\\u043a\\u0433.\",\"measure_precision\":2,\"price\":103.67,\"discount\":17,\"quantity\":5,\"nds\":-1,\"type\":{\"number\":1,\"mark_info\":null}},{\"productUUID\":null,\"name\":\"Оплата долга за поддержку СКТВ (Л/С 44028)\",\"measure_name\":\"\\u043a\\u0433.\",\"measure_precision\":2,\"price\":1239.19,\"discount\":13,\"quantity\":57,\"nds\":-1,\"type\":{\"number\":1,\"mark_info\":null}}]}]";

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
            public void printFailure(PositionCreator.OrderTo.PositionTo order) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    PrintUtil.getInstance().printOrder(
                            getContext().getApplicationContext(),
                            order,
                            this
                    );
                }, 1000);
            }
        };

        for (PositionCreator.OrderTo.PositionTo order : toProcessing.getOrderList()) {
            String toLog = new Gson().toJson(order);
            Log.d(EvoApp.TAG + "_GSON", toLog);
            PrintUtil.getInstance().printOrder(
                    getContext().getApplicationContext(),
                    order,
                    printCallback
            );
//            Log.d(EvoApp.TAG + "_Handler", String.format("List: %s", entry.getKey()));
        }
    }

    /////////////////////////
    private void printExample() {
        printUtil.printDemoOrder(getContext().getApplicationContext());
    }

    private void addFakeReceipt() {
        LocalDateTime localDateTime = LocalDateTime.parse(dateField.getText().toString() + "T00:00:00");
        ReceiptEntity entity = new ReceiptEntity();
        entity.setId(Long.parseLong(LocalDateTime.now().toString("yyyyMMddHHssSSSS")));
        entity.setReceived(localDateTime);
        entity.setPrice(BigDecimal.valueOf(123.45));
        entity.setCountOfPosition(6);
        entity.setUuid(UUID.randomUUID().toString());

        new Thread(() -> {
            EvoApp.getInstance().getDbHelper().insertReceiptToDb(entity);
        }).start();

    }

}