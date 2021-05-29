package ru.softvillage.test_evo.roomDb.Entity.fiscalized;

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
    long sv_id;

    @ColumnInfo(name = "date_time_printed")
    LocalDateTime printed;

    @ColumnInfo(name = "receipt_number")
    private long receiptNumber;

    @ColumnInfo(name = "receipt_uuid")
    private String uuid;

    @ColumnInfo(name = "session_id")
    private Long sessionId;

    @ColumnInfo(name = "user_uuid")
    private String userUuid;

    @ColumnInfo(name = "rn_kkt")
    private String rn_kkt;
    @ColumnInfo(name = "zn_kkt")
    private String zn_kkt;
    @ColumnInfo(name = "org_inn")
    private long inn;
    @ColumnInfo(name = "sno_type")
    private String sno_type;

    @ColumnInfo(name = "shop_name")
    String shop_name;
    @ColumnInfo(name = "shop_address")
    String address;
    @ColumnInfo(name = "shop_payment_place")
    String payment_place;
}
