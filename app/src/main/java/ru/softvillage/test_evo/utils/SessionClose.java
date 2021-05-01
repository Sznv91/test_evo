package ru.softvillage.test_evo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.action.command.print_z_report_command.PrintZReportCommand;
import ru.softvillage.test_evo.EvoApp;

public class SessionClose {

    @SuppressLint("LongLogTag")
    public static void close(Context context) {
        new PrintZReportCommand().process(context, future -> {
            try {
                Log.d(EvoApp.TAG + "_z_report", future.getResult().getData().toString());
            } catch (IntegrationException e) {
                e.printStackTrace();
            }
        });
    }
}
