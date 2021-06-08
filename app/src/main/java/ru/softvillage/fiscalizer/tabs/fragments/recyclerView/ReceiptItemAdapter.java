package ru.softvillage.fiscalizer.tabs.fragments.recyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.softvillage.fiscalizer.EvoApp;
import ru.softvillage.fiscalizer.R;
import ru.softvillage.fiscalizer.roomDb.Entity.fiscalized.ReceiptEntity;

@RequiredArgsConstructor
public class ReceiptItemAdapter extends RecyclerView.Adapter<AbstractReceiptViewHolder> {

    public static final String DATE_SPLITTER_NAME = EvoApp.TAG + "_Date_Splitter_NAME";
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_DATA_SPLITTER = 1;
    private static final int TYPE_NOT_FISCALIZED = 2;


    private final LayoutInflater inflater;
    private final List<ReceiptEntity> itemList = new ArrayList<>();
    private final itemClickInterface callback;

    @NonNull
    @Override
    public AbstractReceiptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_DATA_SPLITTER:
                return new ReceiptDateSplitHolder(inflater.inflate(R.layout.item_good_date, parent, false));
            case TYPE_NORMAL:
            case TYPE_NOT_FISCALIZED:
                return new ReceiptItemViewHolder(inflater.inflate(R.layout.item_receipt, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractReceiptViewHolder holder, int position) {

        holder.bind(itemList.get(holder.getAdapterPosition()));
        switch (holder.getItemViewType()) {
            case TYPE_DATA_SPLITTER:
                TextView dateTextView = holder.itemView.findViewById(R.id.item_date_splitter);
                dateTextView.setOnClickListener(v -> {
                    callback.pushOnDate(itemList.get(position).getReceived());
                });
                break;
            case TYPE_NORMAL:
                holder.itemView.setOnClickListener(v -> callback.clickClick(itemList.get(position)));
                break;
            case TYPE_NOT_FISCALIZED:
                break;
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
        if (itemList.get(position).getUuid() != null && itemList.get(position).getUuid().equals(DATE_SPLITTER_NAME)) {
            return TYPE_DATA_SPLITTER;
        }
        if (TextUtils.isEmpty(itemList.get(position).getUuid())) {
            return TYPE_NOT_FISCALIZED;
        }
        return TYPE_NORMAL;
    }

    public interface itemClickInterface {
        void clickClick(ReceiptEntity recipientEntity);

        void pushOnDate(LocalDateTime date);
    }


}
