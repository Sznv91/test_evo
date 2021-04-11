package ru.softvillage.test_evo.tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

import ru.softvillage.test_evo.R;

public class TabLayoutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_layout_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTabs();
    }

    private void initTabs() {
        ViewPager2 viewPager = Objects.requireNonNull(getView()).findViewById(R.id.pager);
        viewPager.setAdapter(
//                new FragmentAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), getLifecycle()));
                new FragmentAdapter(this));
        TabLayout tabLayout = getView().findViewById(R.id.tab_layout);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText("Object " + (position + 1));
            if (position == 1) {
                tab.setIcon(R.drawable.ic_baseline_receipt_24);
            }
            viewPager.setCurrentItem(tab.getPosition(), true);
        }).attach();
    }

}
