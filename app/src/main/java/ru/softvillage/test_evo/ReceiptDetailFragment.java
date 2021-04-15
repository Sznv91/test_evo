package ru.softvillage.test_evo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;

import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.Receipt;
import ru.softvillage.test_evo.tabs.viewModel.ReceiptDetailViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiptDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiptDetailFragment extends Fragment {
    ReceiptDetailViewModel viewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReceiptDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReceiptDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReceiptDetailFragment newInstance(String param1, String param2) {
        ReceiptDetailFragment fragment = new ReceiptDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_receipt_detail, container, false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressLint("LongLogTag")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ReceiptDetailViewModel.class);
        viewModel.setReceiptCloudId(mParam2);

        RecyclerView recycler = getView().findViewById(R.id.position_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);

        recycler.setAdapter(viewModel.getAdapter());


        new Thread(() -> {
            while (viewModel.getReceipt() == null) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            BigDecimal totalDigit = BigDecimal.ZERO;
            BigDecimal totalDiscount = BigDecimal.ZERO;
            Receipt receipt = viewModel.getReceipt();

            for (Position position : receipt.getPositions()) {
                totalDigit = totalDigit.add(position.getTotalWithoutDiscounts());
                totalDiscount = totalDiscount.add(position.getDiscountPositionSum());
            }
            totalDiscount = totalDiscount.add(receipt.getDiscount());


            BigDecimal finalTotalDigit = totalDigit;
            BigDecimal finalTotalDiscount = totalDiscount;
            getActivity().runOnUiThread(() -> {
                setDisplayData(
                        receipt.getHeader().getNumber(),
                        String.valueOf(finalTotalDigit),
                        String.valueOf(finalTotalDiscount),
                        String.valueOf(receipt.getPayments().get(0).getValue())
                );
            });

        }).start();

    }

    private void setDisplayData(String dsaleNumber,String dtotalCost,String ddiscount,String dtotal){
        TextView saleNumber = getView().findViewById(R.id.sale_number);
        TextView totalCost = getView().findViewById(R.id.total_cost);
        TextView discount = getView().findViewById(R.id.discount);
        TextView total = getView().findViewById(R.id.total);

        saleNumber.setText(dsaleNumber);
        totalCost.setText(dtotalCost);
        discount.setText(ddiscount);
        total.setText(dtotal);
    }
}