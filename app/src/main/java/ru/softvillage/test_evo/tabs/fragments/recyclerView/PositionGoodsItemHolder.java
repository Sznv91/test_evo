package ru.softvillage.test_evo.tabs.fragments.recyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;

import ru.softvillage.test_evo.R;
import ru.softvillage.test_evo.roomDb.Entity.GoodEntity;

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

    public void bind(GoodEntity entity) {
        goodName.setText(entity.name);
        quantity.setText(String.valueOf(entity.quantity));
        positionCostPerOne.setText(String.valueOf(entity.price));
        BigDecimal price = entity.quantity.multiply(entity.price);
        positionPriceMultipleQuantity.setText(String.valueOf(price));
        discount.setText(String.valueOf(entity.discount));
        BigDecimal finalPrice = price.subtract(entity.discount);
        positionCost.setText(String.valueOf(finalPrice));
    }
}
