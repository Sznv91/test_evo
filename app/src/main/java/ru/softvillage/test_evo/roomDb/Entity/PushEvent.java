package ru.softvillage.test_evo.roomDb.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import ru.softvillage.test_evo.liveDataHolder.States;

@Data
@Builder
@Entity(tableName = "push_event")
public class PushEvent {
    @PrimaryKey(autoGenerate = true)
    long id;
    /*@ColumnInfo(name = "date_time")
    LocalDateTime dateTime;*/
    /*@ColumnInfo(name = "type")
    States typeEvent;*/
}
