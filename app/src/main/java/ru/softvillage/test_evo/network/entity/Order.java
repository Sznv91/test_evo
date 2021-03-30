
package ru.softvillage.test_evo.network.entity;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Order {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("phone")
    @Expose
    public String phone;
    @SerializedName("userUUID")
    @Expose
    public Object userUUID;
    @SerializedName("paymant_system")
    @Expose
    public Integer paymantSystem;
    @SerializedName("check_discount")
    @Expose
    public Integer checkDiscount;
    @SerializedName("goods")
    @Expose
    public List<Good> goods = null;

}
