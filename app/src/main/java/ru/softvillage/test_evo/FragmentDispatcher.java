package ru.softvillage.test_evo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import lombok.Getter;
import lombok.Setter;

public class FragmentDispatcher {
    @Getter
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
