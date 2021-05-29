package ru.softvillage.test_evo.roomDb.Entity.fromNetwork;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.math.BigDecimal;

import lombok.Data;
import ru.softvillage.test_evo.roomDb.BigDecimalConverter;

@Entity(tableName = "order_db")
@Data
@TypeConverters({BigDecimalConverter.class})
public class OrderDb {

    @PrimaryKey
    @ColumnInfo(name = "sv_id")
    public long sv_id;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "user_uuid")
    public String userUUID;

    @ColumnInfo(name = "payment_system")
    public Integer paymentSystem;

    @ColumnInfo(name = "check_discount")
    public BigDecimal checkDiscount;
}
