package ru.softvillage.test_evo;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import ru.softvillage.test_evo.tabs.FragmentAdapter;

public class MainActivity extends AppCompatActivity implements LifecycleOwner {

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (!Settings.canDrawOverlays(getApplicationContext())) { startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)); }



        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            Log.d(EvoApp.TAG+"_check_permission", "android.os.Build.VERSION is " + android.os.Build.VERSION.SDK_INT);
            //todo запросить разрешение на работу "android.permission.FOREGROUND_SERVICE"
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
                Log.d(EvoApp.TAG+"_check_permission", "Выполняем запрос разрешения");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.FOREGROUND_SERVICE},
                        PackageManager.PERMISSION_GRANTED);
                EvoApp.getInstance().startForegroundService();
                Log.d(EvoApp.TAG+"_check_permission", "Ожидаем запуск сервиса");
//                init(activity);
            } else {
                Log.d(EvoApp.TAG+"_check_permission", "Разрешение имеется");
            }
        }*/

        ViewPager2 viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(
                new FragmentAdapter(getSupportFragmentManager(), getLifecycle()));

        TabLayout tabLayout = findViewById(R.id.tab_layout);

        new TabLayoutMediator(tabLayout,viewPager, (tab, position) -> {
            tab.setText("Object " + (position+1));
            if (position == 1){
                tab.setIcon(R.drawable.ic_baseline_receipt_24);
            }
            viewPager.setCurrentItem(tab.getPosition(), true);
        }).attach();
    }
}

