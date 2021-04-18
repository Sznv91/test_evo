package ru.softvillage.test_evo;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import java.math.RoundingMode;

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
            BigDecimal totalDigit = BigDecimal.ZERO; //Общая стоимость
            BigDecimal totalDiscount = BigDecimal.ZERO; //Скидка
            BigDecimal totalPricePositionWithDiscount = BigDecimal.ZERO;
            Receipt receipt = viewModel.getReceipt();

            for (Position position : receipt.getPositions()) {
                totalPricePositionWithDiscount = totalPricePositionWithDiscount.add(position.getTotal(BigDecimal.ZERO));
                totalDigit = totalDigit.add(position.getTotalWithoutDiscounts());
                totalDiscount = totalDiscount.add(position.getDiscountPositionSum());
            }
            totalDiscount = totalDiscount.add(receipt.getDiscount());


            BigDecimal finalTotalDigit = totalDigit;
            BigDecimal finalTotalDiscount = totalDiscount;

            ////////////////////////////////////////
            StringBuilder ndsToView = new StringBuilder();
            if (!receipt.getDiscount().equals(BigDecimal.ZERO)) {
                BigDecimal percent = totalPricePositionWithDiscount
                        .divide(
                                totalPricePositionWithDiscount
                                        .subtract(receipt.getPayments().get(0).getValue())
                                , 8, RoundingMode.HALF_UP);


                for (Position position : receipt.getPositions()) {
                    if (position.getTaxNumber() != null) {
                        BigDecimal pricePositionWithTotalDiscount = position.getTotal(BigDecimal.ZERO).subtract(position.getTotal(BigDecimal.ZERO).divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP).multiply(percent));
                        String nds_20 = pricePositionWithTotalDiscount.divide(BigDecimal.valueOf(1.2), 8, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(0.2))
                                .multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).toPlainString();
                        if (ndsToView.length() == 0) {
                            ndsToView.append(nds_20);
                        } else {
                            ndsToView.append("\r\n").append(nds_20);
                        }

                    }
                }
            }

            ////////////////////////////////////////

            getActivity().runOnUiThread(() -> {
                setDisplayData(
                        receipt.getHeader().getNumber(),
                        String.valueOf(finalTotalDigit),
                        String.valueOf(finalTotalDiscount),
                        String.valueOf(receipt.getPayments().get(0).getValue()),
                        ndsToView.toString()
                );
            });




        }).start();

    }

    private void setDisplayData(String dsaleNumber, String dtotalCost, String ddiscount, String dtotal, String dnds) {
        TextView saleNumber = getView().findViewById(R.id.sale_number);
        TextView totalCost = getView().findViewById(R.id.total_cost);
        TextView discount = getView().findViewById(R.id.discount);
        TextView total = getView().findViewById(R.id.total);
        TextView nds = getView().findViewById(R.id.nds);


        saleNumber.setText(dsaleNumber);
        totalCost.setText(dtotalCost);
        discount.setText(ddiscount);
        total.setText(dtotal);
        nds.setText(dnds);
    }
}