package ru.softvillage.fiscalizer.roomDb;

import androidx.room.TypeConverter;

import java.math.BigDecimal;

public class BigDecimalConverter {

    @TypeConverter
    public static BigDecimal stringToBigDecimal(String data) {
        return new BigDecimal(data);
    }

    @TypeConverter
    public static String BigDecimalToString(BigDecimal data) {
        return data.toPlainString();
    }
}
