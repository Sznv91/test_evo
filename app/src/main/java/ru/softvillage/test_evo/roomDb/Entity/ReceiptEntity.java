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
import ru.softvillage.test_evo.utils.PositionCreator;

@Entity(tableName = "receipt")
@Data
@TypeConverters({DateTimeConverter.class, BigDecimalConverter.class})
public class ReceiptEntity {

    public ReceiptEntity() {
    }

    public ReceiptEntity(PositionCreator.OrderTo.PositionTo orderTo) {
        id = orderTo.getOrderData().id;
        received = LocalDateTime.now();
        price = orderTo.getSumPrice();
        countOfPosition = orderTo.getPositions().size();
//        discount = orderTo.getOrderData().checkDiscount;
    }

    @PrimaryKey
    private long id;

    @ColumnInfo(name = "summary_price")
    private BigDecimal price;
    @ColumnInfo(name = "date_time_received")
    private LocalDateTime received;
    @ColumnInfo(name = "count_position")
    private int countOfPosition;
    /*@ColumnInfo(name = "discount")
    private BigDecimal discount;*/

    @ColumnInfo(name = "date_time_printed")
    private LocalDateTime printed;
    @ColumnInfo(name = "receipt_number")
    private long receiptNumber;
    @ColumnInfo(name = "receipt_uuid")
    private String uuid;
    //Смена
    @ColumnInfo(name = "session_id")
    private Long sessionId;
    @ColumnInfo(name = "user_uuid")
    private String userUuid;

}
