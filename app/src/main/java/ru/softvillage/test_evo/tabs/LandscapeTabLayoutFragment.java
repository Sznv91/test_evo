package ru.softvillage.test_evo.tabs;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.tabs.fragments.ReceiptFragment;
import ru.softvillage.test_evo.tabs.fragments.StatisticFragment;
import ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter;

public class LandscapeTabLayoutFragment extends Fragment {
    private View divider_tabs;
    private TextView tab_title_statistic_information,
            title_receipts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_layout_fragment, container, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SessionPresenter.getInstance().getCurrentThemeLiveData().removeObservers(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.holder_statistic_fragment, StatisticFragment.newInstance()).commit();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.holder_receipts, ReceiptFragment.newInstance()).commit();

        divider_tabs = view.findViewById(R.id.divider_tabs);
        tab_title_statistic_information = view.findViewById(R.id.tab_title_statistic_information);
        title_receipts = view.findViewById(R.id.title_receipts);

        Drawable tab_title_statistic_information_icon = ContextCompat.getDrawable(EvoApp.getInstance().getApplicationContext(), R.drawable.ic_component_1statistic);
        tab_title_statistic_information_icon.setColorFilter(ContextCompat.getColor(tab_title_statistic_information.getContext(), R.color.color17), PorterDuff.Mode.SRC_IN);
        Drawable title_receipts_icon = ContextCompat.getDrawable(EvoApp.getInstance().getApplicationContext(), R.drawable.ic_baseline_receipt_24);
        title_receipts_icon.setColorFilter(ContextCompat.getColor(title_receipts.getContext(), R.color.color17), PorterDuff.Mode.SRC_IN);

        tab_title_statistic_information.setCompoundDrawablesRelativeWithIntrinsicBounds(tab_title_statistic_information_icon, null, null, null);
        tab_title_statistic_information.setCompoundDrawablePadding(12);

        title_receipts.setCompoundDrawablesRelativeWithIntrinsicBounds(title_receipts_icon, null, null, null);
        title_receipts.setCompoundDrawablePadding(10);


        SessionPresenter.getInstance().getCurrentThemeLiveData().observe(this, currentTheme -> {
            if (currentTheme == SessionPresenter.THEME_LIGHT) {
                tab_title_statistic_information.setTextColor(ContextCompat.getColor(tab_title_statistic_information.getContext(), R.color.black));
                title_receipts.setTextColor(ContextCompat.getColor(title_receipts.getContext(), R.color.black));
                divider_tabs.setBackgroundColor(ContextCompat.getColor(title_receipts.getContext(), R.color.color_e7));
            } else {
                tab_title_statistic_information.setTextColor(ContextCompat.getColor(tab_title_statistic_information.getContext(), R.color.white));
                title_receipts.setTextColor(ContextCompat.getColor(title_receipts.getContext(), R.color.white));
                divider_tabs.setBackgroundColor(ContextCompat.getColor(title_receipts.getContext(), R.color.black));
            }
        });
    }

}
