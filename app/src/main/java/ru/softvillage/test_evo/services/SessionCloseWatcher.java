package ru.softvillage.test_evo.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.joda.time.LocalDateTime;

import ru.evotor.framework.system.SystemStateApi;
import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter;

public class SessionCloseWatcher extends Service {

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

    @SuppressLint("LongLogTag")
    private void startCheck() {
        while (true) {
            Boolean currentStateOpen = SystemStateApi.isSessionOpened(getApplicationContext());
            LocalDateTime now = LocalDateTime.now();
            if (currentStateOpen) {
                if (SessionPresenter.getInstance().getDateLastCloseSession() == null) {
                    if (SessionPresenter.getInstance().getDateLastOpenSession() == null){
                        SessionPresenter.getInstance().setDateLastOpenSession(now);
                    }
                } else {
                    if (SessionPresenter.getInstance().getDateLastOpenSession() == null || SessionPresenter.getInstance().getDateLastOpenSession().isBefore(SessionPresenter.getInstance().getDateLastCloseSession())) {
                        SessionPresenter.getInstance().setDateLastOpenSession(now);
                    }
                }
            } else {
                if (SessionPresenter.getInstance().getDateLastOpenSession() == null) {
                    if (SessionPresenter.getInstance().getDateLastCloseSession() == null){
                        SessionPresenter.getInstance().setDateLastCloseSession(now);
                    }
                } else {
                    if (SessionPresenter.getInstance().getDateLastCloseSession() == null || SessionPresenter.getInstance().getDateLastCloseSession().isBefore(SessionPresenter.getInstance().getDateLastOpenSession())) {
                        SessionPresenter.getInstance().setDateLastCloseSession(now);
                    }
                }
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}
