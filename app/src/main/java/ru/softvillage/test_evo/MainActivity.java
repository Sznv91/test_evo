package ru.softvillage.test_evo;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.softvillage.test_evo.network.entity.NetworkAnswer;
import ru.softvillage.test_evo.network.entity.Order;
import ru.softvillage.test_evo.services.ForegroundServiceDispatcher;
import ru.softvillage.test_evo.tabs.FragmentAdapter;
import ru.softvillage.test_evo.utils.PositionCreator;
import ru.softvillage.test_evo.utils.PrintUtil;

public class MainActivity extends AppCompatActivity implements LifecycleOwner {

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(
                new FragmentAdapter(getSupportFragmentManager(), getLifecycle()));

        TabLayout tabLayout = findViewById(R.id.tab_layout);

        new TabLayoutMediator(tabLayout,viewPager, (tab, position) -> {
            tab.setText("Object " + (position+1));
            viewPager.setCurrentItem(tab.getPosition(), true);
        }).attach();
    }
}

