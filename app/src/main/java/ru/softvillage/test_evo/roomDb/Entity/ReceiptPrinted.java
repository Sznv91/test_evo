package ru.softvillage.test_evo.roomDb.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.joda.time.LocalDateTime;

import java.math.BigDecimal;

import lombok.Data;
import ru.softvillage.test_evo.roomDb.BigDecimalConverter;
import ru.softvillage.test_evo.roomDb.DateTimeConverter;

@Entity(tableName = "receipt_printed")
@Data
@TypeConverters({DateTimeConverter.class, BigDecimalConverter.class})
public class ReceiptPrinted {

    @PrimaryKey
    long id;

    @ColumnInfo(name = "summary_price")
    BigDecimal price;
    @ColumnInfo(name = "date_time_received")
    LocalDateTime received;
    @ColumnInfo(name = "date_time_printed")
    LocalDateTime printed;
}
