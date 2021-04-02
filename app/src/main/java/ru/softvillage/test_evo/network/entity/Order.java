
package ru.softvillage.test_evo.network.entity;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class  Order {

    @SerializedName("id")
    @Expose
    //Внутренний id - записываем в БД
    public Integer id;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("phone")
    @Expose
    public String phone;
    @SerializedName("userUUID")
    @Expose
    //PrintSellReceiptCommand
    public Object userUUID;
    @SerializedName("paymant_system")
    @Expose
    //Сохраняем в БД
    public Integer paymantSystem;
    @SerializedName("check_discount")
    @Expose
    // Сделать перерасчет. в процентах. Распр. на весь чек.
    public Integer checkDiscount;
    @SerializedName("goods")
    @Expose
    public List<Good> goods = null;

}
