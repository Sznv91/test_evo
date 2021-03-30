package ru.softvillage.test_evo.network.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@Data
public class NetworkAnswer {
    @SerializedName("success")
    @Expose
    Boolean success;

    @SerializedName("error_data")
    @Expose
    String errorMessage;

    @SerializedName("data")
    @Expose
    List<Order> orderList;

}
