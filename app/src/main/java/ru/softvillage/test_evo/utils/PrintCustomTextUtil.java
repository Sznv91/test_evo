package ru.softvillage.test_evo.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ru.evotor.devices.commons.DeviceServiceConnector;
import ru.evotor.devices.commons.exception.DeviceServiceException;
import ru.evotor.devices.commons.printer.PrinterDocument;
import ru.evotor.devices.commons.printer.printable.IPrintable;
import ru.evotor.devices.commons.printer.printable.PrintableText;
import ru.softvillage.test_evo.roomDb.Entity.SessionStatisticData;

public class PrintCustomTextUtil {

    public static void printStatistic(SessionStatisticData statisticData, Context context) {
        final List<IPrintable> pList = new ArrayList<>();
        pList.add(new PrintableText("Статистика использования Фискализатора Soft-Village(www.soft-village.ru):"));
        pList.add(new PrintableText("№ Смены: " + statisticData.getSessionId()));
        pList.add(new PrintableText("Общ. сумма фискализации: " + statisticData.getSumFiscalization().toPlainString() + "руб."));
        pList.add(new PrintableText("Кол-во фискализированных чеков: " + statisticData.getCountReceipt()));
        pList.add(new PrintableText("Отправлено SMS/Email: " + statisticData.getSendSms() + "/" + statisticData.getSendEmail()));
        printText(pList, context);
        /*final List<IPrintable> pList = new ArrayList<>();
        for (GoodItemEntity entity : itemEntityList) {
            pList.add(new PrintableText(entity.getName() + " " + entity.getQuantity().toPlainString()));
        }
        printText(pList);*/
    }

    public static void printText(List<IPrintable> pList, Context context) {
        DeviceServiceConnector.startInitConnections(context);
        new Thread() {
            @Override
            public void run() {
                try {
                    DeviceServiceConnector.getPrinterService().printDocument(
                            ru.evotor.devices.commons.Constants.DEFAULT_DEVICE_INDEX,
                            new PrinterDocument(pList.toArray(new IPrintable[pList.size()])));
                } catch (DeviceServiceException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
}
