package ru.softvillage.fiscalizer.network.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class SmsServerInitResponse {

    @SerializedName("success")
    @Expose
    private int success;

    @SerializedName("user_has_sms_server")
    @Expose
    private boolean initSmsServer;
}
