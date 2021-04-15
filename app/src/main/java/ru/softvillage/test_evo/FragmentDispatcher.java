package ru.softvillage.test_evo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import lombok.Setter;

public class FragmentDispatcher {
    @Setter
    private AppCompatActivity activity;

    public void replaceFragment(Fragment fragment) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_holder, fragment)
                .addToBackStack(String.valueOf(fragment.getId()))
                .commit();
    }
}
