package ru.softvillage.test_evo.tabs.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.joda.time.LocalDateTime;

import java.math.BigDecimal;
import java.util.List;

import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.roomDb.BigDecimalConverter;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptPrinted;
import ru.softvillage.test_evo.tabs.viewModel.ReceiptViewModel;

public class ReceiptFragment extends Fragment {
    private int counter = 0;

    private ReceiptViewModel mViewModel;

    public static ReceiptFragment newInstance() {
        return new ReceiptFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.receipt_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((Button) view.findViewById(R.id.print_to_log)).setOnClickListener(v -> callToDb());
        ((Button) view.findViewById(R.id.add_record_to_db)).setOnClickListener(v -> addRecord());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ReceiptViewModel.class);
        // TODO: Use the ViewModel
    }

    ///////////////////////////////////////////////////////
    @SuppressLint("LongLogTag")
    private void callToDb() {
       EvoApp.getInstance().getDbHelper().getAll().observe(this, receiptPrinters -> {
           for (ReceiptPrinted receipt : receiptPrinters){
               Log.d(EvoApp.TAG + "_RoomDb", receipt.toString());
           }
       });
    }


    private void addRecord() {
        ReceiptPrinted data = new ReceiptPrinted();
        data.setId(++counter);
        data.setPrice(BigDecimal.valueOf(123).add(BigDecimal.valueOf(counter)));
        data.setReceived(LocalDateTime.now());
        EvoApp.getInstance().getDbHelper().insertReceiptToDb(data);
    }


}