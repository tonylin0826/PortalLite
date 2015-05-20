package com.coderobot.portallite.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.coderobot.portallite.R;

/**
 * Created by Tony on 2/23/15.
 */
public class AnimUtil {

    public static void startTranslateInAnimation(Context context, View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in);
        animation.setDuration(500);
        view.startAnimation(animation);
    }

    public static void startAlphaInAnimation(Context context, View view) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.alpha_in);
        animation.setDuration(1000);
        view.startAnimation(animation);
    }
}
