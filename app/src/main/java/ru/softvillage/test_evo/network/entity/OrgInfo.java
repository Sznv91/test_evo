package ru.softvillage.test_evo.network.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class OrgInfo {

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("address")
    @Expose
    String address;

    @SerializedName("inn")
    @Expose
    long inn;

    @SerializedName("sno")
    @Expose
    String sno;

    @SerializedName("payment_place")
    @Expose
    String payment_place;
}
