package ru.softvillage.test_evo.roomDb;

import androidx.room.TypeConverter;

import com.google.gson.Gson;

import ru.softvillage.test_evo.network.entity.Type;

public class GoodTypeDbConverter {

    @TypeConverter
    public static Type stringToGoodType(String data) {
        return new Gson().fromJson(data, Type.class);
    }

    @TypeConverter
    public static String GoodTypeToString(Type data) {
        return new Gson().toJson(data);
    }
}
