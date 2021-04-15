package ru.softvillage.test_evo.tabs.fragments.recyclerView;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.math.RoundingMode;

import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.Receipt;
import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.R;

public class PositionGoodsItemHolder extends RecyclerView.ViewHolder {
    private final TextView goodName;
    private final TextView quantity;
    private final TextView positionCostPerOne;
    private final TextView positionPriceMultipleQuantity;
    private final TextView discount;
    private final TextView positionCost;

    public PositionGoodsItemHolder(@NonNull View itemView) {
        super(itemView);
        goodName = itemView.findViewById(R.id.position_name);
        quantity = itemView.findViewById(R.id.position_quantity);
        positionCostPerOne = itemView.findViewById(R.id.position_cost_per_one);
        positionPriceMultipleQuantity = itemView.findViewById(R.id.position_price_multiple_quantity);
        discount = itemView.findViewById(R.id.position_discount);
        positionCost = itemView.findViewById(R.id.position_cost);
    }

    @SuppressLint("LongLogTag")
    public void bind(Position position, Receipt receipt) {
        goodName.setText(position.getName());
        quantity.setText(String.valueOf(position.getQuantity()));
        positionCostPerOne.setText(String.valueOf(position.getPriceWithDiscountPosition()));

        BigDecimal totalDigit = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (Position position1 : receipt.getPositions()) {
            totalDigit = totalDigit.add(position1.getTotalWithoutDiscounts());
            totalDiscount = totalDiscount.add(position1.getDiscountPositionSum());
        }

        Log.d(EvoApp.TAG+"_discount_", "receipt.getDiscount() " + receipt.getDiscount().toString());
        BigDecimal percentDiscount = totalDigit.divide(receipt.getDiscount(), 2, RoundingMode.HALF_UP);
        BigDecimal onePercentFromPositionTotalPrice = position.getTotal(BigDecimal.ZERO).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal discountPosition = percentDiscount.multiply(onePercentFromPositionTotalPrice);
        positionPriceMultipleQuantity.setText(String.valueOf(position.getTotal(discountPosition)));
        discount.setText(String.valueOf(position.getDiscountPositionSum().add(discountPosition)));
        positionCost.setText(String.valueOf(position.getTotalWithoutDiscounts()));
    }
}
