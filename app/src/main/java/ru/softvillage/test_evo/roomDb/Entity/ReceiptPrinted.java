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
    private long id;

    @ColumnInfo(name = "summary_price")
    private BigDecimal price;
    @ColumnInfo(name = "date_time_received")
    private LocalDateTime received;
    @ColumnInfo(name = "count_position")
    private int countOfPosition;

    @ColumnInfo(name = "date_time_printed")
    private LocalDateTime printed;
    @ColumnInfo(name = "receipt_number")
    private long receiptNumber;
}
