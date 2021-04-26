package ru.softvillage.test_evo.tabs.fragments.recyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.Receipt;
import ru.softvillage.test_evo.R;

@RequiredArgsConstructor
public class PositionGoodsItemAdapter extends RecyclerView.Adapter<PositionGoodsItemHolder> {
    private final LayoutInflater inflater;
    private List<Position> itemList = new ArrayList<>();
    private Receipt receipt;

    @NonNull
    @Override
    public PositionGoodsItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PositionGoodsItemHolder(inflater.inflate(R.layout.item_position, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PositionGoodsItemHolder holder, int position) {
        holder.bind(itemList.get(position), receipt);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItems(Receipt receipt) {
        this.receipt = receipt;
        itemList.clear();
        itemList.addAll(receipt.getPositions());
        notifyDataSetChanged();
    }
}
