package ru.softvillage.test_evo.tabs.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.tabs.viewModel.ReceiptViewModel;

public class ReceiptFragment extends Fragment {

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ReceiptViewModel.class);
        // TODO: Use the ViewModel
    }

}