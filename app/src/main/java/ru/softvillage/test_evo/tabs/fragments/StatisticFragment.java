package ru.softvillage.test_evo.tabs.fragments;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.roomDb.Entity.SessionStatisticData;
import ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter;
import ru.softvillage.test_evo.tabs.viewModel.StatisticViewModel;

import static ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter.THEME_LIGHT;
import static ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter.getInstance;

public class StatisticFragment extends Fragment implements StatisticDisplayUpdate {
    private StatisticViewModel mViewModel;

    private ConstraintLayout statisticFragment;
    private ConstraintLayout sessionNumberHolder;
    private ConstraintLayout timeToCloseHolder;
    private ConstraintLayout sumFiscalizationHolder;
    private ConstraintLayout countReceiptHolder;
    private ConstraintLayout sendSmsHolder;
    private ConstraintLayout sendEmailHolder;

    private TextView statistic_session_number;
    private TextView title_statistic_information;
    private TextView time_to_close;
    private TextView sum_fiscalization;
    private TextView sum_receipt;
    private TextView send_sms;
    private TextView send_email;

    private TextView statistic_current_data;
    private TextView time_ticker_holder;
    private TextView sum;
    private TextView receipt_count;
    private TextView sms_count;
    private TextView email_count;

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
        getInstance().setIstatisticDisplayUpdate(this);

        statisticFragment = view.findViewById(R.id.statistic_fragment);
        sessionNumberHolder = view.findViewById(R.id.session_number_holder);
        timeToCloseHolder = view.findViewById(R.id.time_to_close_holder);
        sumFiscalizationHolder = view.findViewById(R.id.sum_fiscalization_holder);
        countReceiptHolder = view.findViewById(R.id.count_receipt_holder);
        sendSmsHolder = view.findViewById(R.id.send_sms_holder);
        sendEmailHolder = view.findViewById(R.id.send_email_holder);

        statistic_session_number = view.findViewById(R.id.statistic_session_number);
        title_statistic_information = view.findViewById(R.id.title_statistic_information);
        time_to_close = view.findViewById(R.id.time_to_close);
        sum_fiscalization = view.findViewById(R.id.sum_fiscalization);
        sum_receipt = view.findViewById(R.id.sum_receipt);
        send_sms = view.findViewById(R.id.send_sms);
        send_email = view.findViewById(R.id.send_email);

        statistic_current_data = view.findViewById(R.id.statistic_current_data);
        time_ticker_holder = view.findViewById(R.id.time_ticker_holder);
        sum = view.findViewById(R.id.sum);
        receipt_count = view.findViewById(R.id.receipt_count);
        sms_count = view.findViewById(R.id.sms_count);
        email_count = view.findViewById(R.id.email_count);


/**
 * Из класса статистики достаем все нреобходимые данные
 */
        getInstance().getSessionData().toString();

        initDateSession();
        updateView(SessionPresenter.getInstance().getSessionData());
        updateTheme();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        getInstance().setIstatisticDisplayUpdate(null);
        super.onDestroyView();
    }

    private void initDateSession() {
        if (getInstance().getDateLastOpenSession() != null) {
            /*startSession.setText(getInstance().getDateLastOpenSession().toString());*/
        }
        if (getInstance().getDateLastCloseSession() != null) {
//            endSession.setText(getInstance().getDateLastCloseSession().toString());
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(StatisticViewModel.class);
        // TODO: Use the ViewModel
    }


/*    @SuppressLint("LongLogTag")
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

    }*/

    @Override
    public void updateView(SessionStatisticData data) {
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            if (data.getSessionId() == -1) {
                statistic_session_number.setText("Смена закрыта");
            } else {
                statistic_session_number.setText(String.format(getActivity().getString(R.string.title_current_session), data.getSessionId()));
            }

            if (getInstance().getDateLastOpenSession() != null) {
                statistic_current_data.setText(getInstance().getDateLastOpenSession().toString("YYYY-MM-dd"));
                changeDateTimeColour();
            }

            sum.setText(String.format(getActivity().getString(R.string.template_rub_count), data.getSumFiscalization()));
            receipt_count.setText(String.format(getActivity().getString(R.string.template_count), data.getCountReceipt()));
            sms_count.setText(String.format(getActivity().getString(R.string.template_count), data.getSendSms()));
            email_count.setText(String.format(getActivity().getString(R.string.template_count), data.getSendEmail()));

            /*statisticContainer.setText(data.toString());*/
        });
    }

    @Override
    public void updateTheme() {
        int currentTheme = SessionPresenter.getInstance().getCurrentTheme();
        Drawable calendar_icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_statistic_calendar);

        if (currentTheme == THEME_LIGHT) {
            statisticFragment.setBackgroundColor(ContextCompat.getColor(statisticFragment.getContext(), R.color.color_f8));
            sessionNumberHolder.setBackgroundColor(ContextCompat.getColor(sessionNumberHolder.getContext(), R.color.white));
            timeToCloseHolder.setBackgroundColor(ContextCompat.getColor(timeToCloseHolder.getContext(), R.color.white));
            sumFiscalizationHolder.setBackgroundColor(ContextCompat.getColor(sumFiscalizationHolder.getContext(), R.color.white));
            countReceiptHolder.setBackgroundColor(ContextCompat.getColor(countReceiptHolder.getContext(), R.color.white));
            sendSmsHolder.setBackgroundColor(ContextCompat.getColor(sendSmsHolder.getContext(), R.color.white));
            sendEmailHolder.setBackgroundColor(ContextCompat.getColor(sendEmailHolder.getContext(), R.color.white));

            statistic_session_number.setTextColor(ContextCompat.getColor(statistic_session_number.getContext(), R.color.black));
            title_statistic_information.setTextColor(ContextCompat.getColor(title_statistic_information.getContext(), R.color.color29));
            time_to_close.setTextColor(ContextCompat.getColor(time_to_close.getContext(), R.color.black));
            sum_fiscalization.setTextColor(ContextCompat.getColor(sum_fiscalization.getContext(), R.color.black));
            sum_receipt.setTextColor(ContextCompat.getColor(sum_receipt.getContext(), R.color.black));
            send_sms.setTextColor(ContextCompat.getColor(send_sms.getContext(), R.color.black));
            send_email.setTextColor(ContextCompat.getColor(send_email.getContext(), R.color.black));

            changeDateTimeColour();
            int tabIconColor = ContextCompat.getColor(getContext(), R.color.color29);
            calendar_icon.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            statistic_current_data.setCompoundDrawablesRelativeWithIntrinsicBounds(calendar_icon, null, null, null);
            time_ticker_holder.setTextColor(ContextCompat.getColor(time_ticker_holder.getContext(), R.color.color29));
            sum.setTextColor(ContextCompat.getColor(sum.getContext(), R.color.color29));
            receipt_count.setTextColor(ContextCompat.getColor(receipt_count.getContext(), R.color.color29));
            sms_count.setTextColor(ContextCompat.getColor(sms_count.getContext(), R.color.color29));
            email_count.setTextColor(ContextCompat.getColor(email_count.getContext(), R.color.color29));
        } else {
            statisticFragment.setBackgroundColor(ContextCompat.getColor(statisticFragment.getContext(), R.color.black));
            sessionNumberHolder.setBackgroundColor(ContextCompat.getColor(sessionNumberHolder.getContext(), R.color.color31));
            timeToCloseHolder.setBackgroundColor(ContextCompat.getColor(timeToCloseHolder.getContext(), R.color.color31));
            sumFiscalizationHolder.setBackgroundColor(ContextCompat.getColor(sumFiscalizationHolder.getContext(), R.color.color31));
            countReceiptHolder.setBackgroundColor(ContextCompat.getColor(countReceiptHolder.getContext(), R.color.color31));
            sendSmsHolder.setBackgroundColor(ContextCompat.getColor(sendSmsHolder.getContext(), R.color.color31));
            sendEmailHolder.setBackgroundColor(ContextCompat.getColor(sendEmailHolder.getContext(), R.color.color31));

            statistic_session_number.setTextColor(ContextCompat.getColor(statistic_session_number.getContext(), R.color.white));
            title_statistic_information.setTextColor(ContextCompat.getColor(title_statistic_information.getContext(), R.color.color20));
            time_to_close.setTextColor(ContextCompat.getColor(time_to_close.getContext(), R.color.white));
            sum_fiscalization.setTextColor(ContextCompat.getColor(sum_fiscalization.getContext(), R.color.white));
            sum_receipt.setTextColor(ContextCompat.getColor(sum_receipt.getContext(), R.color.white));
            send_sms.setTextColor(ContextCompat.getColor(send_sms.getContext(), R.color.white));
            send_email.setTextColor(ContextCompat.getColor(send_email.getContext(), R.color.white));

            changeDateTimeColour();
            int tabIconColor = ContextCompat.getColor(getContext(), R.color.color20);
            calendar_icon.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            statistic_current_data.setCompoundDrawablesRelativeWithIntrinsicBounds(calendar_icon, null, null, null);
            time_ticker_holder.setTextColor(ContextCompat.getColor(time_ticker_holder.getContext(), R.color.color20));
            sum.setTextColor(ContextCompat.getColor(sum.getContext(), R.color.color20));
            receipt_count.setTextColor(ContextCompat.getColor(receipt_count.getContext(), R.color.color20));
            sms_count.setTextColor(ContextCompat.getColor(sms_count.getContext(), R.color.color20));
            email_count.setTextColor(ContextCompat.getColor(email_count.getContext(), R.color.color20));
        }
    }

    private void changeDateTimeColour() {
        if (getInstance().getSessionData().getSessionId() == -1) {
            statistic_current_data.setTextColor(ContextCompat.getColor(statistic_current_data.getContext(), R.color.color17));
        } else {
            if (getInstance().getCurrentTheme() == THEME_LIGHT) {
                statistic_current_data.setTextColor(ContextCompat.getColor(statistic_current_data.getContext(), R.color.color29));
            } else {
                statistic_current_data.setTextColor(ContextCompat.getColor(statistic_current_data.getContext(), R.color.color20));
            }
        }

    }
}