package ru.softvillage.test_evo.utils;

import android.content.Context;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ru.evotor.framework.component.PaymentPerformer;
import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintReceiptCommandResult;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintSellReceiptCommand;
import ru.evotor.framework.payment.PaymentSystem;
import ru.evotor.framework.payment.PaymentType;
import ru.evotor.framework.receipt.Payment;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.PrintGroup;
import ru.evotor.framework.receipt.Receipt;

public class PrintUtil {
    private static PrintUtil instance;

    private PrintUtil() {
    }

    public static PrintUtil getInstance() {
        if (instance == null) {
            instance = new PrintUtil();
        }
        return instance;
    }

    public void printOrder(Context context, PositionCreator.OrderTo.PositionTo order, PrintCallback callback) {

        //Добавление скидки на чек
        BigDecimal receiptDiscount = BigDecimal.ZERO;
        if (!order.getOrderData().checkDiscount.equals(BigDecimal.ZERO)) {
            receiptDiscount = order.getSumPrice().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(order.getOrderData().checkDiscount);
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
                true,
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

        new PrintSellReceiptCommand(listDocs,
                null,
//                "79011234567",
                order.getOrderData().phone,
//                "example@example.com",
                order.getOrderData().email,
                receiptDiscount,
                null,
                null,
                order.getOrderData().userUUID).process(context, new IntegrationManagerCallback() {
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
                            callback.printFailure(order);
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

        void printFailure(PositionCreator.OrderTo.PositionTo order /*List<Position> list, BigDecimal receiptCost*/);
    }
}
