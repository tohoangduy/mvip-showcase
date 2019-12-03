package com.mq.myvtg.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.mq.myvtg.BuildConfig;

public class SharePref {
    public static final String PREFNAME = BuildConfig.APPLICATION_ID + ".Pref";

    // put/get boolean
    public static void putBoolean(Context context, String key, boolean value) {
        if (context == null || key == null) {
            return;
        }
        SharedPreferences prefi = context.getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefi.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        if (context == null || key == null) {
            return defaultValue;
        }
        SharedPreferences sp = context.getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    // put/get integer
    public static void putInt(Context context, String key, int value) {
        if (context == null || key == null) {
            return;
        }
        SharedPreferences prefi = context.getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefi.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    public static int getInt(Context context, String key, int defaultValue) {
        if (context == null || key == null) {
            return defaultValue;
        }
        SharedPreferences sp = context.getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }

    // put/get long
    public static void putLong(Context context, String key, long value) {
        if (context == null || key == null) {
            return;
        }
        SharedPreferences prefi = context.getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefi.edit();
        editor.putLong(key, value);
        editor.apply();
    }
    public static long getLong(Context context, String key, long defaultValue) {
        if (context == null || key == null) {
            return defaultValue;
        }
        SharedPreferences sp = context.getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        return sp.getLong(key, defaultValue);
    }

    // put/get string
    public static void putString(Context context, String key, String value) {
        if (context == null || key == null) {
            return;
        }
        SharedPreferences prefi = context.getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefi.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static void putArrayString(Context context, String[][] arrKeyValues) {
        SharedPreferences prefi = context.getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefi.edit();
        for (String[] keyValue : arrKeyValues) {
            String key = keyValue[0];
            String value = keyValue[1];
            editor.putString(key, value);
        }
        editor.apply();
    }
    public static String getString(Context context, String key, String defaultValue) {
        if (context == null || key == null) {
            return defaultValue;
        }
        SharedPreferences sp = context.getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        String data="";
        try {
            data=sp.getString(key, defaultValue);
        }catch (Exception e){

        }
        return data;
    }


    public static void remove(Context context, String[] keys) {
        if (context == null || keys == null || keys.length == 0) {
            return;
        }
        SharedPreferences prefi = context.getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefi.edit();
        for (String key : keys) {
            editor.remove(key);
        }
        editor.apply();
    }

    public static void removeAll(Context context) {
        if (context == null) return;

        SharedPreferences prefi = context.getSharedPreferences(PREFNAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefi.edit();
        editor.clear().apply();
    }
}
