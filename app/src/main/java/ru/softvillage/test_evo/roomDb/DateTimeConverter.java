package ru.softvillage.test_evo.roomDb;

import androidx.room.TypeConverter;

import org.joda.time.LocalDateTime;

public class DateTimeConverter {

    @TypeConverter
    public static LocalDateTime stringToLocalDateTime(String data){
        return LocalDateTime.parse(data);
    }

    @TypeConverter
    public static String LocalDateTimeToString(LocalDateTime data){
        return data.toString();
    }
}
