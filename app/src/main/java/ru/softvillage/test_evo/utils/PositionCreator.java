package ru.softvillage.test_evo.utils;

import android.annotation.SuppressLint;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.TaxNumber;
import ru.softvillage.test_evo.network.entity.Good;
import ru.softvillage.test_evo.network.entity.Order;

public class PositionCreator {

    @SuppressLint("LongLogTag")
    public static OrderTo makeOrderList(List<Order> orderList) {
        OrderTo result = new OrderTo();

        for (Order order : orderList) {
            OrderTo.PositionTo tResult = new OrderTo.PositionTo();
            BigDecimal receiptCost = BigDecimal.ZERO;
            for (Good good : order.goods) {
                Position.Builder position =
                        Position.Builder.newInstance(
                                UUID.randomUUID().toString(),
                                good.productUUID,
                                good.name,
                                good.measureName,
                                good.measurePrecision,
                                good.price,
                                good.quantity);

                if (!good.discount.equals(BigDecimal.ZERO)) {
                    BigDecimal priceWithDiscount = new BigDecimal("100").subtract(good.discount).divide(new BigDecimal("100")).multiply(good.price);
                    position.setPriceWithDiscountPosition(priceWithDiscount);
                    receiptCost = receiptCost.add(priceWithDiscount).multiply(good.quantity);
                } else {
                    receiptCost = receiptCost.add(good.price).multiply(good.quantity);
                }

                switch (good.type.number) {
                    case 0:
                        position.toNormal();
                        break;
                    case 1:
                        position.toService();
                        break;
                }

                switch (good.nds) {
                    case -1:
                        position.setTaxNumber(TaxNumber.NO_VAT);
                        break;
                    case 0:
                        position.setTaxNumber(TaxNumber.VAT_0);
                        break;
                    case 110:
                        position.setTaxNumber(TaxNumber.VAT_10_110);
                        break;
                    case 118:
                        position.setTaxNumber(TaxNumber.VAT_18_118);
                        break;
                }
                tResult.positions.add(position.build());
            }
            tResult.setOrderData(order);
            tResult.setSumPrice(receiptCost);
            result.getOrderList().add(tResult);
        }
        return result;
    }

    public static class OrderTo {
        @Getter
        private final List<PositionTo> orderList = new ArrayList<>();

        @Data
        public static class PositionTo {
            @Setter(AccessLevel.NONE)
            private List<Position> positions = new ArrayList<>();
            private BigDecimal sumPrice = new BigDecimal(BigInteger.ZERO);
            private Order orderData = null;
        }

    }
}
