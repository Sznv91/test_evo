package ru.softvillage.test_evo.tabs.fragments;

import ru.softvillage.test_evo.roomDb.Entity.SessionStatisticData;

public interface StatisticDisplayUpdate {
    void updateView(SessionStatisticData data);
    void updateTheme();
}
