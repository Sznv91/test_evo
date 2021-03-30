
package ru.softvillage.test_evo.network.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Good {

    @SerializedName("productUUID")
    @Expose
    public String productUUID;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("measure_name")
    @Expose
    public String measureName;
    @SerializedName("measure_precision")
    @Expose
    public Integer measurePrecision;
    @SerializedName("price")
    @Expose
    public BigDecimal price;
    @SerializedName("discount")
    @Expose
    public BigDecimal discount;
    @SerializedName("quantity")
    @Expose
    public BigDecimal quantity;
    @SerializedName("nds")
    @Expose
    public Integer nds;
    @SerializedName("type")
    @Expose
    public Type type;

}
