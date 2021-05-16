package ru.softvillage.test_evo.roomDb;

import androidx.lifecycle.LiveData;
import androidx.room.Transaction;

import java.util.List;

import ru.softvillage.test_evo.roomDb.Entity.GoodEntity;
import ru.softvillage.test_evo.roomDb.Entity.PartialReceiptPrinted;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptEntity;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptWithGoodEntity;

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

    @Transaction
    public void getById(long receiptId, AsyncCallback callback) {
        dataBase.getQueryExecutor().execute(() -> {
            callback.receiptRequest(dataBase.receiptDao().getById(receiptId));
        });
    }

    @Transaction
    public void insertReceiptWithGoods(ReceiptWithGoodEntity receipt) {
        LocalDataBase.databaseWriteExecutor.execute(() -> {
            dataBase.receiptDao().insert(receipt.getReceiptEntity());
            for (GoodEntity goods : receipt.goodEntities) {
                dataBase.receiptDao().insertGood(goods);
            }
        });
    }

    public LiveData<ReceiptWithGoodEntity> getReceiptWithGoodEntity(long id) {
        return dataBase.receiptDao().loadReceiptBy(id);
    }

    public void getSessionId(long receiptId, AsyncCallback callback) {
        dataBase.getQueryExecutor().execute(() -> {
            callback.sessionRequest(dataBase.receiptDao().getSessionId(receiptId));
        });
    }

    public interface AsyncCallback {
        void sessionRequest(long sessionId);
        void receiptRequest(ReceiptEntity entity);
    }
}
