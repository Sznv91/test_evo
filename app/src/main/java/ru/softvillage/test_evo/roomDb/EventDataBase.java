package ru.softvillage.test_evo.roomDb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.softvillage.test_evo.roomDb.Entity.PushEvent;

@Database(entities = {PushEvent.class}, version = 1, exportSchema = false)
public abstract class EventDataBase extends RoomDatabase {

    public abstract PushEventDao pushEventDao();

    private static volatile EventDataBase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static EventDataBase getDataBase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EventDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            EventDataBase.class,
                            "event_data_base")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
