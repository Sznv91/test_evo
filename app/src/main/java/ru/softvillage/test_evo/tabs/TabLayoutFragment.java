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

import static com.google.android.material.tabs.TabLayout.GRAVITY_CENTER;

public class TabLayoutFragment extends Fragment {
    TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_layout_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = view.findViewById(R.id.tab_layout);

        SessionPresenter.getInstance().getCurrentThemeLiveData().observe(this, currentTheme -> {
            if (currentTheme == SessionPresenter.THEME_LIGHT) {
                tabLayout.setBackgroundColor(ContextCompat.getColor(tabLayout.getContext(), R.color.color_f8));
                tabLayout.setTabTextColors(ContextCompat.getColor(
                        tabLayout.getContext(), R.color.color29),
                        ContextCompat.getColor(tabLayout.getContext(), R.color.black));
            } else {
                tabLayout.setBackgroundColor(ContextCompat.getColor(tabLayout.getContext(), R.color.black));
                tabLayout.setTabTextColors(ContextCompat.getColor(
                        tabLayout.getContext(), R.color.color29),
                        ContextCompat.getColor(tabLayout.getContext(), R.color.white));
            }
        });
        initTabs();
    }

    private void initTabs() {
        ViewPager2 viewPager = Objects.requireNonNull(getView()).findViewById(R.id.pager);
        viewPager.setAdapter(
                new FragmentAdapter(this));


        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Статистика");
                tab.setIcon(R.drawable.ic_component_1statistic);
                int tabIconColor = ContextCompat.getColor(getContext(), R.color.color17);
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
                int tabIconColor = ContextCompat.getColor(getContext(), R.color.color29);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

}
