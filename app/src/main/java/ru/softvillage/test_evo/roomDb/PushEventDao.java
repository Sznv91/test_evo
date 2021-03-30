package ru.softvillage.test_evo.roomDb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ru.softvillage.test_evo.roomDb.Entity.PushEvent;

@Dao
public interface PushEventDao {
    @Query("SELECT * FROM push_event ORDER BY date_time DESC")
    LiveData<List<PushEvent>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvent(PushEvent event);
}
