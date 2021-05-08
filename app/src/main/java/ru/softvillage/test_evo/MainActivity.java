package ru.softvillage.test_evo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import ru.softvillage.test_evo.tabs.TabLayoutFragment;
import ru.softvillage.test_evo.tabs.left_menu.DrawerMenuManager;

public class MainActivity extends AppCompatActivity implements LifecycleOwner {
    DrawerMenuManager<MainActivity> manager;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            manager = new DrawerMenuManager<>(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, new TabLayoutFragment()).commit();
        } else {
            manager.setActivity(this);
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
    }
}

