package ru.softvillage.test_evo.services.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;

import ru.softvillage.test_evo.utils.SessionClose;

/**
 * При создании класса, далее один раз в час (используем Schedules) выполняем проверку на предмет
 * включен ли автозакрытие смены.
 * Если True, то выставляем используем Schedules.
 * Todo: Продумать как отменить Schedules если он выставлен.
 * <p>
 * Продумать как записывать
 */
public class SessionCheckerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        CheckerRestarter.restartChecker(context, intent);
        SessionClose.close(context);
    }


    public static class CheckerRestarter {
        /**
         * Если класс вызывается из вне, то в
         * intent подать null
         */
        private static void restartChecker(Context context, Intent intent) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (intent == null) {
                intent = new Intent(context, SessionCheckerReceiver.class);
            }
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime trigger = LocalDateTime.now();
            trigger = trigger.minusHours(24);
            long ldt = new Duration(now.toDateTime(), trigger.toDateTime()).getMillis();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            am.cancel(pendingIntent);
            am.set(AlarmManager.RTC_WAKEUP, ldt, pendingIntent);
        }
    }

}
