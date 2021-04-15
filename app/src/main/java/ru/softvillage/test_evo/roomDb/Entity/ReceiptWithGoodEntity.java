package ru.softvillage.test_evo.roomDb.Entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import lombok.Data;

@Data
public class ReceiptWithGoodEntity {
    @Embedded
    public ReceiptEntity receiptEntity;
    @Relation(parentColumn = "id",
            entity = GoodEntity.class,
            entityColumn = "receipt_id")
    public List<GoodEntity> goodEntities;
}
