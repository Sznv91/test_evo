package ru.softvillage.test_evo.tabs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import ru.softvillage.test_evo.tabs.fragments.ReceiptFragment;
import ru.softvillage.test_evo.tabs.fragments.StatisticFragment;

public class FragmentAdapter extends FragmentStateAdapter {
    final int PAGE_COUNT = 2;

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public FragmentAdapter(Fragment fragment) {
        super(fragment);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return StatisticFragment.newInstance();
            case 1:
                return ReceiptFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return PAGE_COUNT;
    }
}
