package ru.softvillage.fiscalizer.tabs.viewModel;

import android.view.LayoutInflater;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import lombok.Getter;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.softvillage.fiscalizer.EvoApp;
import ru.softvillage.fiscalizer.roomDb.Entity.fiscalized.ReceiptWithGoodEntity;
import ru.softvillage.fiscalizer.tabs.fragments.recyclerView.PositionGoodsItemAdapter;


public class ReceiptDetailViewModel extends ViewModel {
    private String receiptCloudId;
    private PositionGoodsItemAdapter adapter;
    @Getter
    Receipt receipt = null;

    Observer<ReceiptWithGoodEntity> observer = receipt -> {
        String receiptUuid = receipt.getReceiptEntity().getUuid();
        this.receipt = ReceiptApi.getReceipt(EvoApp.getInstance().getApplicationContext(), receiptUuid);
        updateAdapter();
    };

    public void setReceiptCloudId(String receiptCloudId) {
        this.receiptCloudId = receiptCloudId;
        EvoApp.getInstance().getDbHelper().getReceiptWithGoodEntity(Long.parseLong(receiptCloudId)).observeForever(observer);
    }

    private void updateAdapter() {
        adapter.setItems(receipt);
    }


    public PositionGoodsItemAdapter getAdapter() {
        adapter = new PositionGoodsItemAdapter(LayoutInflater.from(EvoApp.getInstance().getApplicationContext()));
        return adapter;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        EvoApp.getInstance().getDbHelper().getReceiptWithGoodEntity(Long.parseLong(receiptCloudId)).removeObserver(observer);
    }
}
