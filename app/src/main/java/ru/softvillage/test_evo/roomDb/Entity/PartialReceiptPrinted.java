package ru.softvillage.test_evo.roomDb.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.joda.time.LocalDateTime;

import lombok.Data;
import ru.softvillage.test_evo.roomDb.DateTimeConverter;

@Entity
@Data
@TypeConverters({DateTimeConverter.class})
public class PartialReceiptPrinted {

    @PrimaryKey
    long id;

    @ColumnInfo(name = "date_time_printed")
    LocalDateTime printed;

    @ColumnInfo(name = "receipt_number")
    private long receiptNumber;

    @ColumnInfo(name = "receipt_uuid")
    private String uuid;

    @ColumnInfo(name = "session_id")
    private Long sessionId;
}
