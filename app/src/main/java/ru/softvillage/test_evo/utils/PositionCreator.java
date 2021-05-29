package ru.softvillage.test_evo.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.TaxNumber;
import ru.softvillage.test_evo.EvoApp;
import ru.softvillage.test_evo.network.entity.Good;
import ru.softvillage.test_evo.network.entity.Order;
import ru.softvillage.test_evo.roomDb.Entity.fromNetwork.GoodDb;
import ru.softvillage.test_evo.roomDb.Entity.fromNetwork.OrderDb;
import ru.softvillage.test_evo.roomDb.Entity.fromNetwork.OrderDbWithGoods;

public class PositionCreator {

    public static OrderTo makeOrderList(ArrayList<Order> orderList) {
        OrderTo result = new OrderTo();

        for (Order order : orderList) {
            result.getOrderList().add(makeSinglePositionTo(order));
        }
        return result;
    }

    @SuppressLint("LongLogTag")
    public static OrderTo makeOrderList(List<OrderDbWithGoods> orderList) {
        OrderTo result = new OrderTo();

        for (OrderDbWithGoods order : orderList) {
            result.getOrderList().add(makeSinglePositionTo(order));
        }
        return result;
    }

    public static OrderTo.PositionTo makeSinglePositionTo(Order order){
        OrderDbWithGoods orderDbWithGoods = new OrderDbWithGoods(order);
        return makeSinglePositionTo(orderDbWithGoods);
    }

    @SuppressLint("LongLogTag")
    public static OrderTo.PositionTo makeSinglePositionTo(OrderDbWithGoods order) {
        BigDecimal receiptCost = BigDecimal.ZERO;

        OrderTo.PositionTo tResult = new OrderTo.PositionTo();
        for (GoodDb good : order.goodDbEntities) {

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
                Log.d(EvoApp.TAG + "_GSON", "///////////////////////");
                BigDecimal priceOnePercentPerSingleQuantity = good.price.divide(BigDecimal.valueOf(100)/*, 2, RoundingMode.HALF_UP*/);
                Log.d(EvoApp.TAG + "_GSON", String.format("Цена без скидки: %s", good.price));
                Log.d(EvoApp.TAG + "_GSON", String.format("Стоимость одного процена, неокругленная: %s", priceOnePercentPerSingleQuantity));
                Log.d(EvoApp.TAG + "_GSON", String.format("Стоимость одного процена, округленная (не исп в расчете чека) : %s", good.price.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).toPlainString()));
                BigDecimal priceDiscount = priceOnePercentPerSingleQuantity.multiply(good.discount);
                Log.d(EvoApp.TAG + "_GSON", String.format("Размер скидки: %s", priceDiscount));
                BigDecimal priceWithDiscount = rounding(good.price.subtract(priceDiscount));
                Log.d(EvoApp.TAG + "_GSON", String.format("Цена за одну позицию с учетом скидки: %s", priceWithDiscount));
                position.setPriceWithDiscountPosition(priceWithDiscount);
                BigDecimal priceAllContWithDiscount = priceWithDiscount.multiply(good.quantity);
                Log.d(EvoApp.TAG + "_GSON", String.format("Цена за всё количество товара с учетом скидки: %s", priceAllContWithDiscount));
                receiptCost = receiptCost.add(priceAllContWithDiscount);
                Log.d(EvoApp.TAG + "_GSON", "///////////////////////");
            } else {
                Log.d(EvoApp.TAG + "_GSON", "start receiptCost " + receiptCost.toString());
                Log.d(EvoApp.TAG + "_GSON", "good.price " + good.price.toString());
                Log.d(EvoApp.TAG + "_GSON", "good.quantity " + good.quantity.toString());

                receiptCost = receiptCost.add(good.price.multiply(good.quantity));
                Log.d(EvoApp.TAG + "_GSON", "ReceiptCost " + receiptCost.toString());
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
                case 18:
                    position.setTaxNumber(TaxNumber.VAT_18);
                    break;
                case 10:
                    position.setTaxNumber(TaxNumber.VAT_10);
                    break;
            }
            tResult.positions.add(position.build());
        }
        tResult.setOrderData(order);
        tResult.setSumPrice(receiptCost);

        return tResult;

    }


    public static class OrderTo {
        @Getter
        private final List<PositionTo> orderList = new ArrayList<>();

        @Data
        public static class PositionTo {
            @Setter(AccessLevel.NONE)
            private List<Position> positions = new ArrayList<>();
            private BigDecimal sumPrice = BigDecimal.ZERO;
            private OrderDbWithGoods orderData = null;
        }

    }

    private static BigDecimal rounding(BigDecimal val) {
        return val.multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(100), 2, RoundingMode.CEILING);
    }
}
