package ru.softvillage.test_evo.roomDb;

import androidx.lifecycle.LiveData;

import java.util.List;

import ru.softvillage.test_evo.roomDb.Entity.PartialReceiptPrinted;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptEntity;

public class DbHelper {
    LocalDataBase dataBase;

    LiveData<List<ReceiptEntity>> receiptList;

    public DbHelper(LocalDataBase dataBase) {
        this.dataBase = dataBase;
        receiptList = this.dataBase.receiptDao().getAll();
    }

    public void insertReceiptToDb(ReceiptEntity receipt) {
        LocalDataBase.databaseWriteExecutor.execute(
                () -> dataBase.receiptDao().insert(receipt)
        );
    }

    public void updateReceipt(PartialReceiptPrinted receipt) {
        LocalDataBase.databaseWriteExecutor.execute(() ->
                dataBase.receiptDao().update(receipt));
    }

    public LiveData<List<ReceiptEntity>> getAll() {
        return receiptList;
    }

    public LiveData<ReceiptEntity> getById(long receiptId){
        return dataBase.receiptDao().getById(receiptId);
    }
}
