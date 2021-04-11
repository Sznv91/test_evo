package ru.softvillage.test_evo.tabs.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptEntity;
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
        mViewModel = new ViewModelProvider(this).get(ReceiptViewModel.class);
        return inflater.inflate(R.layout.receipt_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ReceiptViewModel.class);
        // TODO: Use the ViewModel
    }
    ///////////////////////////////////////////////////////////////////////////////////

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initRecyclerView() {
        RecyclerView recycler = getView().findViewById(R.id.receipt_list_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getDrawable(R.drawable.line_divider));
        recycler.addItemDecoration(divider);

        recycler.setAdapter(mViewModel.getAdapter());
    }

    public interface itemClickInterface {
        void clickClick(ReceiptEntity recipientEntity);
    }

}