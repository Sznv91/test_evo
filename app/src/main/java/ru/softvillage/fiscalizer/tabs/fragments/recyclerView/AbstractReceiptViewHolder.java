package ru.softvillage.fiscalizer.tabs.fragments.recyclerView;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.softvillage.fiscalizer.roomDb.Entity.fiscalized.ReceiptEntity;

public abstract class AbstractReceiptViewHolder extends RecyclerView.ViewHolder {

    public AbstractReceiptViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(ReceiptEntity entity);
}
