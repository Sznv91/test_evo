package ru.softvillage.test_evo.tabs.fragments.recyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptPrinted;

public class ReceiptItemAdapter extends RecyclerView.Adapter<ReceiptItemViewHolder> {
    private final LayoutInflater inflater;
    private final List<ReceiptPrinted> itemList;

    public ReceiptItemAdapter(LayoutInflater inflater, List<ReceiptPrinted> itemList) {
        this.inflater = inflater;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ReceiptItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReceiptItemViewHolder(inflater.inflate(R.layout.receipt_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptItemViewHolder holder, int position) {
        holder.bind(itemList.get(position));

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
