package com.coderobot.portallite.manager;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Tony on 2015/3/9.
 */
public class PreferenceInfoManager {

    private final static String PREFERENCE_NAME = "PortalLite";

    private static String KEY_IS_LOGIN = "KEY_IS_LOGIN";
    private static String KEY_GET_COOKIE = "KEY_GET_COOKIE";

    private static PreferenceInfoManager instance = null;
    private static SharedPreferences preferences;

    private PreferenceInfoManager(Context context) {
        preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static PreferenceInfoManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceInfoManager(context);
        }

        return instance;
    }

    public boolean getIsLogin() {
        return preferences != null && preferences.getBoolean(KEY_IS_LOGIN, false);
    }

    public void setIsLogin(boolean isLogin) {
        if (preferences == null) return;

        preferences.edit().putBoolean(KEY_IS_LOGIN, isLogin).apply();
    }

    public String getLoginCookie() {
        return (preferences == null) ? null : preferences.getString(KEY_GET_COOKIE, null);
    }

    public void setLoginCookie(String cookie) {
        if (preferences == null) return;

        preferences.edit().putString(KEY_GET_COOKIE, cookie).apply();
    }

}
