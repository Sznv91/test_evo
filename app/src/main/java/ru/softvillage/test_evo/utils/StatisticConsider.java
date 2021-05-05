package ru.softvillage.test_evo.utils;

import java.math.BigDecimal;

import ru.softvillage.test_evo.roomDb.Entity.SessionStatisticData;
import ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter;

import static ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter.CLOSED_SESSION_ID;

public class StatisticConsider {

    public static SessionStatisticData getStatistic() {
        return new SessionStatisticData(SessionPresenter.getInstance().getSessionData());
    }

    public static SessionStatisticData getEmptySessionData() {
        return new SessionStatisticData(CLOSED_SESSION_ID, BigDecimal.ZERO, 0, 0, 0);
    }

    public static void setSessionId(long sessionId) {
        if (sessionId != SessionPresenter.getInstance().getSessionData().getSessionId()) {
            SessionStatisticData result = getEmptySessionData();
            result.setSessionId(sessionId);
            SessionPresenter.getInstance().setSessionStatisticData(result);
        }

    }

    public static void addFiscalizedMoney(BigDecimal sumMoney) {
        SessionStatisticData result = getStatistic();
        result.setSumFiscalization(getStatistic().getSumFiscalization().add(sumMoney));
        SessionPresenter.getInstance().setSessionStatisticData(result);
    }

    public static void addCountReceipt() {
        SessionStatisticData result = getStatistic();
        result.setCountReceipt(result.getCountReceipt() + 1);
        SessionPresenter.getInstance().setSessionStatisticData(result);
    }

    public static void addCountSms() {
        SessionStatisticData result = getStatistic();
        result.setSendSms(result.getSendSms() + 1);
        SessionPresenter.getInstance().setSessionStatisticData(result);
    }

    public static void addCountEmail() {
        SessionStatisticData result = getStatistic();
        result.setSendEmail(result.getSendEmail() + 1);
        SessionPresenter.getInstance().setSessionStatisticData(result);
    }
}
