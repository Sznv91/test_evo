package ru.softvillage.test_evo.tabs;

import android.app.Application;

import androidx.lifecycle.ViewModel;

import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.tabs.fragments.recyclerView.ReceiptItemAdapter;

public class ReceiptDetailViewModel extends ViewModel {
    Application app = EvoApp.getInstance();
    ReceiptItemAdapter adapter;

}
