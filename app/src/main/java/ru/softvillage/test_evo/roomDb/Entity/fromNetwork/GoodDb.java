package ru.softvillage.test_evo.roomDb.Entity.fromNetwork;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.math.BigDecimal;

import lombok.Data;
import ru.softvillage.test_evo.network.entity.Type;
import ru.softvillage.test_evo.roomDb.BigDecimalConverter;
import ru.softvillage.test_evo.roomDb.GoodTypeDbConverter;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "good_db",
        foreignKeys = @ForeignKey(
                entity = OrderDb.class,
                parentColumns = "sv_id",
                childColumns = "order_db_id",
                onDelete = CASCADE),
        indices = @Index("order_db_id"))
@Data
@TypeConverters({GoodTypeDbConverter.class, BigDecimalConverter.class})
public class GoodDb {

    @PrimaryKey(autoGenerate = true)
    long id;

    @ColumnInfo(name = "order_db_id")
    long orderDbId;

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
    public BigDecimal discount;

    @ColumnInfo(name = "quantity")
    public BigDecimal quantity;

    @ColumnInfo(name = "nds")
    public Integer nds;

    @ColumnInfo(name = "type")
    public Type type;
}
