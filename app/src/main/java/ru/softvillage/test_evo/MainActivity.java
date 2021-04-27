package ru.softvillage.test_evo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import ru.softvillage.test_evo.tabs.TabLayoutFragment;
import ru.softvillage.test_evo.tabs.left_menu.DrawerMenuManager;

public class MainActivity extends AppCompatActivity implements LifecycleOwner {
    DrawerMenuManager<MainActivity> manager;

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

    }
}

