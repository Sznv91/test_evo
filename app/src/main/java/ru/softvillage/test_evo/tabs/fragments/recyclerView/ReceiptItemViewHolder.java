package ru.softvillage.test_evo.tabs.fragments.recyclerView;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.roomDb.Entity.fiscalized.ReceiptEntity;
import ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter;

public class ReceiptItemViewHolder extends AbstractReceiptViewHolder {
    private final TextView title_receipt_sale;
    private final TextView tv_static_summ;
    private final TextView title_receipt_time;
    private final ImageView iv_static_check_list;

    private final ConstraintLayout item_receipt_layout;

    private final Observer<Integer> observer = new Observer<Integer>() {

        @SuppressLint("LongLogTag")
        @Override
        public void onChanged(Integer integer) {
            if (integer == SessionPresenter.THEME_LIGHT) {
                item_receipt_layout.setBackgroundColor(ContextCompat.getColor(item_receipt_layout.getContext(), R.color.background_lt));
                title_receipt_sale.setTextColor(ContextCompat.getColor(title_receipt_sale.getContext(), R.color.fonts_lt));
                tv_static_summ.setTextColor(ContextCompat.getColor(tv_static_summ.getContext(), R.color.active_fonts_lt));
                title_receipt_time.setTextColor(ContextCompat.getColor(title_receipt_time.getContext(), R.color.active_fonts_lt));
            } else {
                item_receipt_layout.setBackgroundColor(ContextCompat.getColor(item_receipt_layout.getContext(), R.color.background_dt));
                title_receipt_sale.setTextColor(ContextCompat.getColor(title_receipt_sale.getContext(), R.color.fonts_dt));
                tv_static_summ.setTextColor(ContextCompat.getColor(tv_static_summ.getContext(), R.color.active_fonts_dt));
                title_receipt_time.setTextColor(ContextCompat.getColor(title_receipt_time.getContext(), R.color.active_fonts_dt));
            }
        }
    };

    public ReceiptItemViewHolder(@NonNull View itemView) {
        super(itemView);
        item_receipt_layout = itemView.findViewById(R.id.item_receipt_layout);
        title_receipt_sale = itemView.findViewById(R.id.title_receipt_sale);
        tv_static_summ = itemView.findViewById(R.id.tv_static_summ);
        title_receipt_time = itemView.findViewById(R.id.title_receipt_time);
        iv_static_check_list = itemView.findViewById(R.id.iv_static_check_list);
        SessionPresenter.getInstance().getCurrentThemeLiveData().observeForever(observer);
    }

    public void bind(ReceiptEntity entity) {
        if (entity.getReceiptNumber() == 0) {
            title_receipt_sale.setText(String.format("Чек продажи на %d позиции", entity.getCountOfPosition()));
            int tabIconColor = ContextCompat.getColor(EvoApp.getInstance(), R.color.active_fonts_lt);
            iv_static_check_list.getDrawable().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        } else {
            title_receipt_sale.setText(String.format(EvoApp.getInstance().getString(R.string.title_receipt_sale), entity.getReceiptNumber(), entity.getCountOfPosition()));
        }
        tv_static_summ.setText(String.format(EvoApp.getInstance().getString(R.string.tv_static_summ), entity.getPrice()));
        title_receipt_time.setText(entity.getReceived().toString("HH:mm"));
    }

}
