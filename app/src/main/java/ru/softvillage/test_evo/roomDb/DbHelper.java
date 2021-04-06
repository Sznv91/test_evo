package ru.softvillage.test_evo.roomDb;

import androidx.lifecycle.LiveData;

import java.util.List;

import ru.softvillage.test_evo.roomDb.Entity.ReceiptPrinted;

public class DbHelper {
    LocalDataBase dataBase;

    LiveData<List<ReceiptPrinted>> receiptList;

    public DbHelper(LocalDataBase dataBase) {
        this.dataBase = dataBase;
        receiptList = this.dataBase.receiptDao().getAll();
    }

    public void insertReceiptToDb(ReceiptPrinted receipt) {
        LocalDataBase.databaseWriteExecutor.execute(
                () -> dataBase.receiptDao().insert(receipt)
        );
    }

    public void updateReceipt(ReceiptPrinted receipt) {
        LocalDataBase.databaseWriteExecutor.execute(() ->
                dataBase.receiptDao().update(receipt));
    }

    public LiveData<List<ReceiptPrinted>> getAll() {
        return receiptList;
    }
}
