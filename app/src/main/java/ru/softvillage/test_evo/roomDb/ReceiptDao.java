package ru.softvillage.test_evo.roomDb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.softvillage.test_evo.roomDb.Entity.ReceiptPrinted;

@Dao
public interface ReceiptDao {

    @Query("SELECT * FROM receipt_printed ORDER BY date_time_received DESC")
    LiveData<List<ReceiptPrinted>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ReceiptPrinted receipt);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(ReceiptPrinted receipt);
}
