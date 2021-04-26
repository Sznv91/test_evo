package ru.softvillage.test_evo.tabs.fragments.recyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptEntity;

@RequiredArgsConstructor
public class ReceiptItemAdapter extends RecyclerView.Adapter<AbstractViewHolder> {
    public static final String DATE_SPLITTER_NAME = EvoApp.TAG + "_Date_Splitter_NAME";
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_DATA_SPLITTER = 1;

    private final LayoutInflater inflater;
    private final List<ReceiptEntity> itemList = new ArrayList<>();
    private final itemClickInterface callback;

    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_DATA_SPLITTER:
                return new DateSplitHolder(inflater.inflate(R.layout.item_good_date, parent, false));
            case TYPE_NORMAL:
                return new ReceiptItemViewHolder(inflater.inflate(R.layout.item_receipt, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder holder, int position) {
        holder.bind(itemList.get(holder.getAdapterPosition()));
        if (holder.getItemViewType() == TYPE_DATA_SPLITTER) {
            TextView dateTextView = holder.itemView.findViewById(R.id.item_date_splitter);
            dateTextView.setOnClickListener(v -> {
                callback.pushOnDate(itemList.get(position).getReceived());
            });
        } else {
            holder.itemView.setOnClickListener(v -> callback.clickClick(itemList.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItems(List<ReceiptEntity> entityList) {
        itemList.clear();
        itemList.addAll(entityList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
            if (itemList.get(position).getUuid().equals(DATE_SPLITTER_NAME)) {
                return TYPE_DATA_SPLITTER;
            }
        return TYPE_NORMAL;
    }

    public interface itemClickInterface {
        void clickClick(ReceiptEntity recipientEntity);

        void pushOnDate(LocalDateTime date);
    }


}
