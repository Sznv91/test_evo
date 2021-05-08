package ru.softvillage.test_evo.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import org.joda.time.LocalDateTime;

import ru.evotor.framework.core.action.command.print_z_report_command.PrintZReportCommand;
import ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter;

public class SessionClose {

    @SuppressLint("LongLogTag")
    public static void close(Context context) {
        new PrintZReportCommand().process(context, future -> {
            SessionPresenter.getInstance().setDateLastCloseSession(LocalDateTime.now());
        });
    }
}
