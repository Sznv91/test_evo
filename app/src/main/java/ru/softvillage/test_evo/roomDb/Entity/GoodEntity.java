package ru.softvillage.test_evo.roomDb.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import ru.softvillage.test_evo.network.entity.Good;
import ru.softvillage.test_evo.roomDb.BigDecimalConverter;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.RESTRICT;

@Entity(tableName = "good",
        foreignKeys = @ForeignKey(
                entity = ReceiptEntity.class,
                parentColumns = "id",
                childColumns = "receipt_id",
                onDelete = CASCADE),
        indices = @Index("receipt_id"))
@TypeConverters({BigDecimalConverter.class})
@Data
public class GoodEntity {

    @PrimaryKey(autoGenerate = true)
//    @Setter(AccessLevel.NONE)
    private long id;
    @ColumnInfo(name = "receipt_id")
    private long receiptId;

    @ColumnInfo(name = "product_uuid")
    public String productUUID;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "measure_name")
    public String measureName;
    @ColumnInfo(name = "measure_precision")
    public Integer measurePrecision;
    @ColumnInfo(name = "price")
    public BigDecimal price;
    @ColumnInfo(name = "discount")
    //в процентах, на позицию.
    public BigDecimal discount;
    @ColumnInfo(name = "quantity")
    //Количество товара
    public BigDecimal quantity;
    @ColumnInfo(name = "nds")
    //-1 ; 0; 10; 18; 110; 118
    //На каждую позицию
    public Integer nds;

    @ColumnInfo(name = "type")
    //0 - товар, 1 -услуга //To normal + to Service
    public int type;

    public GoodEntity() {
    }

    public GoodEntity(Good good, long receiptId) {
        this.receiptId = receiptId;

        productUUID = good.productUUID;
        name = good.name;
        measureName = good.measureName;
        measurePrecision = good.measurePrecision;
        price = good.price;
        discount = good.discount;
        quantity = good.quantity;
        nds = good.nds;
        type = good.type.number;
    }

}
