package ru.softvillage.test_evo.tabs.fragments.recyclerView;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.softvillage.test_evo.roomDb.Entity.ReceiptEntity;

public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {

    public AbstractViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bind(ReceiptEntity entity);
}
