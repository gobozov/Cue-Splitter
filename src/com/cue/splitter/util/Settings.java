package com.cue.splitter.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 13.08.12
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public class Settings {

    public static final String PREF_DEFAULT_FOLDER_ENABLED = "default_folder_key";
    public static final String PREF_DEFAULT_FOLDER_VALUE = "default_folder_value_key";
    public static final String PREF_USE_ID3_TAGS = "use_id3_tags_key";


    public static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getString(Context context, String name, String defaultValue) {
        return getPreferences(context).getString(name, defaultValue);
    }

    public static void setString(Context context,String key, String value) {
        getPreferences(context).edit().putString(key, value).commit();
    }

    public static Boolean getBoolean(Context context,String name) {
        return getPreferences(context).getBoolean(name, false);
    }

    public static void setBoolean(Context context,String key, boolean value) {
        getPreferences(context).edit().putBoolean(key, value).commit();
    }

    public static int getInt(Context context,String name) {
        return Integer.parseInt(getPreferences(context).getString(name, "-1"));
    }

}
