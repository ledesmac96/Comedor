package com.unse.bienestar.comedordos.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenciasManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    public PreferenciasManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(ABC.PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(ABC.IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {

        return pref.getBoolean(ABC.IS_FIRST_TIME_LAUNCH, true);
    }


    public String getValueString(String tag) {
        return pref.getString(tag, "");
    }

    public int getValueInt(String tag) {
        return pref.getInt(tag, 0);
    }

    public void setValue(String tag, String value) {
        editor.putString(tag, value);
        editor.commit();
    }

    public void setValue(String tag, int value) {
        editor.putInt(tag, value);
        editor.commit();
    }

    public boolean getValue(String tag) {
        return pref.getBoolean(tag, true);
    }

    public void setValue(String tag, boolean value) {
        editor.putBoolean(tag, value);
        editor.commit();
    }

}