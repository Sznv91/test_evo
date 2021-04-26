package ru.softvillage.test_evo.tabs.fragments.recyclerView;

import android.view.View;
import android.widget.TextView;

import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptEntity;

public class DateSplitHolder extends AbstractViewHolder {
    private final TextView date;

    public DateSplitHolder(View itemView) {
        super(itemView);
        date = itemView.findViewById(R.id.item_date_splitter);
    }

    public void bind(ReceiptEntity entity) {
        date.setText(entity.getReceived().toLocalDate().toString());
    }

}
