package ru.softvillage.test_evo.network.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class OrgInfo {

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("shop_address_city")
    @Expose
    String shop_address_city;

    @SerializedName("shop_address_street")
    @Expose
    String shop_address_street;

    @SerializedName("inn")
    @Expose
    long inn;

    @SerializedName("sno")
    @Expose
    String sno;

    @SerializedName("payment_location_address_city")
    @Expose
    String payment_location_address_city;

    @SerializedName("payment_location_address_street")
    @Expose
    String payment_location_address_street;
}
