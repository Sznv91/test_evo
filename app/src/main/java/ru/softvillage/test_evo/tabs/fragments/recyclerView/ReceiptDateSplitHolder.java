package ru.softvillage.test_evo.tabs.fragments.recyclerView;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.roomDb.Entity.fiscalized.ReceiptEntity;

public class ReceiptDateSplitHolder extends AbstractReceiptViewHolder {
    private final TextView date;

    public ReceiptDateSplitHolder(View itemView) {
        super(itemView);
        date = itemView.findViewById(R.id.item_date_splitter);
    }

    public void bind(ReceiptEntity entity) {
        Drawable calendar_icon = ContextCompat.getDrawable(EvoApp.getInstance().getApplicationContext(), R.drawable.ic_statistic_calendar);
        int tabIconColor = ContextCompat.getColor(EvoApp.getInstance().getApplicationContext(), R.color.color29);
        calendar_icon.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        date.setCompoundDrawablesRelativeWithIntrinsicBounds(calendar_icon, null, null, null);

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
        date.setText(String.format("%d %s %dг", dayOfMonth, nameMonthOfYear, year));
    }

}
