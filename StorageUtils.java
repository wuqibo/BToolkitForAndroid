package com.wuqibo.bppcallbackservice.btoolkit;

import android.content.Context;
import android.content.SharedPreferences;

public class StorageUtils {

    private final static String SharedPreferencesName = "LOCAL";

    public static void setString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

}
