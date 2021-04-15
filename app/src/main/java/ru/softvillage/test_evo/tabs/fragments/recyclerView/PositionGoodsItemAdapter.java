package ru.softvillage.test_evo.tabs.fragments.recyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.roomDb.Entity.GoodEntity;

@RequiredArgsConstructor
public class PositionGoodsItemAdapter extends RecyclerView.Adapter<PositionGoodsItemHolder> {
    private final LayoutInflater inflater;
    private List<GoodEntity> itemList = new ArrayList<>();

    @NonNull
    @Override
    public PositionGoodsItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PositionGoodsItemHolder(inflater.inflate(R.layout.position_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PositionGoodsItemHolder holder, int position) {
        holder.bind(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItems(List<GoodEntity> entityList) {
        itemList.clear();
        itemList.addAll(entityList);
        notifyDataSetChanged();
    }
}
