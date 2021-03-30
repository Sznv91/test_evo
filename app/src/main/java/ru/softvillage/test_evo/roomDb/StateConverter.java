package ru.softvillage.test_evo.roomDb;

import androidx.room.TypeConverter;

import ru.softvillage.test_evo.liveDataHolder.States;

public class StateConverter {

    @TypeConverter
    public static States stringToState(String data) {
        return States.valueOf(data);
    }

    @TypeConverter
    public static String statesToString(States data) {
        return data.toString();
    }
}
