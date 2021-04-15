package ru.softvillage.test_evo.tabs.viewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.List;

import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.ReceiptDetailFragment;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptEntity;
import ru.softvillage.test_evo.tabs.fragments.recyclerView.ReceiptItemAdapter;

public class ReceiptViewModel extends ViewModel {
    Application app = EvoApp.getInstance();
    ReceiptItemAdapter adapter;
    @SuppressLint("LongLogTag")
    Observer<List<ReceiptEntity>> observer = receiptEntities -> {
        getAdapter().setItems(receiptEntities);
        Log.d(EvoApp.TAG + "_Db", receiptEntities.toString());
    };

    @SuppressLint("LongLogTag")
    public ReceiptItemAdapter getAdapter() {
        if (adapter == null) {
            adapter = new ReceiptItemAdapter(LayoutInflater.from(app.getApplicationContext()), recipientEntity -> {
                Log.d(EvoApp.TAG + "_Recycler", "click - click " + recipientEntity.getReceiptNumber());
                Fragment fragment = ReceiptDetailFragment.newInstance(String.valueOf(recipientEntity.getReceiptNumber()), String.valueOf(recipientEntity.getId()));
                EvoApp.getInstance().getFragmentDispatcher().replaceFragment(fragment);
            });
            observeOnChangeDb();
        }
        return adapter;
    }

    private void observeOnChangeDb() {
        EvoApp.getInstance().getDbHelper().getAll().observeForever(observer);
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onCleared() {
        Log.d(EvoApp.TAG + "_LifeCycle", "Отписались от LiveData");
        EvoApp.getInstance().getDbHelper().getAll().removeObserver(observer);
        super.onCleared();
    }

}