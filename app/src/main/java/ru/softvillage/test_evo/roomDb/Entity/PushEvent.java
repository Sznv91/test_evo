package ru.softvillage.test_evo.roomDb.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.joda.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import ru.softvillage.test_evo.liveDataHolder.States;
import ru.softvillage.test_evo.roomDb.DateTimeConverter;
import ru.softvillage.test_evo.roomDb.StateConverter;


@Data
//@Builder
@Entity(tableName = "push_event")
@TypeConverters({DateTimeConverter.class, StateConverter.class})
public class PushEvent {
    @PrimaryKey(autoGenerate = true)
    long id;

    @ColumnInfo(name = "date_time")
    LocalDateTime dateTime;

    @ColumnInfo(name = "type")
    States typeEvent;
}
