package ru.softvillage.fiscalizer.roomDb;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.TypeConverters;
import androidx.room.Update;

import org.joda.time.LocalDateTime;

import java.util.List;

import ru.softvillage.fiscalizer.roomDb.Entity.fiscalized.GoodEntity;
import ru.softvillage.fiscalizer.roomDb.Entity.fiscalized.PartialReceiptPrinted;
import ru.softvillage.fiscalizer.roomDb.Entity.fiscalized.ReceiptEntity;
import ru.softvillage.fiscalizer.roomDb.Entity.fiscalized.ReceiptWithGoodEntity;
import ru.softvillage.fiscalizer.roomDb.Entity.fromNetwork.GoodDb;
import ru.softvillage.fiscalizer.roomDb.Entity.fromNetwork.OrderDb;
import ru.softvillage.fiscalizer.roomDb.Entity.fromNetwork.OrderDbWithGoods;

@Dao
public interface ReceiptDao {

    @Query("SELECT * FROM receipt ORDER BY date_time_received DESC")
    LiveData<List<ReceiptEntity>> getAll();

    /*@Query("SELECT * FROM receipt WHERE receipt_number = null ORDER BY date_time_received DESC")
    ArrayList<ReceiptEntity> getAllNotFiscalized();*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ReceiptEntity receipt);

    //    @Update(onConflict = OnConflictStrategy.REPLACE)
    @Update(entity = ReceiptEntity.class)
    void update(PartialReceiptPrinted receipt);

    @Query("SELECT * FROM receipt WHERE sv_id = :sv_id")
    ReceiptEntity getById(long sv_id);

    @Transaction
    @Query("SELECT * FROM receipt WHERE sv_id = :sv_id")
    LiveData<ReceiptWithGoodEntity> loadReceiptBy(long sv_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGood(GoodEntity entity);

    @Query("SELECT session_id FROM receipt WHERE sv_id=:sv_receiptId")
    Long getSessionId(long sv_receiptId);

    ///////////////////////////////////////////////////////////////////

    /**
     * Network entity part
     */

    @Transaction
    @Query("SELECT * FROM order_db")
    List<OrderDbWithGoods> getOrderDbWithGoods();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createOrderDb(OrderDb entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void createGoodDb(List<GoodDb> entity);

    @Delete()
    void removeOrderDb(OrderDb entity);

    @TypeConverters({DateTimeConverter.class})
    @Query("SELECT date_time_received FROM receipt WHERE sv_id =:sv_receiptId")
    LocalDateTime getDateTimeReceived(long sv_receiptId);
}
