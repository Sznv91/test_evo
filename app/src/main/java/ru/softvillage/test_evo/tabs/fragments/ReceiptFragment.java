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
import ru.softvillage.test_evo.tabs.viewModel.ReceiptViewModel;

public class ReceiptFragment extends Fragment {
    private ReceiptViewModel mViewModel;

    public static ReceiptFragment newInstance() {
        return new ReceiptFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(ReceiptViewModel.class);
        mViewModel.setContext(getContext());
        return inflater.inflate(R.layout.receipt_fragment, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.setContext(null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mViewModel.setContext(getContext());
    }

    ///////////////////////////////////////////////////////////////////////////////////

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initRecyclerView() {
        RecyclerView recycler = getView().findViewById(R.id.receipt_list_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
        mViewModel.setLinearLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getContext().getDrawable(R.drawable.line_divider));
        recycler.addItemDecoration(divider);

        recycler.setAdapter(mViewModel.getAdapter());
    }

}