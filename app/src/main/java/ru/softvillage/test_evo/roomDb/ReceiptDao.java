package ru.softvillage.test_evo.roomDb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.softvillage.test_evo.roomDb.Entity.PartialReceiptPrinted;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptEntity;

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
    LiveData<ReceiptEntity> getById(long id);
}
