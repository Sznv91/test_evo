package ru.softvillage.fiscalizer.tabs.fragments;

import ru.softvillage.fiscalizer.roomDb.Entity.SessionStatisticData;

public interface StatisticDisplayUpdate {
    void updateView(SessionStatisticData data);
    void updateTheme();
    void updateNetworkQuality();
}
