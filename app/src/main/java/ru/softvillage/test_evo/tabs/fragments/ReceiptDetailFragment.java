package ru.softvillage.test_evo.tabs.fragments;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.evotor.framework.receipt.FiscalReceipt;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.ReceiptApi;
import ru.evotor.framework.receipt.TaxNumber;
import ru.evotor.framework.users.User;
import ru.evotor.framework.users.UserApi;
import ru.evotor.query.Cursor;
import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.roomDb.DbHelper;
import ru.softvillage.test_evo.roomDb.Entity.ReceiptEntity;
import ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter;
import ru.softvillage.test_evo.tabs.viewModel.ReceiptDetailViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiptDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiptDetailFragment extends Fragment {
    ReceiptDetailViewModel viewModel;

    /**
     * Элементы несущие информационную нагрузку.
     */
    private TextView saleNumber;
    private TextView totalCost;
    private TextView discount;
    private TextView total;
    private TextView ndsDigit;
    private TextView ndsType;
    private TextView sessionId;

    private ImageView diplomat_icon,
            user_icon,
            location_icon;

    private ScrollView receipt_detail_layout;
    FrameLayout receipt_detail_title_holder;
    private View divider,
            divider_cred,
            divider_shop_info;

    private TextView receipt_detail_title,
            receipt_type,
            title_total_cost,
            title_discount,
            title_total,
            title_payment,

    shop_name,
            shop_address_city,
            shop_address_street,
            title_user_name,
            user_name,
            title_payment_location,
            payment_location_address_city,
            payment_location_address_street;

    private String firstName, secondName = "";

    private static final String ARG_PARAM2 = "param2";

    private String receiptSoftVillageId;

    public ReceiptDetailFragment() {
        SessionPresenter.getInstance().getDrawerManager().showUpButton(true);
        // Required empty public constructor
    }

    public static ReceiptDetailFragment newInstance(String param2) {
        ReceiptDetailFragment fragment = new ReceiptDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            receiptSoftVillageId = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_receipt_detail, container, false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressLint("LongLogTag")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        saleNumber = getView().findViewById(R.id.sale_number);
        sessionId = view.findViewById(R.id.session_id);
        totalCost = getView().findViewById(R.id.total_cost);
        discount = getView().findViewById(R.id.discount);
        total = getView().findViewById(R.id.total);
        ndsDigit = getView().findViewById(R.id.nds);
        ndsType = getView().findViewById(R.id.nds_type);

        receipt_detail_title_holder = view.findViewById(R.id.receipt_detail_title_holder);
        receipt_detail_layout = view.findViewById(R.id.receipt_detail_layout);
        divider = view.findViewById(R.id.divider);
        divider_cred = view.findViewById(R.id.divider_cred);
        receipt_detail_title = view.findViewById(R.id.receipt_detail_title);
        receipt_type = view.findViewById(R.id.receipt_type);
        title_total_cost = view.findViewById(R.id.title_total_cost);
        title_discount = view.findViewById(R.id.title_discount);
        title_total = view.findViewById(R.id.title_total);
        title_payment = view.findViewById(R.id.title_payment);

        diplomat_icon = view.findViewById(R.id.diplomat_icon);
        user_icon = view.findViewById(R.id.user_icon);
        location_icon = view.findViewById(R.id.location_icon);
        shop_name = view.findViewById(R.id.shop_name);
        shop_address_city = view.findViewById(R.id.shop_address_city);
        shop_address_street = view.findViewById(R.id.shop_address_street);
        title_user_name = view.findViewById(R.id.title_user_name);
        user_name = view.findViewById(R.id.user_name);
        title_payment_location = view.findViewById(R.id.title_payment_location);
        payment_location_address_city = view.findViewById(R.id.payment_location_address_city);
        payment_location_address_street = view.findViewById(R.id.payment_location_address_street);
        initColour(SessionPresenter.getInstance().getCurrentTheme());

        viewModel = new ViewModelProvider(this).get(ReceiptDetailViewModel.class);
        viewModel.setReceiptCloudId(receiptSoftVillageId);

        //todo удалить фейковый добавлятель адреса точки продаж
        addFakeShopAddress();
        RecyclerView recycler = getView().findViewById(R.id.position_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);

        recycler.setAdapter(viewModel.getAdapter());

        EvoApp.getInstance().getDbHelper().getSessionId(Long.parseLong(receiptSoftVillageId), new DbHelper.AsyncCallback() {
            @Override
            public void sessionRequest(long sessionIdReceived) {
                getActivity().runOnUiThread(() -> sessionId.setText(String.format(getActivity().getString(R.string.receipt_detail_session_id), sessionIdReceived)));
            }

            @Override
            public void receiptRequest(ReceiptEntity entity) {

            }
        });

        new Thread(() -> {
            while (viewModel.getReceipt() == null) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Receipt receipt = viewModel.getReceipt();

            List<User> users = UserApi.getAllUsers(getContext());
            EvoApp.getInstance().getDbHelper().getById(Long.parseLong(receiptSoftVillageId), new DbHelper.AsyncCallback() {
                @Override
                public void sessionRequest(long sessionId) {

                }

                @Override
                public void receiptRequest(ReceiptEntity entity) {
                    for (User user : users) {
                        Cursor<FiscalReceipt> fiscalReceiptCursor = ReceiptApi.getFiscalReceipts(getContext(), receipt.getHeader().getUuid());
                        while (fiscalReceiptCursor.moveToNext()){
                            Log.d(EvoApp.TAG + "_receipt_detail_just_receipt", fiscalReceiptCursor.getValue().toString());
                        }
                        fiscalReceiptCursor.close();


                        Log.d(EvoApp.TAG + "_receipt_detail", user.toString());
                        Log.d(EvoApp.TAG + "_receipt_detail_just_receipt", receipt.toString());
                        if (entity.getUserUuid().equals(user.getUuid())) {

                            if (!TextUtils.isEmpty(user.getFirstName())) {
                                firstName = user.getFirstName();
                            }
                            if (!TextUtils.isEmpty(user.getSecondName())) {
                                secondName = user.getSecondName();
                            }
                        }
                    }
                }
            });
//            user_name.setText( receipt.getPayments().get(0).getAccountId() + " + " +receipt.getPayments().get(0).getUuid());

            BigDecimal totalDigit = BigDecimal.ZERO; //Общая стоимость
            BigDecimal totalDiscount = BigDecimal.ZERO; //Скидка
            BigDecimal totalPricePositionWithDiscount = BigDecimal.ZERO;


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
                        finalTotalDigit
                        /*String.valueOf(finalTotalDigit)*/,
//                        String.valueOf(finalTotalDiscount),
                        finalTotalDiscount,
                        receipt.getPayments().get(0).getValue(),
//                        String.valueOf(receipt.getPayments().get(0).getValue()),
                        ndsDigit.toString(),
                        ndsType.toString()
                );

                user_name.setText(String.format("%s %s", firstName, secondName));
                shop_name.setText(SessionPresenter.getInstance().getShop_name());
                shop_address_city.setText(SessionPresenter.getInstance().getShop_address_city());
                shop_address_street.setText(SessionPresenter.getInstance().getShop_address_street());
                payment_location_address_city.setText(SessionPresenter.getInstance().getPayment_location_address_city());
                payment_location_address_street.setText(SessionPresenter.getInstance().getPayment_location_address_street());
            });


        }).start();

    }

    private void setDisplayData(String dsaleNumber, BigDecimal dtotalCost, BigDecimal ddiscount, BigDecimal dtotal, String dndsDigit, String dndsType) {
        saleNumber.setText(String.format(getActivity().getString(R.string.receipt_detail_sale_num), dsaleNumber));
        totalCost.setText(String.format("=%.02f", dtotalCost).replace(",", "."));
        discount.setText(String.format("=%.02f", ddiscount).replace(",", "."));
        total.setText(String.format("=%.02f", dtotal).replace(",", "."));
        ndsDigit.setText(String.format("=%s", dndsDigit));
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

    private void initColour(int themeType) {
        if (themeType == SessionPresenter.THEME_LIGHT) {
            receipt_detail_title_holder.setBackgroundColor(ContextCompat.getColor(receipt_detail_layout.getContext(), R.color.white));
            receipt_detail_layout.setBackgroundColor(ContextCompat.getColor(receipt_detail_layout.getContext(), R.color.white));
            divider.setBackgroundColor(ContextCompat.getColor(divider.getContext(), R.color.light_divider));
            divider_cred.setBackgroundColor(ContextCompat.getColor(divider_cred.getContext(), R.color.light_divider));
//            divider_shop_info.setBackgroundColor(ContextCompat.getColor(divider_shop_info.getContext(), R.color.light_divider));

            saleNumber.setTextColor(ContextCompat.getColor(saleNumber.getContext(), R.color.black));
            saleNumber.setAlpha(Float.parseFloat("0.3"));
            sessionId.setTextColor(ContextCompat.getColor(sessionId.getContext(), R.color.black));
            sessionId.setAlpha(Float.parseFloat("0.3"));
            totalCost.setTextColor(ContextCompat.getColor(totalCost.getContext(), R.color.color20));
            discount.setTextColor(ContextCompat.getColor(discount.getContext(), R.color.color20));
            total.setTextColor(ContextCompat.getColor(total.getContext(), R.color.color_20_alpha));
            ndsDigit.setTextColor(ContextCompat.getColor(ndsDigit.getContext(), R.color.color29));
            ndsType.setTextColor(ContextCompat.getColor(ndsType.getContext(), R.color.color29));

            receipt_detail_title.setTextColor(ContextCompat.getColor(receipt_detail_title.getContext(), R.color.color19));
            receipt_type.setTextColor(ContextCompat.getColor(receipt_type.getContext(), R.color.color_c0));
            title_total_cost.setTextColor(ContextCompat.getColor(title_total_cost.getContext(), R.color.color_20_alpha));
            title_discount.setTextColor(ContextCompat.getColor(title_discount.getContext(), R.color.color20));
            title_total.setTextColor(ContextCompat.getColor(title_total.getContext(), R.color.color20));

            diplomat_icon.setColorFilter(ContextCompat.getColor(diplomat_icon.getContext(), R.color.color_c0), PorterDuff.Mode.SRC_IN);
            user_icon.setColorFilter(ContextCompat.getColor(user_icon.getContext(), R.color.color_c0), PorterDuff.Mode.SRC_IN);
            location_icon.setColorFilter(ContextCompat.getColor(location_icon.getContext(), R.color.color_c0), PorterDuff.Mode.SRC_IN);

            shop_name.setTextColor(ContextCompat.getColor(shop_name.getContext(), R.color.color20));
            title_user_name.setTextColor(ContextCompat.getColor(title_user_name.getContext(), R.color.color20));
            title_payment_location.setTextColor(ContextCompat.getColor(title_payment_location.getContext(), R.color.color20));

        } else {

            receipt_detail_title_holder.setBackgroundColor(ContextCompat.getColor(receipt_detail_layout.getContext(), R.color.black));
            receipt_detail_layout.setBackgroundColor(ContextCompat.getColor(receipt_detail_layout.getContext(), R.color.color31));
            divider.setBackgroundColor(ContextCompat.getColor(divider.getContext(), R.color.dark_divider));
            divider_cred.setBackgroundColor(ContextCompat.getColor(divider_cred.getContext(), R.color.dark_divider));
//            divider_shop_info.setBackgroundColor(ContextCompat.getColor(divider_shop_info.getContext(), R.color.dark_divider));


            saleNumber.setTextColor(ContextCompat.getColor(saleNumber.getContext(), R.color.color29));
            sessionId.setTextColor(ContextCompat.getColor(sessionId.getContext(), R.color.color29));
            totalCost.setTextColor(ContextCompat.getColor(totalCost.getContext(), R.color.color29));
            discount.setTextColor(ContextCompat.getColor(discount.getContext(), R.color.color29));
            total.setTextColor(ContextCompat.getColor(total.getContext(), R.color.color_c4));
            ndsDigit.setTextColor(ContextCompat.getColor(ndsDigit.getContext(), R.color.color29));
            ndsType.setTextColor(ContextCompat.getColor(ndsType.getContext(), R.color.color29));

            receipt_detail_title.setTextColor(ContextCompat.getColor(receipt_detail_title.getContext(), R.color.color_c4));
            receipt_type.setTextColor(ContextCompat.getColor(receipt_type.getContext(), R.color.color29));
            title_total_cost.setTextColor(ContextCompat.getColor(title_total_cost.getContext(), R.color.color29));
            title_discount.setTextColor(ContextCompat.getColor(title_discount.getContext(), R.color.color29));
            title_total.setTextColor(ContextCompat.getColor(title_total.getContext(), R.color.color_c4));

            diplomat_icon.setColorFilter(ContextCompat.getColor(diplomat_icon.getContext(), R.color.color_c4), PorterDuff.Mode.SRC_IN);
            user_icon.setColorFilter(ContextCompat.getColor(user_icon.getContext(), R.color.color_c4), PorterDuff.Mode.SRC_IN);
            location_icon.setColorFilter(ContextCompat.getColor(location_icon.getContext(), R.color.color_c4), PorterDuff.Mode.SRC_IN);

            shop_name.setTextColor(ContextCompat.getColor(shop_name.getContext(), R.color.color_c4));
            title_user_name.setTextColor(ContextCompat.getColor(title_user_name.getContext(), R.color.color_c4));
            title_payment_location.setTextColor(ContextCompat.getColor(title_payment_location.getContext(), R.color.color_c4));

        }
        shop_address_city.setTextColor(ContextCompat.getColor(shop_address_city.getContext(), R.color.color29));
        shop_address_street.setTextColor(ContextCompat.getColor(shop_address_street.getContext(), R.color.color29));
        user_name.setTextColor(ContextCompat.getColor(user_name.getContext(), R.color.color29));
        payment_location_address_city.setTextColor(ContextCompat.getColor(payment_location_address_city.getContext(), R.color.color29));
        payment_location_address_street.setTextColor(ContextCompat.getColor(payment_location_address_street.getContext(), R.color.color29));
        title_payment.setTextColor(ContextCompat.getColor(title_payment.getContext(), R.color.color29));
    }

    private void addFakeShopAddress() {
        SessionPresenter.getInstance().setShopInfo(
                "Софт-Вилладж",
                "346500, Ростовская обл. г. Шахты",
                "Ул. Шевченко 141",
                "190000, г. Санкт-Петербург",
                "Большая Морская улица, 30"
        );
    }
}