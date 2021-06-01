package ru.softvillage.test_evo.roomDb;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Transaction;

import java.util.List;

import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.roomDb.Entity.fiscalized.GoodEntity;
import ru.softvillage.test_evo.roomDb.Entity.fiscalized.PartialReceiptPrinted;
import ru.softvillage.test_evo.roomDb.Entity.fiscalized.ReceiptEntity;
import ru.softvillage.test_evo.roomDb.Entity.fiscalized.ReceiptWithGoodEntity;
import ru.softvillage.test_evo.roomDb.Entity.fromNetwork.OrderDb;
import ru.softvillage.test_evo.roomDb.Entity.fromNetwork.OrderDbWithGoods;

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

    /*public ArrayList<ReceiptEntity> getAllNotFiscalized() {
        return dataBase.receiptDao().getAllNotFiscalized();
    }*/

    ////////////////////////////////////////////////////////////////////

    /**
     * Network entity part
     */
    public List<OrderDbWithGoods> getOrderDbWithGoods() {
        return dataBase.receiptDao().getOrderDbWithGoods();
    }

    @SuppressLint("LongLogTag")
    @Transaction
    public void createOrderDbWithGoods(OrderDbWithGoods entity) {
        LocalDataBase.databaseWriteExecutor.execute(() -> {
            dataBase.receiptDao().createOrderDb(entity.getOrderDb());
            dataBase.receiptDao().createGoodDb(entity.getGoodDbEntities());
            Log.d(EvoApp.TAG + "_" + getClass().getSimpleName() + "_createOrderDbWithGoods", "Успешно сохранили в БД: " + entity.toString());
        });
    }

    public void removeOrderDbWithGoods(OrderDb entity, removeCallBack callBack) {
        LocalDataBase.databaseWriteExecutor.execute(() -> {
            dataBase.receiptDao().removeOrderDb(entity);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (callBack != null) {
                callBack.removeFinish();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////

    public interface removeCallBack {
        void removeFinish();
    }

    public interface AsyncCallback {
        void sessionRequest(long sessionId);

        void receiptRequest(ReceiptEntity entity);
    }
}
