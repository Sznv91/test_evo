package ru.softvillage.test_evo.tabs;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter;

public class TabLayoutFragment extends Fragment {
    TabLayout tabLayout;
    private View tab_divider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_layout_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = view.findViewById(R.id.tab_layout);
        tab_divider = view.findViewById(R.id.tab_divider);

        SessionPresenter.getInstance().getCurrentThemeLiveData().observe(this, currentTheme -> {
            int tabIconColor = 0;
            if (currentTheme == SessionPresenter.THEME_LIGHT) {
                tabLayout.setBackgroundColor(ContextCompat.getColor(tabLayout.getContext(), R.color.main_lt));
                tabLayout.setTabTextColors(ContextCompat.getColor(
                        tabLayout.getContext(), R.color.active_fonts_lt),
                        ContextCompat.getColor(tabLayout.getContext(), R.color.fonts_lt));
                tab_divider.setBackgroundColor(ContextCompat.getColor(tab_divider.getContext(), R.color.background_lt));

                int tabCount = tabLayout.getTabCount();
                tabIconColor = ContextCompat.getColor(getContext(), R.color.active_fonts_lt);
                for (int i = 0; i < tabCount; i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    if (tab != null && !tab.isSelected()) {
                        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    }
                }

            } else {
                tabLayout.setBackgroundColor(ContextCompat.getColor(tabLayout.getContext(), R.color.main_dt));
                tabLayout.setTabTextColors(ContextCompat.getColor(
                        tabLayout.getContext(), R.color.active_fonts_dt),
                        ContextCompat.getColor(tabLayout.getContext(), R.color.fonts_dt));
                tab_divider.setBackgroundColor(ContextCompat.getColor(tab_divider.getContext(), R.color.divider_dt));

                int tabCount = tabLayout.getTabCount();
                tabIconColor = ContextCompat.getColor(getContext(), R.color.active_fonts_dt);
                for (int i = 0; i < tabCount; i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    if (tab != null && !tab.isSelected()) {
                        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                    }
                }
            }
        });
        initTabs();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SessionPresenter.getInstance().getCurrentThemeLiveData().removeObservers(this);
    }

    private void initTabs() {
        ViewPager2 viewPager = Objects.requireNonNull(getView()).findViewById(R.id.pager);
        viewPager.setAdapter(
                new FragmentAdapter(this));


        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Статистика");
                tab.setIcon(R.drawable.ic_component_1statistic);
                int tabIconColor = ContextCompat.getColor(getContext(), R.color.header_lt);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            } else {
                tab.setText("Чеки");
                tab.setIcon(R.drawable.ic_baseline_receipt_24);
            }
            viewPager.setCurrentItem(tab.getPosition(), true);
        }).attach();


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getContext(), R.color.color17);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = 0;
                if (SessionPresenter.getInstance().getCurrentTheme() == SessionPresenter.THEME_LIGHT) {
                    tabIconColor = ContextCompat.getColor(getContext(), R.color.active_fonts_lt);
                } else {
                    tabIconColor = ContextCompat.getColor(getContext(), R.color.active_fonts_dt);
                }
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}
