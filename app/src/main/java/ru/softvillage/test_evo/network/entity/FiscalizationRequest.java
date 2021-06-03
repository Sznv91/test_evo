package ru.softvillage.test_evo.network.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * Класс ответ от backend для подтверждения печати(фискализации) чека
 */
@Data
public class FiscalizationRequest {

    @SerializedName("success")
    @Expose
    private boolean needPrint;

    @SerializedName("message")
    @Expose
    private long svUuid;
}
