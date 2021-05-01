package ru.softvillage.test_evo.tabs.left_menu.util;

import android.content.Context;
import android.content.SharedPreferences;

import ru.softvillage.test_evo.EvoApp;

public class Prefs {
    private final static Prefs instance = new Prefs();

    private final SharedPreferences sharedPreferences;

    public static Prefs getInstance() {
        return instance;
    }

    private Prefs() {
        sharedPreferences = EvoApp.getInstance().getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    public String loadString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void saveString(String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    public int loadInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public void saveInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).commit();
    }

    public long loadLong(String key) {
        return sharedPreferences.getLong(key, -1);
    }

    public void saveLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).commit();
    }

    public boolean loadBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

/*    public boolean loadBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }*/

    public void saveBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public boolean isKeyExist(String key) {
        return sharedPreferences.contains(key);
    }

    public void removeKey(String key) {
        sharedPreferences.edit().remove(key).commit();
    }
}
