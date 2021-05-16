package ru.softvillage.test_evo.tabs.fragments.recyclerView;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.math.RoundingMode;

import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.Receipt;
import ru.evotor.framework.receipt.TaxNumber;
import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.tabs.left_menu.presenter.SessionPresenter;

public class PositionGoodsItemHolder extends RecyclerView.ViewHolder {
    private final TextView position_name,
            receipt_item_single_position,
            receipt_item_position_discount,
            receipt_item_position_cost,
            receipt_item_nds;

/*    private final TextView receipt_item_single_position;

    private final TextView receipt_item_position_discount;

    private final TextView receipt_item_position_cost;

    private final TextView receipt_item_nds;*/

   /* private final TextView static_nds;
    private final TextView nds_percent;
    private final TextView static_nds_equal;
    private final TextView nds_digit;*/


    public PositionGoodsItemHolder(@NonNull View itemView) {
        super(itemView);
        position_name = itemView.findViewById(R.id.position_name);
        receipt_item_single_position = itemView.findViewById(R.id.receipt_item_single_position);
        receipt_item_position_discount = itemView.findViewById(R.id.receipt_item_position_discount);
        receipt_item_position_cost = itemView.findViewById(R.id.receipt_item_position_cost);

        receipt_item_nds = itemView.findViewById(R.id.receipt_item_nds);
        initColour(SessionPresenter.getInstance().getCurrentTheme());
       /* static_nds = itemView.findViewById(R.id.static_nds);
        nds_percent = itemView.findViewById(R.id.nds_percent);
        static_nds_equal = itemView.findViewById(R.id.static_nds_equal);
        nds_digit = itemView.findViewById(R.id.nds_digit);*/
    }

    @SuppressLint("LongLogTag")
    public void bind(Position position, Receipt receipt) {
        position_name.setText(position.getName());
        BigDecimal totalDigit = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalPricePositionWithDiscount = BigDecimal.ZERO; //A

        for (Position position1 : receipt.getPositions()) {
            totalDigit = totalDigit.add(position1.getTotalWithoutDiscounts());
            totalDiscount = totalDiscount.add(position1.getDiscountPositionSum());
            totalPricePositionWithDiscount = totalPricePositionWithDiscount.add(position1.getTotal(BigDecimal.ZERO));
        }
        receipt_item_position_cost.setText(
                String.format(
                        EvoApp.getInstance().getString(R.string.receipt_item_position_cost)
                        , position.getTotalWithoutDiscounts())
                        .replace(",", ".")
        );
//        positionCost.setText(String.valueOf(position.getTotalWithoutDiscounts()));

        if (!receipt.getDiscount().equals(BigDecimal.ZERO)) {
            /**
             * Для расчета процена скидки на весь чек:
             * сложить цену всех позийи с учетом скидки на позцию (A) ->
             * вычесть сумму итогового платежа (B) ->
             * цену всех позийи разедлить по получившееся значение (A/B)
             */
            Log.d(EvoApp.TAG + "position_discount", "цена всех позиций с учетом скидки на каждую позицию: " + totalPricePositionWithDiscount.toPlainString());
            BigDecimal onePercentTotalPrice = totalPricePositionWithDiscount.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);


            BigDecimal percent = receipt.getDiscount().divide(onePercentTotalPrice, 2, RoundingMode.HALF_UP);/*totalPricePositionWithDiscount
                    .divide(
                            totalPricePositionWithDiscount
                                    .subtract(receipt.getPayments().get(0).getValue())
                            , 8, RoundingMode.HALF_UP)*/
            ; // A/B
            Log.d(EvoApp.TAG + "position_discount", "процент скидки: " + percent.toPlainString());

            /**
             * Для расчета цены одной позици с учетом общей скидки:
             * Цена со скидкой на позицию (A)
             * Общая скидка на чек в % (B)
             * С = A-(A/100*B)
             */
            BigDecimal priceTotalPosition = position.getTotal(BigDecimal.ZERO).subtract(position.getTotal(BigDecimal.ZERO).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP).multiply(percent));
            BigDecimal tPriceTotalPosition = priceTotalPosition.setScale(2, RoundingMode.DOWN);
            Log.d(EvoApp.TAG + "position_discount", "цена одной позции с общей скидкой: " + priceTotalPosition.toPlainString());


            /**
             * Цена одной единицы товара
             * Количество товара (D)
             * Цена с учетом скидки на позицию и скидки по чеку (I)
             * F = I / D
             */
            BigDecimal pricePerOnePosition = priceTotalPosition.divide(position.getQuantity(), 6, RoundingMode.HALF_UP);
            BigDecimal tPricePerOnePosition = pricePerOnePosition.setScale(2, RoundingMode.DOWN);
            receipt_item_single_position.setText(String.format(EvoApp.getInstance().getString(R.string.receipt_item_single_position)
                    , position.getQuantity()
                    , tPricePerOnePosition
                    , tPriceTotalPosition)
                    .replace(",", "."));

            /**
             * Расчет скидки позции + общей скидки для отображения
             * От цены со скидкой отнимаем общий процент
             */
            BigDecimal discountBigDec = position.getPriceWithDiscountPosition().multiply(position.getQuantity()).subtract(tPriceTotalPosition).add(position.getDiscountPositionSum());
            receipt_item_position_discount.setText(String.format(EvoApp.getInstance().getString(R.string.receipt_item_position_discount), discountBigDec).replace(",", "."));
//                    String.valueOf(discountBigDec));

            BigDecimal nds_20 = tPriceTotalPosition.divide(BigDecimal.valueOf(1.2), 10, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(0.2))
                    .multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)/*.toPlainString()*/;
            BigDecimal nds_10 = tPriceTotalPosition.divide(BigDecimal.valueOf(1.1), 10, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(0.1))
                    .multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)/*.toPlainString()*/;

            if (position.getTaxNumber() != null && !position.getTaxNumber().equals(TaxNumber.NO_VAT)) {
                receipt_item_nds.setVisibility(View.VISIBLE);
                /*static_nds.setVisibility(View.VISIBLE);
                nds_percent.setVisibility(View.VISIBLE);
                static_nds_equal.setVisibility(View.VISIBLE);
                nds_digit.setVisibility(View.VISIBLE);*/
            }


            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_0)) {
                receipt_item_nds.setText(String.format(EvoApp.getInstance().getString(R.string.receipt_item_nds)
                        , "0%"
                        , BigDecimal.ZERO)
                        .replace(",", "."));
               /* nds_percent.setText("0%");
                nds_digit.setText("0.00");*/
            }

            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_18)) {
                receipt_item_nds.setText(String.format(EvoApp.getInstance().getString(R.string.receipt_item_nds)
                        , "20%"
                        , nds_20)
                        .replace(",", "."));
                /*nds_percent.setText("20%");
                nds_digit.setText(nds_20);*/
            }

            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_10)) {
                receipt_item_nds.setText(String.format(EvoApp.getInstance().getString(R.string.receipt_item_nds)
                        , "10%"
                        , nds_10)
                        .replace(",", "."));
                /*nds_percent.setText("10%");
                nds_digit.setText(nds_10);*/
            }
            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_18_118)) {
                receipt_item_nds.setText(String.format(EvoApp.getInstance().getString(R.string.receipt_item_nds)
                        , "20/120"
                        , nds_20)
                        .replace(",", "."));
               /* nds_percent.setText("20/120");
                nds_digit.setText(nds_20);*/
            }
            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_10_110)) {
                receipt_item_nds.setText(String.format(EvoApp.getInstance().getString(R.string.receipt_item_nds)
                        , "10/110"
                        , nds_10)
                        .replace(",", "."));
/*                nds_percent.setText("10/110");
                nds_digit.setText(nds_10);*/
            }


        } else {
            receipt_item_single_position.setText(String.format(EvoApp.getInstance().getString(R.string.receipt_item_single_position)
                    , position.getQuantity()
                    , position.getTotal(BigDecimal.ZERO)
                    , position.getPriceWithDiscountPosition().setScale(2, RoundingMode.DOWN))
                    .replace(",", "."));
            /*position_price_multiple_quantity.setText(String.valueOf(position.getTotal(BigDecimal.ZERO)));
            position_cost_per_one.setText(String.valueOf(position.getPriceWithDiscountPosition().setScale(2, RoundingMode.DOWN)));*/
            receipt_item_position_discount.setText(String.format(EvoApp.getInstance().getString(R.string.receipt_item_position_discount), position.getDiscountPositionSum()).replace(",", "."));
//            discount.setText(String.valueOf(position.getDiscountPositionSum()));

            BigDecimal nds_20 = position.getTotal(BigDecimal.ZERO).divide(BigDecimal.valueOf(1.2), 10, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(0.2))
                    .multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)/*.toPlainString()*/;
            BigDecimal nds_10 = position.getTotal(BigDecimal.ZERO).divide(BigDecimal.valueOf(1.1), 10, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(0.1))
                    .multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)/*.toPlainString()*/;

            if (position.getTaxNumber() != null && !position.getTaxNumber().equals(TaxNumber.NO_VAT)) {
                receipt_item_nds.setVisibility(View.VISIBLE);
                /*static_nds.setVisibility(View.VISIBLE);
                nds_percent.setVisibility(View.VISIBLE);
                static_nds_equal.setVisibility(View.VISIBLE);
                nds_digit.setVisibility(View.VISIBLE);*/
            }


            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_0)) {
                receipt_item_nds.setText(String.format(EvoApp.getInstance().getString(R.string.receipt_item_nds)
                        , "0%"
                        , BigDecimal.ZERO)
                        .replace(",", "."));
               /* nds_percent.setText("0%");
                nds_digit.setText("0.00");*/
            }

            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_18)) {
                receipt_item_nds.setText(String.format(EvoApp.getInstance().getString(R.string.receipt_item_nds)
                        , "20%"
                        , nds_20)
                        .replace(",", "."));
                /*nds_percent.setText("20%");
                nds_digit.setText(nds_20);*/
            }

            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_10)) {
                receipt_item_nds.setText(String.format(EvoApp.getInstance().getString(R.string.receipt_item_nds)
                        , "10%"
                        , nds_10)
                        .replace(",", "."));
                /*nds_percent.setText("10%");
                nds_digit.setText(nds_10);*/
            }
            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_18_118)) {
                receipt_item_nds.setText(String.format(EvoApp.getInstance().getString(R.string.receipt_item_nds)
                        , "20/120"
                        , nds_20)
                        .replace(",", "."));
               /* nds_percent.setText("20/120");
                nds_digit.setText(nds_20);*/
            }
            if (position.getTaxNumber() != null && position.getTaxNumber().equals(TaxNumber.VAT_10_110)) {
                receipt_item_nds.setText(String.format(EvoApp.getInstance().getString(R.string.receipt_item_nds)
                        , "10/110"
                        , nds_10)
                        .replace(",", "."));
/*                nds_percent.setText("10/110");
                nds_digit.setText(nds_10);*/
            }
        }

        Log.d(EvoApp.TAG + "_total_discount_receipt.toString()", receipt.toString());
    }

    private void initColour(int currentTheme) {
        Drawable good_icon = ContextCompat.getDrawable(EvoApp.getInstance().getApplicationContext(), R.drawable.ic_baseline_receipt_24);
        if (currentTheme == SessionPresenter.THEME_LIGHT) {
            position_name.setTextColor(ContextCompat.getColor(position_name.getContext(), R.color.color20));
            good_icon.setColorFilter(ContextCompat.getColor(position_name.getContext(), R.color.color20), PorterDuff.Mode.SRC_IN);
        } else {
            position_name.setTextColor(ContextCompat.getColor(position_name.getContext(), R.color.color_c4));
            good_icon.setColorFilter(ContextCompat.getColor(position_name.getContext(), R.color.color_c4), PorterDuff.Mode.SRC_IN);
        }
        position_name.setCompoundDrawablesRelativeWithIntrinsicBounds(good_icon, null, null, null);
        receipt_item_single_position.setTextColor(ContextCompat.getColor(receipt_item_single_position.getContext(), R.color.color29));
        receipt_item_position_discount.setTextColor(ContextCompat.getColor(receipt_item_position_discount.getContext(), R.color.color29));
        receipt_item_position_cost.setTextColor(ContextCompat.getColor(receipt_item_position_cost.getContext(), R.color.color29));
        receipt_item_nds.setTextColor(ContextCompat.getColor(receipt_item_nds.getContext(), R.color.color29));
    }
}
