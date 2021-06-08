package ru.softvillage.fiscalizer.tabs.fragments.recyclerView;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import ru.softvillage.fiscalizer.EvoApp;
import ru.softvillage.fiscalizer.R;
import ru.softvillage.fiscalizer.roomDb.Entity.fiscalized.ReceiptEntity;
import ru.softvillage.fiscalizer.tabs.left_menu.presenter.SessionPresenter;

public class ReceiptDateSplitHolder extends AbstractReceiptViewHolder {
    private final TextView item_date_splitter;
    private final ImageView image_calendar;
    private final ConstraintLayout item_date_holder;

    private final Observer<Integer> observer = new Observer<Integer>() {
        @Override
        public void onChanged(Integer integer) {
            int tabIconColor;
            if (integer == SessionPresenter.THEME_LIGHT) {
                item_date_holder.setBackgroundColor(ContextCompat.getColor(item_date_holder.getContext(), R.color.main_lt));
                item_date_splitter.setTextColor(ContextCompat.getColor(item_date_splitter.getContext(), R.color.active_fonts_lt));
                tabIconColor = ContextCompat.getColor(EvoApp.getInstance().getApplicationContext(), R.color.active_fonts_lt);
            } else {
                item_date_holder.setBackgroundColor(ContextCompat.getColor(item_date_holder.getContext(), R.color.main_dt));
                item_date_splitter.setTextColor(ContextCompat.getColor(item_date_splitter.getContext(), R.color.active_fonts_dt));
                tabIconColor = ContextCompat.getColor(EvoApp.getInstance().getApplicationContext(), R.color.active_fonts_dt);
            }
            Drawable calendar_icon = ContextCompat.getDrawable(EvoApp.getInstance().getApplicationContext(), R.drawable.ic_statistic_calendar);
            assert calendar_icon != null;
            calendar_icon.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            image_calendar.setImageDrawable(calendar_icon);
        }
    };

    public ReceiptDateSplitHolder(View itemView) {
        super(itemView);
        item_date_splitter = itemView.findViewById(R.id.item_date_splitter);
        image_calendar = itemView.findViewById(R.id.image_calendar);
        item_date_holder = itemView.findViewById(R.id.item_date_holder);
    }

    public void bind(ReceiptEntity entity) {
        SessionPresenter.getInstance().getCurrentThemeLiveData().observeForever(observer);

        int numMonthOfYear = entity.getReceived().getMonthOfYear();
        String nameMonthOfYear = "";
        switch (numMonthOfYear) {
            case 1:
                nameMonthOfYear = "Января";
                break;
            case 2:
                nameMonthOfYear = "Февраля";
                break;
            case 3:
                nameMonthOfYear = "Марта";
                break;
            case 4:
                nameMonthOfYear = "Апреля";
                break;
            case 5:
                nameMonthOfYear = "Мая";
                break;
            case 6:
                nameMonthOfYear = "Июня";
                break;
            case 7:
                nameMonthOfYear = "Июля";
                break;
            case 8:
                nameMonthOfYear = "Августа";
                break;
            case 9:
                nameMonthOfYear = "Сентября";
                break;
            case 10:
                nameMonthOfYear = "Октября";
                break;
            case 11:
                nameMonthOfYear = "Ноября";
                break;
            case 12:
                nameMonthOfYear = "Декабря";
                break;
        }
        int dayOfMonth = entity.getReceived().getDayOfMonth();
        int year = entity.getReceived().getYear();
        item_date_splitter.setText(String.format("%d %s %dг", dayOfMonth, nameMonthOfYear, year));
    }

}
