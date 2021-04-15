package ru.softvillage.test_evo.roomDb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.softvillage.test_evo.roomDb.Entity.GoodEntity;
import ru.softvillage.test_evo.roomDb.Entity.PushEvent;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptEntity;

@Database(entities = {PushEvent.class, ReceiptEntity.class, GoodEntity.class}, version = 1, exportSchema = false)
public abstract class LocalDataBase extends RoomDatabase {

    public abstract PushEventDao pushEventDao();

    public abstract ReceiptDao receiptDao();

    private static volatile LocalDataBase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static LocalDataBase getDataBase(final Context context) {
        if (INSTANCE == null) {
            synchronized (LocalDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            LocalDataBase.class,
                            "soft_village_data_base")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
