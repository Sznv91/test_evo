
package ru.softvillage.fiscalizer.network.entity;

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
    //в процентах, на позицию.

    public BigDecimal discount;
    @SerializedName("quantity")
    @Expose
    //Количество товара
    public BigDecimal quantity;

    @SerializedName("nds")
    @Expose
    //-1 ; 0; 110; 118; 10; 18
    //На каждую позицию
    public Integer nds;
    @SerializedName("type")
    @Expose
    public Type type;

}
