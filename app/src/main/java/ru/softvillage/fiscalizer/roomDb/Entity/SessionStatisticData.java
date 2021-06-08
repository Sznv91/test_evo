package ru.softvillage.fiscalizer.roomDb.Entity;

import java.math.BigDecimal;

import lombok.Data;

/**
 * Класс для отображения статистики смены на главном экране и при печати отчета.
 * хранится в SharedPreference, для сохранения/загрузки используется GSON
 */
@Data
public class SessionStatisticData {
    private Long sessionId;
    private BigDecimal sumFiscalization;
    private int countReceipt;
    private int sendSms;
    private int sendEmail;

    public SessionStatisticData() {
    }

    public SessionStatisticData(Long sessionId, BigDecimal sumFiscalization, int countReceipt, int sendSms, int sendEmail) {
        this.sessionId = sessionId;
        this.sumFiscalization = sumFiscalization;
        this.countReceipt = countReceipt;
        this.sendSms = sendSms;
        this.sendEmail = sendEmail;
    }

    public SessionStatisticData(SessionStatisticData data) {
        sessionId = data.getSessionId();
        sumFiscalization = data.getSumFiscalization();
        countReceipt = data.getCountReceipt();
        sendSms = data.getSendSms();
        sendEmail = data.getSendEmail();
    }
}
