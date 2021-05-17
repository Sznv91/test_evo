package ru.softvillage.test_evo.roomDb;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.softvillage.test_evo.roomDb.Entity.GoodEntity;
import ru.softvillage.test_evo.roomDb.Entity.PushEvent;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptEntity;

@Database(entities = {PushEvent.class, ReceiptEntity.class, GoodEntity.class}, version = 1, exportSchema = false)
public abstract class LocalDataBase extends RoomDatabase {
    /*public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'receipt' ADD COLUMN 'user_uuid' TEXT DEFAULT '20210323-F84D-4023-80C2-43E669A3C55B'");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'receipt' ADD COLUMN 'rn_kkt' TEXT");
            database.execSQL("ALTER TABLE 'receipt' ADD COLUMN 'zn_kkt' TEXT");
            database.execSQL("ALTER TABLE 'receipt' ADD COLUMN 'org_inn' INTEGER");
            database.execSQL("ALTER TABLE 'receipt' ADD COLUMN 'sno_type' TEXT");
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'receipt' ADD COLUMN 'user_uuid' TEXT");
        }
    };*/

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
                            /*.addMigrations(MIGRATION_1_2, MIGRATION_2_3)*/
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
