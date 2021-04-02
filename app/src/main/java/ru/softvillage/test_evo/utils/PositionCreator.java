package ru.softvillage.test_evo.utils;

import android.annotation.SuppressLint;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                receiptCost = receiptCost.add(good.price);
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

        public static class PositionTo {
            @Getter
            private final List<Position> positions = new ArrayList<>();
            @Getter
            @Setter
            private BigDecimal sumPrice = new BigDecimal(BigInteger.ZERO);
            @Getter
            @Setter
            private Order orderData = null;
        }

    }
}
