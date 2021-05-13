package ru.softvillage.test_evo.tabs.fragments.recyclerView;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;

import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptEntity;
import ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter;

public class ReceiptItemViewHolder extends AbstractReceiptViewHolder {
    private final TextView title_receipt_sale;
    private final TextView tv_static_summ;
    private final TextView title_receipt_time;

    private final ConstraintLayout item_receipt_layout;

    private final Observer<Integer> observer = new Observer<Integer>() {

        @SuppressLint("LongLogTag")
        @Override
        public void onChanged(Integer integer) {
            if (integer == SessionPresenter.THEME_LIGHT){
                item_receipt_layout.setBackgroundColor(ContextCompat.getColor(item_receipt_layout.getContext(), R.color.white));
                title_receipt_sale.setTextColor(ContextCompat.getColor(title_receipt_sale.getContext(), R.color.black));
            } else {
                item_receipt_layout.setBackgroundColor(ContextCompat.getColor(item_receipt_layout.getContext(), R.color.color31));
                title_receipt_sale.setTextColor(ContextCompat.getColor(title_receipt_sale.getContext(), R.color.white));
            }
        }
    };

    public ReceiptItemViewHolder(@NonNull View itemView) {
        super(itemView);
        item_receipt_layout = itemView.findViewById(R.id.item_receipt_layout);
        title_receipt_sale = itemView.findViewById(R.id.title_receipt_sale);
        tv_static_summ = itemView.findViewById(R.id.tv_static_summ);
        title_receipt_time = itemView.findViewById(R.id.title_receipt_time);
        SessionPresenter.getInstance().getCurrentThemeLiveData().observeForever(observer);
    }

    public void bind(ReceiptEntity entity) {
        title_receipt_sale.setText(String.format(EvoApp.getInstance().getString(R.string.title_receipt_sale), entity.getSessionId(), entity.getCountOfPosition()));
        tv_static_summ.setText(String.format(EvoApp.getInstance().getString(R.string.tv_static_summ), entity.getPrice()));
        title_receipt_time.setText(entity.getReceived().toString("HH:mm"));
    }


}
