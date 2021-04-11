package ru.softvillage.test_evo;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import ru.softvillage.test_evo.tabs.TabLayoutFragment;

public class MainActivity extends AppCompatActivity implements LifecycleOwner {

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, new TabLayoutFragment()).commit();
        }
        EvoApp.getInstance().getFragmentDispatcher().setActivity(this);

    }
}

