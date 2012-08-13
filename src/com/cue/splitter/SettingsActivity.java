package com.cue.splitter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.DialogPreference;
import android.preference.PreferenceActivity;
import android.util.AttributeSet;
import com.cue.splitter.util.Settings;
import com.cue.splitter.view.CustomPreferenceDialog;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 13.08.12
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener  {


    private CheckBoxPreference isDefaultFolderEnabled;
    private CustomPreferenceDialog defaultFolderValue;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        isDefaultFolderEnabled = (CheckBoxPreference)  getPreferenceScreen().findPreference(Settings.PREF_DEFAULT_FOLDER_ENABLED);
        defaultFolderValue = (CustomPreferenceDialog) getPreferenceScreen().findPreference(Settings.PREF_DEFAULT_FOLDER_VALUE);


    }

    @Override
    protected void onResume() {
        super.onResume();
        String on = getRString(R.string.on);
        String off = getRString(R.string.off);
        isDefaultFolderEnabled.setSummary(Settings.getBoolean(this, Settings.PREF_DEFAULT_FOLDER_ENABLED) ? on : off);
        defaultFolderValue.setSummary(Settings.getString(this, Settings.PREF_DEFAULT_FOLDER_VALUE));
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    private String getRString(int id) {
        return getResources().getString(id);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String on = getRString(R.string.on);
        String off = getRString(R.string.off);

        if (key.equals(Settings.PREF_DEFAULT_FOLDER_ENABLED)){
            isDefaultFolderEnabled.setSummary(Settings.getBoolean(this, key) ? on : off);
        } else if(key.equals(Settings.PREF_DEFAULT_FOLDER_VALUE)){
            defaultFolderValue.setSummary(Settings.getString(this, Settings.PREF_DEFAULT_FOLDER_VALUE));
        }

    }




}