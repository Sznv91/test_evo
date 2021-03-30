package ru.softvillage.test_evo.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import ru.evotor.framework.receipt.Position;
import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.network.entity.Good;
import ru.softvillage.test_evo.network.entity.Order;

public class PositionCreator {

    @SuppressLint("LongLogTag")
    public static OrderTo makeOrderList(List<Order> orderList) {
//        List<List<Position>> result = new ArrayList<>();
//        BigDecimal totalCost = new BigDecimal("0.0");
        OrderTo result = new OrderTo();
        for (Order order : orderList) {
            List<Position> list = new ArrayList<>();
            BigDecimal receiptCost = BigDecimal.ZERO;
            for (Good good : order.goods) {
                Position position =
                        Position.Builder.newInstance(
                                UUID.randomUUID().toString(),
                                good.productUUID,
                                good.name,
                                good.measureName,
                                good.measurePrecision,
                                good.price,
                                good.quantity).build();
                list.add(position);
                receiptCost = receiptCost.add(good.price);
            }
//            Log.d(EvoApp.TAG + "_List", list.toString());
//            Log.d(EvoApp.TAG + "_List", receiptCost.toString());
            result.getPositions().put(list, receiptCost);
        }
        return result;
    }

    public static class OrderTo {
        @Getter
        private final Map<List<Position>, BigDecimal> positions = new HashMap<>();
    }
}
