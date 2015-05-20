package com.coderobot.portallite.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * Created by Tony on 2/14/15.
 */
public class UIUtil {

    private static final String TAG = "UIUtil";

    public static int getNavigetionBartHeight(Context context) {

        Resources resources = context.getResources();
        int resourceID = resources.getIdentifier("navigation_bar_height", "dimen", "android");

        return (resourceID > 0) ? resources.getDimensionPixelSize(resourceID) : 0;
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceID = resources.getIdentifier("status_bar_height", "dimen", "android");

        return (resourceID > 0) ? resources.getDimensionPixelSize(resourceID) : 0;
    }

    private static void log(String msg) {
        Log.d(TAG, msg);
    }
}
