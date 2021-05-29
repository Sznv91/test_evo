package ru.softvillage.test_evo.roomDb.Entity.fromNetwork;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import ru.softvillage.test_evo.network.entity.Good;
import ru.softvillage.test_evo.network.entity.Order;

@Data
public class OrderDbWithGoods {
    @Embedded
    public OrderDb orderDb;
    @Relation(parentColumn = "sv_id",
            entity = GoodDb.class,
            entityColumn = "order_db_id")
    public List<GoodDb> goodDbEntities;

    public OrderDbWithGoods() {
    }

    public OrderDbWithGoods(Order order) {
        orderDb = new OrderDb();
        orderDb.setSv_id(order.getId());
        orderDb.setEmail(order.getEmail());
        orderDb.setPhone(order.getPhone());
        orderDb.setUserUUID(order.getUserUUID());
        orderDb.setPaymentSystem(order.getPaymantSystem());
        orderDb.setCheckDiscount(order.getCheckDiscount());

        goodDbEntities = new ArrayList<>();
        for (Good good : order.getGoods()) {
            GoodDb entity = new GoodDb();
            entity.setOrderDbId(order.getId());
            entity.setProductUUID(good.getProductUUID());
            entity.setName(good.getName());
            entity.setMeasureName(good.getMeasureName());
            entity.setMeasurePrecision(good.getMeasurePrecision());
            entity.setPrice(good.getPrice());
            entity.setDiscount(good.getDiscount());
            entity.setQuantity(good.getQuantity());
            entity.setNds(good.getNds());
            entity.setType(good.getType());
            goodDbEntities.add(entity);
        }

    }
}
