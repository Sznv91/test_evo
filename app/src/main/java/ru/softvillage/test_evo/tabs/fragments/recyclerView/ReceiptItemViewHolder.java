package ru.softvillage.test_evo.tabs.fragments.recyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptEntity;

public class ReceiptItemViewHolder extends AbstractViewHolder {
    private final TextView receiptNumber;
    private final TextView goodCount;
    private final TextView totalCost;

    public ReceiptItemViewHolder(@NonNull View itemView) {
        super(itemView);
        receiptNumber = itemView.findViewById(R.id.text_view_receipt_number);
        goodCount = itemView.findViewById(R.id.text_view_good_count);
        totalCost = itemView.findViewById(R.id.text_view_total_cost);
    }

    public void bind(ReceiptEntity entity) {
        receiptNumber.setText(String.valueOf(entity.getReceiptNumber()));
        goodCount.setText(String.valueOf(entity.getCountOfPosition()));
        totalCost.setText(String.valueOf(entity.getPrice().toPlainString()));
    }
}
