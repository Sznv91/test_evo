package ru.softvillage.test_evo.roomDb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import ru.softvillage.test_evo.roomDb.Entity.GoodEntity;
import ru.softvillage.test_evo.roomDb.Entity.PartialReceiptPrinted;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptEntity;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptWithGoodEntity;

@Dao
public interface ReceiptDao {

    @Query("SELECT * FROM receipt ORDER BY date_time_received DESC")
    LiveData<List<ReceiptEntity>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ReceiptEntity receipt);

    //    @Update(onConflict = OnConflictStrategy.REPLACE)
    @Update(entity = ReceiptEntity.class)
    void update(PartialReceiptPrinted receipt);

    @Query("SELECT * FROM receipt WHERE id = :id")
    ReceiptEntity getById(long id);

    @Transaction
    @Query("SELECT * FROM receipt WHERE id = :id")
    LiveData<ReceiptWithGoodEntity> loadReceiptBy(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGood(GoodEntity entity);

    @Query("SELECT session_id FROM receipt WHERE id=:receiptId")
    Long getSessionId(long receiptId);
}
