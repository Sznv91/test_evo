package ru.softvillage.fiscalizer.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.joda.time.LocalDateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.evotor.framework.component.PaymentPerformer;
import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintReceiptCommandResult;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintSellReceiptCommand;
import ru.evotor.framework.kkt.api.KktApi;
import ru.evotor.framework.payment.PaymentSystem;
import ru.evotor.framework.payment.PaymentType;
import ru.evotor.framework.receipt.FiscalReceipt;
import ru.evotor.framework.receipt.Payment;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.PrintGroup;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.system.SystemStateApi;
import ru.evotor.framework.users.UserApi;
import ru.evotor.query.Cursor;
import ru.softvillage.fiscalizer.EvoApp;
import ru.softvillage.fiscalizer.network.entity.FiscalizedAnswer;
import ru.softvillage.fiscalizer.network.entity.NetworkAnswer;
import ru.softvillage.fiscalizer.roomDb.Entity.fiscalized.PartialReceiptPrinted;
import ru.softvillage.fiscalizer.services.ForegroundServiceDispatcher;
import ru.softvillage.fiscalizer.tabs.left_menu.DrawerMenuManager;
import ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter;

public class PrintUtil {
    private static PrintUtil instance;
    private static final String EMAIL_STAB = "stab@sv.ru";

    private PrintUtil() {
    }

    public static PrintUtil getInstance() {
        if (instance == null) {
            instance = new PrintUtil();
        }
        return instance;
    }

    @SuppressLint("LongLogTag")
    public void printOrder(Context context, PositionCreator.OrderTo.PositionTo order, PrintCallback callback) {
        boolean shouldPrintReceipt = SessionPresenter.getInstance().isPrintChecks();

        //Добавление скидки на чек
        BigDecimal receiptDiscount = BigDecimal.ZERO;
        if (!order.getOrderData().orderDb.checkDiscount.equals(BigDecimal.ZERO)) {
            Log.d(EvoApp.TAG + "_discount_", "Сумма чека без скидок: " + order.getSumPrice());
            BigDecimal onePercentFromAllPrice = order.getSumPrice().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            Log.d(EvoApp.TAG + "_discount_", "Цена одного процента от общей стоимости: " + onePercentFromAllPrice);
            Log.d(EvoApp.TAG + "_discount_", "Процент скидки: " + order.getOrderData().orderDb.checkDiscount);
            receiptDiscount = onePercentFromAllPrice.multiply(order.getOrderData().orderDb.checkDiscount);
            Log.d(EvoApp.TAG + "_discount_", "Размер скидки: " + receiptDiscount);
        }

        BigDecimal finalCost = order.getSumPrice().subtract(receiptDiscount);
        //Способ оплаты
        Map<Payment, BigDecimal> payments = new HashMap<>();
        payments.put(new Payment(
                UUID.randomUUID().toString(),
                finalCost,
                null,
                new PaymentPerformer(
                        new PaymentSystem(PaymentType.ELECTRON, "Internet", "12424"),
                        "имя пакета",
                        "название компонента",
                        "app_uuid",
                        "appName"
                ),
                null,
                null,
                null
        ), finalCost);
        PrintGroup printGroup = new PrintGroup(
                UUID.randomUUID().toString(),
                PrintGroup.Type.CASH_RECEIPT,
                null,
                null,
                null,
                null,
                shouldPrintReceipt, //true
                null,
                null);
        Receipt.PrintReceipt printReceipt = new Receipt.PrintReceipt(
                printGroup,
                order.getPositions(),
                payments,
                new HashMap<Payment, BigDecimal>(), new HashMap<String, BigDecimal>()
        );

        ArrayList<Receipt.PrintReceipt> listDocs = new ArrayList<>();
        listDocs.add(printReceipt);

        /**
         * Если любой переключатель (Evotor/SW) отправки СМС/EMAIL активен, то достаем из полученного по сети запроса эти данные.
         * Далее проверяем, если EVOTOR то оставляем переменные как есть, если SW то приравниваем к null, для того чтобы при фискализации
         * не передавать эти данные. После окончания фискализации:
         * SW - передаем сведения что необходимо выполнить отправку SMS/EMAIL.
         * Evotor - ничего не передаем.
         */
        String recipientPhone = SessionPresenter.getInstance().isSendSms() ? order.getOrderData().orderDb.phone : null;
        String recipientEmail = SessionPresenter.getInstance().isSendEmail() ? order.getOrderData().orderDb.email : EMAIL_STAB;
        if (SessionPresenter.getInstance().getDefaultSmsService().equals(DrawerMenuManager.SOFT_VILLAGE_SERVICE)) {
            recipientPhone = null;
        }
        if (SessionPresenter.getInstance().getDefaultEmailService().equals(DrawerMenuManager.SOFT_VILLAGE_SERVICE)) {
            recipientEmail = EMAIL_STAB;
        }

        /**
         * Проверка на случаей если выбрана отправка чека, но при формированнии чека не заданы реквизиты получателя чека.
         */
        if (TextUtils.isEmpty(recipientPhone) && TextUtils.isEmpty(recipientEmail)){
            recipientEmail = EMAIL_STAB;
        }


        new PrintSellReceiptCommand(listDocs,
                null,
                recipientPhone,
                recipientEmail,
                receiptDiscount,
                null,
                null,
                order.getOrderData().orderDb.userUUID).process(context, new IntegrationManagerCallback() {
            @SuppressLint("LongLogTag")
            @Override
            public void run(IntegrationManagerFuture integrationManagerFuture) {
                try {
                    IntegrationManagerFuture.Result result = integrationManagerFuture.getResult();
                    switch (result.getType()) {
                        case OK:
                            PrintReceiptCommandResult printSellReceiptResult = PrintReceiptCommandResult.create(result.getData());
                            Toast.makeText(context, "OK", Toast.LENGTH_LONG).show();

                            PartialReceiptPrinted dataToDb = new PartialReceiptPrinted();
                            dataToDb.setPrinted(LocalDateTime.now());
                            dataToDb.setReceiptNumber(Long.parseLong(result.getData().getString("receiptNumber")));
                            dataToDb.setUuid(result.getData().getString("receiptUuid"));
                            dataToDb.setSv_id(order.getOrderData().orderDb.sv_id);
                            dataToDb.setSessionId(SystemStateApi.getLastSessionNumber(context));
                            if (order.getOrderData().orderDb.getUserUUID() != null) {
                                dataToDb.setUserUuid(order.getOrderData().orderDb.getUserUUID());
                            } else {
                                dataToDb.setUserUuid(UserApi.getAuthenticatedUser(EvoApp.getInstance()).getUuid());
                            }

                            dataToDb.setRn_kkt(KktApi.receiveKktRegNumber(context));
                            dataToDb.setZn_kkt(KktApi.receiveKktSerialNumber(context));
                            dataToDb.setInn(SessionPresenter.getInstance().getOrg_inn());
                            dataToDb.setSno_type(SessionPresenter.getInstance().getSno_type());
                            dataToDb.setShop_name(SessionPresenter.getInstance().getShop_name());
                            dataToDb.setAddress(SessionPresenter.getInstance().getAddress());
                            dataToDb.setPayment_place(SessionPresenter.getInstance().getPayment_place());
                            EvoApp.getInstance().getDbHelper().updateReceipt(dataToDb);

                            /**
                             * Отправка ответа на сервер
                             */
                            FiscalizedAnswer answer = new FiscalizedAnswer();
                            answer.setSmsFlag(SessionPresenter.getInstance().getDefaultSmsService().equals(DrawerMenuManager.SOFT_VILLAGE_SERVICE) && SessionPresenter.getInstance().isSendSms());
                            answer.setEmailFlag(SessionPresenter.getInstance().getDefaultEmailService().equals(DrawerMenuManager.SOFT_VILLAGE_SERVICE) && SessionPresenter.getInstance().isSendEmail());
                            answer.setId(order.getOrderData().orderDb.sv_id);
                            answer.setNumber(Long.parseLong(result.getData().getString("receiptNumber")));
                            answer.setUuid(result.getData().getString("receiptUuid"));
                            answer.setStatus(1);

                            Cursor<FiscalReceipt> fiscalReceiptCursor = ReceiptApi.getFiscalReceipts(EvoApp.getInstance(), result.getData().getString("receiptUuid"));
                            while (fiscalReceiptCursor.moveToNext()) {
                                FiscalReceipt fiscalReceipt = fiscalReceiptCursor.getValue();
                                answer.setDocumentNumber(fiscalReceipt.getDocumentNumber());
                                answer.setFiscalIdentifier(fiscalReceipt.getFiscalIdentifier());
                                answer.setFsSerialNumber(fiscalReceipt.getFiscalStorageNumber());
                            }
                            fiscalReceiptCursor.close();

                            EvoApp.getInstance().getOrderInterface().postUpdateReceipt(answer).enqueue(new Callback<NetworkAnswer>() {
                                @Override
                                public void onResponse(Call<NetworkAnswer> call, Response<NetworkAnswer> response) {

                                }

                                @Override
                                public void onFailure(Call<NetworkAnswer> call, Throwable t) {

                                }
                            });


                            /**
                             * Обновление блока статистики
                             */
                            StatisticConsider.addCountReceipt();
                            StatisticConsider.addFiscalizedMoney(finalCost);
                            if (SessionPresenter.getInstance().isSendSms() && !TextUtils.isEmpty(order.getOrderData().orderDb.getPhone())) {
                                StatisticConsider.addCountSms();
                            }
                            if (SessionPresenter.getInstance().isSendEmail() && !TextUtils.isEmpty(order.getOrderData().orderDb.getEmail())) {
                                StatisticConsider.addCountEmail();
                            }
                            /**
                             * Обновление оповещения
                             */
                            ForegroundServiceDispatcher.updateNotificationCounter();
                            callback.printSuccess();
                            break;
                        case ERROR:
                            //todo отловить причину истечения срока сессии -> закрыть сессию по условию.
                            Log.d(EvoApp.TAG + "_print_error", result.getError().getMessage());
                            callback.printFailure(order, result.getError().getMessage() + " Code:" + result.getError().getCode() + " Data: " + result.getError().getData());
//                            Toast.makeText(context, result.getError().getMessage(), Toast.LENGTH_LONG).show();
                            break;
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void printDemoOrder(Context context) {

        //Создание списка товаров чека
        List<Position> list = new ArrayList<>();
        list.add(
                Position.Builder.newInstance(
                        //UUID позиции
                        UUID.randomUUID().toString(),
                        //UUID товара
                        null,
                        //Наименование
                        "Имя товара",
                        //Наименование единицы измерения
                        "шт",
                        //Точность единицы измерения
                        0,
                        //Цена без скидок
                        new BigDecimal(1000),
                        //Количество
                        BigDecimal.TEN
                ).build()
        );
        list.add(
                Position.Builder.newInstance(
                        UUID.randomUUID().toString(),
                        null,
                        "1234",
                        "12",
                        0,
                        new BigDecimal(500),
                        BigDecimal.ONE)
                        //Добавление цены с учетом скидки на позицию. Итог = price - priceWithDiscountPosition
                        .setPriceWithDiscountPosition(new BigDecimal(300)).build()
        );
        //Способ оплаты
        HashMap payments = new HashMap<Payment, BigDecimal>();
        payments.put(new Payment(
                UUID.randomUUID().toString(),
                new BigDecimal(9300),
                null,
                new PaymentPerformer(
                        new PaymentSystem(PaymentType.ELECTRON, "Internet", "12424"),
                        "имя пакета",
                        "название компонента",
                        "app_uuid",
                        "appName"
                ),
                null,
                null,
                null
        ), new BigDecimal(9300));
        PrintGroup printGroup = new PrintGroup(
                UUID.randomUUID().toString(),
                PrintGroup.Type.CASH_RECEIPT,
                null,
                null,
                null,
                null,
                true,
                null,
                null);
        Receipt.PrintReceipt printReceipt = new Receipt.PrintReceipt(
                printGroup,
                list,
                payments,
                new HashMap<Payment, BigDecimal>(), new HashMap<String, BigDecimal>()
        );

        ArrayList<Receipt.PrintReceipt> listDocs = new ArrayList<>();
        listDocs.add(printReceipt);
        //Добавление скидки на чек
        BigDecimal receiptDiscount = new BigDecimal(1000);
        new PrintSellReceiptCommand(listDocs,
                null,
                "79011234567",
                "example@example.com",
                receiptDiscount,
                null,
                null,
                null).process(context, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture integrationManagerFuture) {
                try {
                    IntegrationManagerFuture.Result result = integrationManagerFuture.getResult();
                    switch (result.getType()) {
                        case OK:
                            PrintReceiptCommandResult printSellReceiptResult = PrintReceiptCommandResult.create(result.getData());
                            Toast.makeText(context, "OK", Toast.LENGTH_LONG).show();
                            break;
                        case ERROR:
                            Toast.makeText(context, result.getError().getMessage(), Toast.LENGTH_LONG).show();
                            break;
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public interface PrintCallback {
        void printSuccess();

        void printFailure(PositionCreator.OrderTo.PositionTo order, String error /*List<Position> list, BigDecimal receiptCost*/);
    }
}
