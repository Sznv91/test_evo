package ru.softvillage.fiscalizer.roomDb.Entity.fiscalized;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import lombok.Data;

@Data
public class ReceiptWithGoodEntity {
    @Embedded
    public ReceiptEntity receiptEntity;
    @Relation(parentColumn = "sv_id",
            entity = GoodEntity.class,
            entityColumn = "receipt_id")
    public List<GoodEntity> goodEntities;
}
