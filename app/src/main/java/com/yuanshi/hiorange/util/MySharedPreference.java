package com.yuanshi.hiorange.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2018/3/1.
 */

public class MySharedPreference {


    private static final String TAG = "MySharedPreference";
    private static final String SP_NAME = "hi_sp";

    public static void saveString(Context context, String key, String value) {

        if (context != null) {
            SharedPreferences spf = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);

            spf.edit().putString(key, value).commit();
        } else {
            Log.e(TAG, "saveSharePreference: Context为" + context);
        }
    }

    public static void saveInt(Context context, String key, int value) {

        if (context != null) {
            SharedPreferences spf = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);

            spf.edit().putInt(key, value).commit();
        } else {
            Log.e(TAG, "saveSharePreference: Context为" + context);
        }
    }

    public static void saveBoolean(Context context, String key, boolean value) {

        if (context != null) {
            SharedPreferences spf = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);

            spf.edit().putBoolean(key, value).commit();
        } else {
            Log.e(TAG, "saveSharePreference: Context为" + context);
        }
    }

    public static String getString(Context context, String key, String defualt) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getString(key, defualt);
    }

    public static int getInt(Context context, String key, int defualt) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getInt(key, defualt);
    }

    public static boolean getBoolean(Context context, String key, boolean defualt) {
        return context.getSharedPreferences(SP_NAME, MODE_PRIVATE).getBoolean(key, defualt);
    }

    public static void cleanSharedPreferences(Context context){
        SharedPreferences spf = context.getSharedPreferences(SP_NAME, MODE_PRIVATE);
        spf.edit().clear().commit();
    }
}