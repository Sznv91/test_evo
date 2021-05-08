package ru.softvillage.test_evo.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;

import ru.evotor.framework.system.SystemStateApi;
import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.roomDb.Entity.SessionStatisticData;
import ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter;
import ru.softvillage.test_evo.utils.PrintCustomTextUtil;
import ru.softvillage.test_evo.utils.SessionClose;
import ru.softvillage.test_evo.utils.StatisticConsider;

import static ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter.AUTO_CLOSE_AT_;
import static ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter.AUTO_CLOSE_EVERY_;
import static ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter.AUTO_CLOSE_EVERY_DAY;

public class SessionCloseWatcher extends Service {
    private static final int UPDATE_TIME_MILLIS = 1000;

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate() {
        Log.d(EvoApp.TAG + "_SessionCloseWatcher", "Start service");
        new Thread(this::startCheck).start();
        super.onCreate();
    }

    @SuppressLint("LongLogTag")
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(EvoApp.TAG + "_SessionCloseWatcher", "onBind service");
        return null;
    }

    SessionStatisticData data = StatisticConsider.getEmptySessionData();

    @SuppressLint("LongLogTag")
    private void startCheck() {
        while (true) {
            Boolean currentStateOpen = SystemStateApi.isSessionOpened(getApplicationContext());
            LocalDateTime now = LocalDateTime.now();
            /**
             * Выполняем отслеживание закрытия / открытия смен
             */
            if (currentStateOpen) {
                if (SessionPresenter.getInstance().getDateLastCloseSession() == null) {
                    if (SessionPresenter.getInstance().getDateLastOpenSession() == null) {
                        SessionPresenter.getInstance().setDateLastOpenSession(now);
                        StatisticConsider.setSessionId(SystemStateApi.getLastSessionNumber(getApplicationContext()));
                    }
                } else {
                    if (SessionPresenter.getInstance().getDateLastOpenSession() == null || SessionPresenter.getInstance().getDateLastOpenSession().isBefore(SessionPresenter.getInstance().getDateLastCloseSession())) {
                        SessionPresenter.getInstance().setDateLastOpenSession(now);
                        StatisticConsider.setSessionId(SystemStateApi.getLastSessionNumber(getApplicationContext()));
                    }
                }
            } else {
                if (SessionPresenter.getInstance().getDateLastOpenSession() == null) {
                    if (SessionPresenter.getInstance().getDateLastCloseSession() == null) {
                        SessionPresenter.getInstance().setDateLastCloseSession(LocalDateTime.now()/*.minusSeconds(3)*/);
                        Log.d(EvoApp.TAG + "_print_report", "Отловили закрытие смены в 1");
                        /*if (SessionPresenter.getInstance().isPrintReportOnClose()) {
                            Log.d(EvoApp.TAG + "_print_report", "Ожидаем печати отчета");
                            PrintCustomTextUtil.printStatistic(SessionPresenter.getInstance().getSessionData(), getApplicationContext());
                        }
                        SessionPresenter.getInstance().setSessionStatisticData(StatisticConsider.getEmptySessionData());*/
                    }
                } else {
                    if (SessionPresenter.getInstance().getDateLastCloseSession() == null || SessionPresenter.getInstance().getDateLastCloseSession().isBefore(SessionPresenter.getInstance().getDateLastOpenSession())) {
                        SessionPresenter.getInstance().setDateLastCloseSession(LocalDateTime.now()/*.minusSeconds(1)*/);
                        Log.d(EvoApp.TAG + "_print_report", "Отловили закрытие смены в 2");
                        /*if (SessionPresenter.getInstance().isPrintReportOnClose()) {
                            Log.d(EvoApp.TAG + "_print_report", "Ожидаем печати отчета");
                            PrintCustomTextUtil.printStatistic(SessionPresenter.getInstance().getSessionData(), getApplicationContext());
                        }
                        SessionPresenter.getInstance().setSessionStatisticData(StatisticConsider.getEmptySessionData());*/
                    }
                }
                SessionPresenter.getInstance().setSessionStatisticData(StatisticConsider.getEmptySessionData());

//                StatisticConsider.setSessionId(CLOSED_SESSION_ID);
            }
            /**
             * Закончили отслеживание
             */
            ////////////////////////////////////////////
            if (currentStateOpen != SessionPresenter.getInstance().getPreviousSessionStatus()) {
                SessionPresenter.getInstance().setPreviousSessionStatus(currentStateOpen);
                if (!currentStateOpen) {
                    if (SessionPresenter.getInstance().isPrintReportOnClose()) {
                        Log.d(EvoApp.TAG + "_print_report", "Ожидаем печати отчета из SessionCloseWatcher.class отдельной проверки.");
                        PrintCustomTextUtil.printStatistic(data, getApplicationContext());
                        ForegroundServiceDispatcher.updateNotificationCounter();
                    }
                }
            }
            if (data != SessionPresenter.getInstance().getSessionData()) {
                data = SessionPresenter.getInstance().getSessionData();
            }
            ////////////////////////////////////////////
            /**
             * Выполняем проверку о включении переключателя "Автоматическое закрытие смены"
             * Если переключатель есть, ->
             *      Выясняем какой режим выбран ->
             *      Берем данные о времени закрытия соотвествующиму типу закрытия ->
             *      Проверяем нужно ли закрывать смену.
             *
             */
            if (SessionPresenter.getInstance().isAutoClose()) {
                int autoCloseType = SessionPresenter.getInstance().getAutoCloseType();
                switch (autoCloseType) {
                    /**
                     * Вычесляем duration между ДатойВременем последнего закрытия,
                     * и текущей датойВременем.
                     * Если >= 24 часа, то закрываем смену.
                     */
                    case AUTO_CLOSE_EVERY_DAY:
                        int hours24InSeconds = 60 * 24 * 60;
                        if (currentStateOpen && deltaSecondsCalculator().getSeconds() /** -1*/ == hours24InSeconds) { //>=
                            if (SessionPresenter.getInstance().isPrintReportOnClose()) {
                                Log.d(EvoApp.TAG + "_print_report", "Ожидаем печати отчета из case AUTO_CLOSE_EVERY_DAY");
                                PrintCustomTextUtil.printStatistic(SessionPresenter.getInstance().getSessionData(), getApplicationContext());
                            }
                            SessionClose.close(getApplicationContext());
                        }
                        break;
                    /**
                     * Вычесляем duration между ДатойВременем последнего закрытия,
                     * и текущей датойВременем.
                     * Если >= времени соответсвующего типа закрытия, то закрываем смену.
                     */
                    case AUTO_CLOSE_EVERY_:
                        int value = SessionPresenter.getInstance().getAutoCloseEveryValue();
                        int unit = SessionPresenter.getInstance().getAutoCloseEveryUnit();
                        if (unit == SessionPresenter.AUTO_CLOSE_EVERY_UNIT_HOUR) {
                            value = value * 60 * 60;
                        } else {
                            value = value * 60;
                        }
                        if ((currentStateOpen && deltaSecondsCalculator().getSeconds() /** -1*/ == value) || (currentStateOpen && deltaSecondsCalculator().getSeconds() > (value + 60))) { // >=
                            /*if (SessionPresenter.getInstance().isPrintReportOnClose()) {
                                Log.d(EvoApp.TAG + "_print_report", "Ожидаем печати отчета из case AUTO_CLOSE_EVERY_");
//                                PrintCustomTextUtil.printStatistic(SessionPresenter.getInstance().getSessionData(), getApplicationContext());
                            }*/
                            Log.d(EvoApp.TAG + "_close_session", "AUTO_CLOSE_EVERY_ delta= " + deltaSecondsCalculator().getSeconds() + " value: " + value);

                            Log.d(EvoApp.TAG + "_close_session", "Закрываем смену из AUTO_CLOSE_EVERY_");
                            SessionClose.close(getApplicationContext());
                        }
                        break;
                    /**
                     * Если текущая дата, совпадает с временем соответсвующего типа закрытия,
                     * то закрываем сессию.
                     */
                    case AUTO_CLOSE_AT_:
                        int hour = SessionPresenter.getInstance().getAutoCloseAtHour();
                        int minutes = SessionPresenter.getInstance().getAutoCloseAtMinute();
                        LocalDateTime closeTime = now;
                        closeTime = closeTime.withTime(hour, minutes, 0, 0);
                        LocalDateTime tNow = now.withSecondOfMinute(0).withMillisOfSecond(0);
                        LocalDateTime lastDateTimeClose = SessionPresenter.getInstance().getDateLastCloseSession();
                        if (closeTime.toString().equals(tNow.toString())) {
                            lastDateTimeClose = lastDateTimeClose.withSecondOfMinute(0).withMillisOfSecond(0);
                            if (!lastDateTimeClose.toString().equals(tNow.toString())) {
                                if (SessionPresenter.getInstance().isPrintReportOnClose()) {
                                    Log.d(EvoApp.TAG + "_print_report", "Ожидаем печати отчета из case AUTO_CLOSE_AT_");
                                    PrintCustomTextUtil.printStatistic(SessionPresenter.getInstance().getSessionData(), getApplicationContext());
                                }
                                SessionClose.close(getApplicationContext());
                            }
                        }
                        break;
                    default:
                        break;

                }
            }


            /**
             * Переходим в сон на 1 секунду.
             */
            try {
                Thread.sleep(UPDATE_TIME_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Метод возвращает разницу в минутах между текущей датой временем
     * и датой временем последнего закрытия смены.
     *
     * @return разница в минутах между текущей датой временем
     * и датой временем последнего закрытия смены
     */
    private Seconds deltaSecondsCalculator() {
        Duration deltaFromLastClose = new Duration(
                SessionPresenter.getInstance().getDateLastOpenSession().toDateTime() //getDateLastCloseSession
                , LocalDateTime.now().toDateTime());
        return deltaFromLastClose.toStandardSeconds();


    }

}
