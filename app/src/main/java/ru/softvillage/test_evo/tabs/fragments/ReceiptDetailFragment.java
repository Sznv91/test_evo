package ru.softvillage.test_evo.tabs.fragments;

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
import java.util.HashMap;
import java.util.Map;

import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.TaxNumber;
import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.R;
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
    private String receiptSoftVillageId;

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
            receiptSoftVillageId = getArguments().getString(ARG_PARAM2);
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
        viewModel.setReceiptCloudId(receiptSoftVillageId);

        RecyclerView recycler = getView().findViewById(R.id.position_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);

        recycler.setAdapter(viewModel.getAdapter());

        TextView sessionId = view.findViewById(R.id.session_id);
        EvoApp.getInstance().getDbHelper().getSessionId(Long.parseLong(receiptSoftVillageId), receivedSessionId -> {
            getActivity().runOnUiThread(() -> sessionId.setText(String.valueOf(receivedSessionId)));
        });

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
            /**
             * String - тип НДС: 20%
             *                   10%
             *                   20/120
             *                   10/110
             * BigDecimal - сумма со всех позиций одинакового типа НДС.
             */
            Map<String, BigDecimal> ndsData = new HashMap<>();

            StringBuilder ndsDigit = new StringBuilder();
            StringBuilder ndsType = new StringBuilder();
            BigDecimal percent = BigDecimal.ZERO;
            if (!receipt.getDiscount().equals(BigDecimal.ZERO)) {
                BigDecimal onePercentTotalPrice = totalPricePositionWithDiscount.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                percent = receipt.getDiscount().divide(onePercentTotalPrice, 2, RoundingMode.HALF_UP);
            }


            for (Position position : receipt.getPositions()) {

                if (position.getTaxNumber() != null) {
                    BigDecimal pricePositionWithTotalDiscount = position.getTotal(BigDecimal.ZERO).subtract(position.getTotal(BigDecimal.ZERO).divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP).multiply(percent));
                    BigDecimal nds_20 = pricePositionWithTotalDiscount.divide(BigDecimal.valueOf(1.2), 8, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(0.2))
                            .multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)/*.toPlainString()*/;
                    BigDecimal nds_10 = pricePositionWithTotalDiscount.divide(BigDecimal.valueOf(1.1), 8, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(0.1))
                            .multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)/*.toPlainString()*/;
                    BigDecimal nds_0 = pricePositionWithTotalDiscount.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                    if (position.getTaxNumber().equals(TaxNumber.VAT_10)) {
                        if (ndsData.get(TaxNumber.VAT_10.name()) != null) {
                            BigDecimal tNds = ndsData.get(TaxNumber.VAT_10.name());
                            tNds = tNds.add(nds_10);
                            ndsData.put(TaxNumber.VAT_10.name(), tNds);
                        } else {
                            ndsData.put(TaxNumber.VAT_10.name(), nds_10);
                        }
                    }

                    if (position.getTaxNumber().equals(TaxNumber.VAT_10_110)) {
                        if (ndsData.get(TaxNumber.VAT_10_110.name()) != null) {
                            BigDecimal tNds = ndsData.get(TaxNumber.VAT_10_110.name());
                            tNds = tNds.add(nds_10);
                            ndsData.put(TaxNumber.VAT_10_110.name(), tNds);
                        } else {
                            ndsData.put(TaxNumber.VAT_10_110.name(), nds_10);
                        }
                    }

                    if (position.getTaxNumber().equals(TaxNumber.VAT_18)) {
                        if (ndsData.get(TaxNumber.VAT_18.name()) != null) {
                            BigDecimal tNds = ndsData.get(TaxNumber.VAT_18.name());
                            tNds = tNds.add(nds_20);
                            ndsData.put(TaxNumber.VAT_18.name(), tNds);
                        } else {
                            ndsData.put(TaxNumber.VAT_18.name(), nds_20);
                        }
                    }
                    if (position.getTaxNumber().equals(TaxNumber.VAT_18_118)) {
                        if (ndsData.get(TaxNumber.VAT_18_118.name()) != null) {
                            BigDecimal tNds = ndsData.get(TaxNumber.VAT_18_118.name());
                            tNds = tNds.add(nds_20);
                            ndsData.put(TaxNumber.VAT_18_118.name(), tNds);
                        } else {
                            ndsData.put(TaxNumber.VAT_18_118.name(), nds_20);
                        }
                    }

                    if (position.getTaxNumber().equals(TaxNumber.VAT_0)) {
                        if (ndsData.get(TaxNumber.VAT_0.name()) != null) {
                            BigDecimal tNds = ndsData.get(TaxNumber.VAT_0.name());
                            tNds = tNds.add(nds_0);
                            ndsData.put(TaxNumber.VAT_0.name(), tNds);
                        } else {
                            ndsData.put(TaxNumber.VAT_0.name(), nds_0);
                        }
                    }

                    if (position.getTaxNumber().equals(TaxNumber.NO_VAT)) {
                        if (ndsData.get(TaxNumber.NO_VAT.name()) != null) {
                            BigDecimal tNds = ndsData.get(TaxNumber.NO_VAT.name());
                            tNds = tNds.add(nds_0);
                            ndsData.put(TaxNumber.NO_VAT.name(), tNds);
                        } else {
                            ndsData.put(TaxNumber.NO_VAT.name(), nds_0);
                        }
                    }


                }
            }
            for (Map.Entry<String, BigDecimal> entry : ndsData.entrySet()) {
                if (ndsDigit.length() == 0) {
                    ndsDigit.append(entry.getValue().toPlainString());
                    ndsType.append("Сумма").append(ndsTypeChanger(entry.getKey()));
                } else {
                    ndsDigit.append("\r\n").append(entry.getValue().toPlainString());
                    ndsType.append("\r\n").append("Сумма").append(ndsTypeChanger(entry.getKey()));
                }
            }


            ////////////////////////////////////////

            getActivity().runOnUiThread(() -> {
                setDisplayData(
                        receipt.getHeader().getNumber(),
                        String.valueOf(finalTotalDigit),
                        String.valueOf(finalTotalDiscount),
                        String.valueOf(receipt.getPayments().get(0).getValue()),
                        ndsDigit.toString(),
                        ndsType.toString()
                );
            });


        }).start();

    }

    private void setDisplayData(String dsaleNumber, String dtotalCost, String ddiscount, String dtotal, String dndsDigit, String dndsType) {
        TextView saleNumber = getView().findViewById(R.id.sale_number);
        TextView totalCost = getView().findViewById(R.id.total_cost);
        TextView discount = getView().findViewById(R.id.discount);
        TextView total = getView().findViewById(R.id.total);
        TextView ndsDigit = getView().findViewById(R.id.nds);
        TextView ndsType = getView().findViewById(R.id.nds_type);


        saleNumber.setText(dsaleNumber);
        totalCost.setText(dtotalCost);
        discount.setText(ddiscount);
        total.setText(dtotal);
        ndsDigit.setText(dndsDigit);
        ndsType.setText(dndsType);
    }

    private String ndsTypeChanger(String ndsType) {
        String ten = TaxNumber.VAT_10.name();
        String twenty = TaxNumber.VAT_18.name();
        String ten_110 = TaxNumber.VAT_10_110.name();
        String twenty_120 = TaxNumber.VAT_18_118.name();
        String zero = TaxNumber.VAT_0.name();
        final String sumNds = " НДС ";

        if (ndsType.equals(ten)) {
            return sumNds + "10%";
        }
        if (ndsType.equals(twenty)) {
            return sumNds + "20%";
        }
        if (ndsType.equals(ten_110)) {
            return sumNds + "10/110";
        }
        if (ndsType.equals(twenty_120)) {
            return sumNds + "20/120";
        }
        if (ndsType.equals(zero)) {
            return sumNds + "0%";
        }
        return " БЕЗ НДС";
    }
}