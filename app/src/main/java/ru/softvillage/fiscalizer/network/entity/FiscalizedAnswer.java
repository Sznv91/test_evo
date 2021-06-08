package ru.softvillage.fiscalizer.network.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class FiscalizedAnswer {
    /*"documentNumber": "0",
            "emailFlag": false,
            "fiscalIdentifier": "0",
            "fsSerialNumber": "",
            "id": 2660,
            "number": "3211",
            "smsFlag": false,
            "status": 1,
            "uuid": "eb0b5fe1-9feb-46f8-9a62-39432aaddaed"*/
    @SerializedName("document_number")
    @Expose
    private long documentNumber; //фд

    @SerializedName("email_flag")
    @Expose
    private boolean emailFlag;

    @SerializedName("fiscal_identifier")
    @Expose
    private long fiscalIdentifier; //фп

    @SerializedName("fs_serial_number")
    @Expose
    private long fsSerialNumber; //фн

    @SerializedName("id")
    @Expose
    private long id; // в базе SV

    @SerializedName("number")
    @Expose
    private long number; // номер чека в терминале

    @SerializedName("sms_flag")
    @Expose
    private Boolean smsFlag;

    @SerializedName("status")
    @Expose
    private int status; // всегда 1

    @SerializedName("uuid")
    @Expose
    private String uuid; // uuid чека в терминале.
}
