package com.coderobot.portallite.main;

import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.os.Bundle;

import com.coderobot.portallite.manager.PortalLiteDB;
import com.coderobot.portallite.manager.PreferenceInfoManager;

/**
 * Created by the great Tony on 2015/5/23.
 */
public class Global extends Application implements Application.ActivityLifecycleCallbacks {
    public PortalLiteDB portalLiteDB;
    public PreferenceInfoManager preferenceInfoManager;
    public Typeface typeface;
    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(this);

        portalLiteDB = PortalLiteDB.getInstance(this);
        preferenceInfoManager = PreferenceInfoManager.getInstance(this);


        typeface = Typeface.createFromAsset(getAssets(), "fonts/test4.ttf");

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
