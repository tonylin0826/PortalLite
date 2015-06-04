package com.coderobot.portallite.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.coderobot.portallite.model.data.Semester;
import com.coderobot.portallite.model.data.User;

/**
 * Created by Tony on 2015/3/9.
 */
public class PreferenceInfoManager {

    private final static String PREFERENCE_NAME = "PortalLite";

    private static String KEY_IS_LOGIN = "KEY_IS_LOGIN";
    private static String KEY_GET_COOKIE = "KEY_GET_COOKIE";
    private static String KEY_CURRENT_SEMESTER = "KEY_CURRENT_SEMESTER";
    private static String KEY_USER = "KEY_USER";

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
        cookie = cookie.replace("]", "").replace("[", "").replace("Set-", "");
        preferences.edit().putString(KEY_GET_COOKIE, cookie).apply();
    }

    public Semester getCurrentSemester() {
        return (preferences == null) ? null : new Semester(preferences.getString(KEY_CURRENT_SEMESTER, "100/1"));
    }

    public void setCurrentSemester(Semester semester) {
        if (preferences == null) return;

        preferences.edit().putString(KEY_CURRENT_SEMESTER, semester.toString()).apply();
    }

    public User getUser() {
        return (preferences == null) ? null : new User(preferences.getString(KEY_USER, "test__,__test"));
    }

    public void setUser(User user) {
        if (preferences == null) return;

        preferences.edit().putString(KEY_USER, user.toString()).apply();
    }

}
