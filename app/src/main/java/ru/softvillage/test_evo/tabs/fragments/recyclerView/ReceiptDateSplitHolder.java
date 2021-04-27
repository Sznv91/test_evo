package ru.softvillage.test_evo.tabs.fragments.recyclerView;

import android.view.View;
import android.widget.TextView;

import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptEntity;

public class ReceiptDateSplitHolder extends AbstractReceiptViewHolder {
    private final TextView date;

    public ReceiptDateSplitHolder(View itemView) {
        super(itemView);
        date = itemView.findViewById(R.id.item_date_splitter);
    }

    public void bind(ReceiptEntity entity) {
        date.setText(entity.getReceived().toLocalDate().toString());
    }

}
