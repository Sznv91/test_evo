package ru.softvillage.test_evo.tabs.fragments;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.joda.time.LocalDateTime;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
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
import ru.softvillage.test_evo.roomDb.Entity.fiscalized.ReceiptEntity;
import ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter;
import ru.softvillage.test_evo.tabs.viewModel.ReceiptDetailViewModel;

import static android.graphics.Color.WHITE;

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
    private TextView tab_title_statistic_information;

    private TextView saleNumber;
    private TextView totalCost;
    private TextView discount;
    private TextView total;
    private TextView cash;
    private TextView ndsDigit;
    private TextView ndsType;
    private TextView sessionId;

    private ImageView diplomat_icon,
            user_icon,
            location_icon,
            qr_holder;

    private ScrollView receipt_detail_layout;
    private FrameLayout receipt_detail_title_holder,
            fragment_receipt_loader;
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
            address,
            title_user_name,
            user_name,
            title_payment_location,
            payment_location_address_city;

    private TextView
            title_sno,
            title_session_fm,
            title_fd_num,
            title_fp_num,
            title_fm_date,
            content_sno,
            title_rn_kkt,
            title_zn_kkt,
            title_site_fns,
            title_fn_num,
            title_inn_num;


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
        cash = view.findViewById(R.id.cash);
        ndsDigit = getView().findViewById(R.id.nds);
        ndsType = getView().findViewById(R.id.nds_type);

        receipt_detail_title_holder = view.findViewById(R.id.receipt_detail_title_holder);
        fragment_receipt_loader = view.findViewById(R.id.fragment_receipt_loader);
        receipt_detail_layout = view.findViewById(R.id.receipt_detail_layout);
        divider = view.findViewById(R.id.divider);
        divider_cred = view.findViewById(R.id.divider_cred);
        divider_shop_info = view.findViewById(R.id.divider_shop_info);
        receipt_detail_title = view.findViewById(R.id.receipt_detail_title);
        receipt_type = view.findViewById(R.id.receipt_type);
        title_total_cost = view.findViewById(R.id.title_total_cost);
        title_discount = view.findViewById(R.id.title_discount);
        title_total = view.findViewById(R.id.title_total);
        title_payment = view.findViewById(R.id.title_payment);

        diplomat_icon = view.findViewById(R.id.diplomat_icon);
        user_icon = view.findViewById(R.id.user_icon);
        location_icon = view.findViewById(R.id.location_icon);
        qr_holder = view.findViewById(R.id.qr_holder);
        shop_name = view.findViewById(R.id.shop_name);
        address = view.findViewById(R.id.address);
        title_user_name = view.findViewById(R.id.title_user_name);
        user_name = view.findViewById(R.id.user_name);
        title_payment_location = view.findViewById(R.id.title_payment_location);
        payment_location_address_city = view.findViewById(R.id.payment_place);

        title_sno = view.findViewById(R.id.title_sno);
        title_session_fm = view.findViewById(R.id.title_session_fm);
        title_fd_num = view.findViewById(R.id.title_fd_num);
        title_fp_num = view.findViewById(R.id.title_fp_num);
        title_fm_date = view.findViewById(R.id.title_fm_date);
        content_sno = view.findViewById(R.id.content_sno);
        title_rn_kkt = view.findViewById(R.id.title_rn_kkt);
        title_zn_kkt = view.findViewById(R.id.title_zn_kkt);
        title_site_fns = view.findViewById(R.id.title_site_fns);
        title_fn_num = view.findViewById(R.id.title_fn_num);
        title_inn_num = view.findViewById(R.id.title_inn_num);
        initColour(SessionPresenter.getInstance().getCurrentTheme());

        viewModel = new ViewModelProvider(this).get(ReceiptDetailViewModel.class);
        viewModel.setReceiptCloudId(receiptSoftVillageId);

        /*//todo удалить фейковый добавлятель адреса точки продаж
        addFakeShopAddress();*/
        /**
         * Экран загрузки
         */
        fragment_receipt_loader.setVisibility(View.VISIBLE);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            fragment_receipt_loader.setVisibility(View.GONE);
        }, 2000);

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
                        if (entity.getUserUuid().equals(user.getUuid())) {
                            if (!TextUtils.isEmpty(user.getFirstName())) {
                                firstName = user.getFirstName();
                            }
                            if (!TextUtils.isEmpty(user.getSecondName())) {
                                secondName = user.getSecondName();
                            }
                        }
                    }
                    getActivity().runOnUiThread(() -> {
                        if (TextUtils.isEmpty(firstName)) {
                            firstName = "Кассир";
                        }
                        user_name.setText(String.format("%s %s", firstName, secondName));
                        shop_name.setText(entity.getShop_name());
                        address.setText(entity.getAddress());
                        payment_location_address_city.setText(entity.getPayment_place());

                        content_sno.setText(entity.getSno_type());
                        String rn = "";
                        if (entity.getRn_kkt().length() > 1) {
                            for (int i = 0; i < entity.getRn_kkt().length(); i++) {
                                if (!entity.getRn_kkt().substring(i, i + 1).equals(" ")) {
                                    rn += entity.getRn_kkt().substring(i, i + 1);
                                }
                            }
                        } else {
                            rn = "0000000000000000";
                        }

                        title_rn_kkt.setText(String.format(getActivity().getString(R.string.title_rn_kkt), rn));
                        title_zn_kkt.setText(String.format(getActivity().getString(R.string.title_zn_kkt), entity.getZn_kkt()));
                        title_inn_num.setText(String.format(getActivity().getString(R.string.title_inn_num), entity.getInn()));
                    });
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

                Cursor<FiscalReceipt> fiscalReceiptCursor = ReceiptApi.getFiscalReceipts(getContext(), receipt.getHeader().getUuid());
                String toQrData;
                while (fiscalReceiptCursor.moveToNext()) {
                    title_session_fm.setText(String.format(getActivity().getString(R.string.title_session_fm), fiscalReceiptCursor.getValue().getSessionNumber() + 1));
                    title_fd_num.setText(String.format(getActivity().getString(R.string.title_fd_num), fiscalReceiptCursor.getValue().getDocumentNumber()));
                    title_fp_num.setText(String.format(getActivity().getString(R.string.title_fp_num), fiscalReceiptCursor.getValue().getFiscalIdentifier()));
                    LocalDateTime localDateTime = LocalDateTime.fromDateFields(fiscalReceiptCursor.getValue().getCreationDate());
                    title_fm_date.setText(localDateTime.toString("dd.MM.YY HH:mm"));
                    title_fn_num.setText(String.format(getActivity().getString(R.string.title_fn_num), fiscalReceiptCursor.getValue().getFiscalStorageNumber()));

                    toQrData = "t=" + localDateTime.toString("YYYYMMdd") + "T" + localDateTime.toString("HHmm") +
                            "&s=" + String.format("%.02f", receipt.getPayments().get(0).getValue()).replace(",", ".") +
                            "&fn=" + fiscalReceiptCursor.getValue().getFiscalStorageNumber() +
                            "&i=" + fiscalReceiptCursor.getValue().getDocumentNumber() +
                            "&fp=" + fiscalReceiptCursor.getValue().getFiscalIdentifier();
                    Bitmap barcode_bitmap = null;
                    try {
                        barcode_bitmap = encodeAsBitmap(toQrData, BarcodeFormat.QR_CODE, 150, 150);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    qr_holder.setImageBitmap(barcode_bitmap);
                    Log.d(EvoApp.TAG + "_receipt_detail_just_receipt", fiscalReceiptCursor.getValue().toString());
                }
                fiscalReceiptCursor.close();


            });


        }).start();

        tab_title_statistic_information = getActivity().findViewById(R.id.tab_title_statistic_information);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            tab_title_statistic_information.setText("Детали чека");
        }

    }

    private void setDisplayData(String dsaleNumber, BigDecimal dtotalCost, BigDecimal ddiscount, BigDecimal dtotal, String dndsDigit, String dndsType) {
        saleNumber.setText(String.format(getActivity().getString(R.string.receipt_detail_sale_num), dsaleNumber));
        totalCost.setText(String.format("= %.02f", dtotalCost).replace(",", "."));
        discount.setText(String.format("= %.02f", ddiscount).replace(",", "."));
        total.setText(String.format("= %.02f", dtotal).replace(",", "."));
        cash.setText(String.format("= %.02f", dtotal).replace(",", "."));
        ndsDigit.setText(String.format("= %s", dndsDigit));
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
            fragment_receipt_loader.setBackgroundColor(ContextCompat.getColor(fragment_receipt_loader.getContext(), R.color.background_lt));
            receipt_detail_title_holder.setBackgroundColor(ContextCompat.getColor(receipt_detail_layout.getContext(), R.color.background_lt));
            receipt_detail_layout.setBackgroundColor(ContextCompat.getColor(receipt_detail_layout.getContext(), R.color.background_lt));
            divider.setBackgroundColor(ContextCompat.getColor(divider.getContext(), R.color.divider_lt));
            divider_cred.setBackgroundColor(ContextCompat.getColor(divider_cred.getContext(), R.color.divider_lt));
            divider_shop_info.setBackgroundColor(ContextCompat.getColor(divider_shop_info.getContext(), R.color.divider_lt));

            saleNumber.setTextColor(ContextCompat.getColor(saleNumber.getContext(), R.color.active_fonts_lt));
            sessionId.setTextColor(ContextCompat.getColor(sessionId.getContext(), R.color.active_fonts_lt));
            totalCost.setTextColor(ContextCompat.getColor(totalCost.getContext(), R.color.active_fonts_lt));
            discount.setTextColor(ContextCompat.getColor(discount.getContext(), R.color.active_fonts_lt));
            total.setTextColor(ContextCompat.getColor(total.getContext(), R.color.fonts_lt));
            ndsDigit.setTextColor(ContextCompat.getColor(ndsDigit.getContext(), R.color.active_fonts_lt));
            ndsType.setTextColor(ContextCompat.getColor(ndsType.getContext(), R.color.active_fonts_lt));
            title_payment.setTextColor(ContextCompat.getColor(title_payment.getContext(), R.color.active_fonts_lt));
            cash.setTextColor(ContextCompat.getColor(cash.getContext(), R.color.active_fonts_lt));


            receipt_detail_title.setTextColor(ContextCompat.getColor(receipt_detail_title.getContext(), R.color.fonts_lt));
            receipt_type.setTextColor(ContextCompat.getColor(receipt_type.getContext(), R.color.active_fonts_lt));
            title_total_cost.setTextColor(ContextCompat.getColor(title_total_cost.getContext(), R.color.active_fonts_lt));
            title_discount.setTextColor(ContextCompat.getColor(title_discount.getContext(), R.color.active_fonts_lt));
            title_total.setTextColor(ContextCompat.getColor(title_total.getContext(), R.color.fonts_lt));

            diplomat_icon.setColorFilter(ContextCompat.getColor(diplomat_icon.getContext(), R.color.active_fonts_lt), PorterDuff.Mode.SRC_IN);
            user_icon.setColorFilter(ContextCompat.getColor(user_icon.getContext(), R.color.active_fonts_lt), PorterDuff.Mode.SRC_IN);
            location_icon.setColorFilter(ContextCompat.getColor(location_icon.getContext(), R.color.active_fonts_lt), PorterDuff.Mode.SRC_IN);

            shop_name.setTextColor(ContextCompat.getColor(shop_name.getContext(), R.color.fonts_lt));
            address.setTextColor(ContextCompat.getColor(address.getContext(), R.color.active_fonts_lt));
            title_user_name.setTextColor(ContextCompat.getColor(title_user_name.getContext(), R.color.fonts_lt));
            user_name.setTextColor(ContextCompat.getColor(user_name.getContext(), R.color.active_fonts_lt));
            title_payment_location.setTextColor(ContextCompat.getColor(title_payment_location.getContext(), R.color.fonts_lt));
            payment_location_address_city.setTextColor(ContextCompat.getColor(payment_location_address_city.getContext(), R.color.active_fonts_lt));

            title_sno.setTextColor(ContextCompat.getColor(title_sno.getContext(), R.color.active_fonts_lt));
            title_session_fm.setTextColor(ContextCompat.getColor(title_session_fm.getContext(), R.color.active_fonts_lt));
            title_fd_num.setTextColor(ContextCompat.getColor(title_fd_num.getContext(), R.color.active_fonts_lt));
            title_fp_num.setTextColor(ContextCompat.getColor(title_fp_num.getContext(), R.color.active_fonts_lt));
            title_fm_date.setTextColor(ContextCompat.getColor(title_fm_date.getContext(), R.color.active_fonts_lt));
            content_sno.setTextColor(ContextCompat.getColor(content_sno.getContext(), R.color.active_fonts_lt));
            title_rn_kkt.setTextColor(ContextCompat.getColor(title_rn_kkt.getContext(), R.color.active_fonts_lt));
            title_zn_kkt.setTextColor(ContextCompat.getColor(title_zn_kkt.getContext(), R.color.active_fonts_lt));
            title_site_fns.setTextColor(ContextCompat.getColor(title_site_fns.getContext(), R.color.active_fonts_lt));
            title_fn_num.setTextColor(ContextCompat.getColor(title_fn_num.getContext(), R.color.active_fonts_lt));
            title_inn_num.setTextColor(ContextCompat.getColor(title_inn_num.getContext(), R.color.active_fonts_lt));
        } else {
            fragment_receipt_loader.setBackgroundColor(ContextCompat.getColor(fragment_receipt_loader.getContext(), R.color.main_dt));
            receipt_detail_title_holder.setBackgroundColor(ContextCompat.getColor(receipt_detail_layout.getContext(), R.color.main_dt));
            receipt_detail_layout.setBackgroundColor(ContextCompat.getColor(receipt_detail_layout.getContext(), R.color.main_dt));
            divider.setBackgroundColor(ContextCompat.getColor(divider.getContext(), R.color.divider_dt));
            divider_cred.setBackgroundColor(ContextCompat.getColor(divider_cred.getContext(), R.color.divider_dt));
            divider_shop_info.setBackgroundColor(ContextCompat.getColor(divider_shop_info.getContext(), R.color.divider_dt));

            saleNumber.setTextColor(ContextCompat.getColor(saleNumber.getContext(), R.color.active_fonts_dt));
            sessionId.setTextColor(ContextCompat.getColor(sessionId.getContext(), R.color.active_fonts_dt));
            totalCost.setTextColor(ContextCompat.getColor(totalCost.getContext(), R.color.active_fonts_dt));
            discount.setTextColor(ContextCompat.getColor(discount.getContext(), R.color.active_fonts_dt));
            total.setTextColor(ContextCompat.getColor(total.getContext(), R.color.fonts_dt));
            ndsDigit.setTextColor(ContextCompat.getColor(ndsDigit.getContext(), R.color.active_fonts_dt));
            ndsType.setTextColor(ContextCompat.getColor(ndsType.getContext(), R.color.active_fonts_dt));
            title_payment.setTextColor(ContextCompat.getColor(title_payment.getContext(), R.color.active_fonts_dt));
            cash.setTextColor(ContextCompat.getColor(cash.getContext(), R.color.active_fonts_dt));


            receipt_detail_title.setTextColor(ContextCompat.getColor(receipt_detail_title.getContext(), R.color.fonts_dt));
            receipt_type.setTextColor(ContextCompat.getColor(receipt_type.getContext(), R.color.active_fonts_dt));
            title_total_cost.setTextColor(ContextCompat.getColor(title_total_cost.getContext(), R.color.active_fonts_dt));
            title_discount.setTextColor(ContextCompat.getColor(title_discount.getContext(), R.color.active_fonts_dt));
            title_total.setTextColor(ContextCompat.getColor(title_total.getContext(), R.color.fonts_dt));

            diplomat_icon.setColorFilter(ContextCompat.getColor(diplomat_icon.getContext(), R.color.active_fonts_dt), PorterDuff.Mode.SRC_IN);
            user_icon.setColorFilter(ContextCompat.getColor(user_icon.getContext(), R.color.active_fonts_dt), PorterDuff.Mode.SRC_IN);
            location_icon.setColorFilter(ContextCompat.getColor(location_icon.getContext(), R.color.active_fonts_dt), PorterDuff.Mode.SRC_IN);

            shop_name.setTextColor(ContextCompat.getColor(shop_name.getContext(), R.color.fonts_dt));
            address.setTextColor(ContextCompat.getColor(address.getContext(), R.color.active_fonts_dt));
            title_user_name.setTextColor(ContextCompat.getColor(title_user_name.getContext(), R.color.fonts_dt));
            user_name.setTextColor(ContextCompat.getColor(user_name.getContext(), R.color.active_fonts_dt));
            title_payment_location.setTextColor(ContextCompat.getColor(title_payment_location.getContext(), R.color.fonts_dt));
            payment_location_address_city.setTextColor(ContextCompat.getColor(payment_location_address_city.getContext(), R.color.active_fonts_dt));

            title_sno.setTextColor(ContextCompat.getColor(title_sno.getContext(), R.color.active_fonts_dt));
            title_session_fm.setTextColor(ContextCompat.getColor(title_session_fm.getContext(), R.color.active_fonts_dt));
            title_fd_num.setTextColor(ContextCompat.getColor(title_fd_num.getContext(), R.color.active_fonts_dt));
            title_fp_num.setTextColor(ContextCompat.getColor(title_fp_num.getContext(), R.color.active_fonts_dt));
            title_fm_date.setTextColor(ContextCompat.getColor(title_fm_date.getContext(), R.color.active_fonts_dt));
            content_sno.setTextColor(ContextCompat.getColor(content_sno.getContext(), R.color.active_fonts_dt));
            title_rn_kkt.setTextColor(ContextCompat.getColor(title_rn_kkt.getContext(), R.color.active_fonts_dt));
            title_zn_kkt.setTextColor(ContextCompat.getColor(title_zn_kkt.getContext(), R.color.active_fonts_dt));
            title_site_fns.setTextColor(ContextCompat.getColor(title_site_fns.getContext(), R.color.active_fonts_dt));
            title_fn_num.setTextColor(ContextCompat.getColor(title_fn_num.getContext(), R.color.active_fonts_dt));
            title_inn_num.setTextColor(ContextCompat.getColor(title_inn_num.getContext(), R.color.active_fonts_dt));
        }

    }

    /*private void addFakeShopAddress() {
        SessionPresenter.getInstance().setShopInfo(
                "Софт-Вилладж",
                "346500, Ростовская обл. г. Шахты",
                "Ул. Шевченко 141",
                "190000, г. Санкт-Петербург",
                "Большая Морская улица, 30"
        );
    }*/

    private static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height)
            throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                if (SessionPresenter.getInstance().getCurrentTheme() == SessionPresenter.THEME_LIGHT) {
                    pixels[offset + x] = result.get(x, y) ? ContextCompat.getColor(EvoApp.getInstance(), R.color.color20) : WHITE;
                } else {
                    pixels[offset + x] = result.get(x, y) ? ContextCompat.getColor(EvoApp.getInstance(), R.color.color_da) : ContextCompat.getColor(EvoApp.getInstance(), R.color.color31);
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }
}