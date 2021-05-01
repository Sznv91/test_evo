package ru.softvillage.test_evo.tabs.left_menu;

public interface IMainView1 {
    void updateEveryTitle();
    void updateAtTitle();

    void onConnectAppSuccess(boolean connected);
    void onConnectAppError(String error);

    void onSetErrorError(String error);
    void onSetErrorSuccess();

    void onSessionTimerTick(long delta);
    void showSessionWholeTime(long delta);

    void onPrintZReportError(String error);

//    void updateStatistic(SessionViewData data);

    void onGetReceiptError(String error);

    void showNoConnectionIcon();
}
