package ru.softvillage.fiscalizer;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import ru.softvillage.fiscalizer.services.ForegroundServiceDispatcher;
import ru.softvillage.fiscalizer.tabs.LandscapeTabLayoutFragment;
import ru.softvillage.fiscalizer.tabs.TabLayoutFragment;
import ru.softvillage.fiscalizer.tabs.left_menu.DrawerMenuManager;
import ru.softvillage.fiscalizer.tabs.left_menu.dialogs.AboutDialog;
import ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter;

public class MainActivity extends AppCompatActivity implements LifecycleOwner {
    DrawerMenuManager<MainActivity> manager;

    /**
     * Для запрета возврата из ReceiptDetailFragment до окончания загрузки данных на UI
     */
    @SuppressLint("LongLogTag")
    @Override
    public void onBackPressed() {
        if (EvoApp.getInstance().getFragmentDispatcher().isAllowBack()){
            super.onBackPressed();
        } else {
            Log.d(EvoApp.TAG + "_MainActivity", "onBackPressed false");
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (SessionPresenter.getInstance().getCurrentTheme() == SessionPresenter.THEME_LIGHT) {
            setTheme(R.style.SVLightTheme);
        } else {
            setTheme(R.style.SVDarkTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startForegroundService();
        manager = new DrawerMenuManager<>(this);

        if (!SessionPresenter.getInstance().getIsCheckedUserAgreement()){
            AboutDialog dialog = AboutDialog.newInstance(AboutDialog.TYPE_USER_AGREEMENT);
            dialog.setCancelable(false);
            dialog.show(getSupportFragmentManager(), AboutDialog.TYPE_USER_AGREEMENT);
        }

        if (savedInstanceState == null) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, new TabLayoutFragment()).commit();
//                return "Портретная ориентация";
            else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, new LandscapeTabLayoutFragment()).commit();
            }
            /*return "Альбомная ориентация";*/
        }
        EvoApp.getInstance().getFragmentDispatcher().setActivity(this);


        /**
         * Узнаем размеры дисплеея
         */
        //Способ #1
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        //Способ #2
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        Log.d(EvoApp.TAG + "_display_metrics", "[Используя ресурсы] \n" +
                "Ширина: " + displaymetrics.widthPixels + "\n" +
                "Высота: " + displaymetrics.heightPixels + "\n"
                + "\n" +
                "[Используя Display] \n" +
                "Ширина: " + metricsB.widthPixels + "\n" +
                "Высота: " + metricsB.heightPixels);

        SessionPresenter.getInstance().initOrgInfo();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private void startForegroundService() {
        if (isMyServiceRunning(ForegroundServiceDispatcher.class)) {
            Toast.makeText(getApplicationContext(), "Service already running", Toast.LENGTH_LONG).show();
            return;
        }
        Intent startIntent = new Intent(this, ForegroundServiceDispatcher.class);
        startIntent.setAction("start");
        startService(startIntent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    //////////////////////////


    @Override
    protected void onDestroy() {
        SessionPresenter.getInstance().setiMainView1(null);
        super.onDestroy();
    }
}

