package ru.softvillage.test_evo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import org.joda.time.LocalDateTime;

import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.action.command.print_z_report_command.PrintZReportCommand;
import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter;

public class SessionClose {

    @SuppressLint("LongLogTag")
    public static void close(Context context) {
        new PrintZReportCommand().process(context, future -> {
            SessionPresenter.getInstance().setDateLastCloseSession(LocalDateTime.now());

            /*try {
                Log.d(EvoApp.TAG + "_z_report", future.getResult().getData().toString());
            } catch (IntegrationException e) {
                e.printStackTrace();
            }*/
            /*if (SessionPresenter.getInstance().isPrintReportOnClose()) {
                Log.d(EvoApp.TAG + "_print_report", "Ожидаем печати отчета из case PrintZReportCommand().process");
//                                PrintCustomTextUtil.printStatistic(SessionPresenter.getInstance().getSessionData(), getApplicationContext());
            }*/
        });
        /**
         * Работающий код.
         */
        /*if (SessionPresenter.getInstance().isPrintReportOnClose()){
            Log.d(EvoApp.TAG+"_print_report", "Ожидаем печати отчета из SessionClose.class");
            PrintCustomTextUtil.printStatistic(SessionPresenter.getInstance().getSessionData(), context);
        }*/
    }
}
