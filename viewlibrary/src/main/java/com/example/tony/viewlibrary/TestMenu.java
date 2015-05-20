package com.example.tony.viewlibrary;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;


/**
 * TODO: document your custom view class.
 */
public class TestMenu extends LinearLayout {

    private static final String TAG = "TestMenu";
    private ArrayList<ScaleAnimation> animations = new ArrayList<>();

    public TestMenu(Context context) {
        super(context);
        init();
    }

    public TestMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);


        ShadowButton trigger = new ShadowButton(getContext());
        trigger.setImageResource(R.drawable.ic_logo);
        trigger.setButtonColor(Color.parseColor("#88d0c7"));
        Resources r = getResources();
        int px1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, r.getDisplayMetrics());
        int px2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, r.getDisplayMetrics());

        ViewGroup.LayoutParams triggerLayoutParams = new ViewGroup.LayoutParams(px1, px1);
        addView(trigger, triggerLayoutParams);

        for (int i = 0; i < 6; i++) {

            ShadowButton btn = new ShadowButton(getContext());
            btn.setId(i);
            btn.setImageResource(R.drawable.ic_location);
            btn.setButtonColor(Color.parseColor("#88d0c7"));
            //btn.setBackgroundColor(Color.RED);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(px2, px2);
            layoutParams.gravity = Gravity.CENTER;
            addView(btn, 0, layoutParams);
            btn.setVisibility(INVISIBLE);


        }

        trigger.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setClickable(false);
                if (getChildAt(0).getVisibility() == VISIBLE) {
                    for (int i = 0; i < 6; i++) {
                        getChildAt(i).setVisibility(INVISIBLE);
                    }

                    setClickable(true);
                } else {
                    MyScaleAnimation scaleAnimation = new MyScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    scaleAnimation.setDuration(50);
                    scaleAnimation.setIndex(5);

                    scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            MyScaleAnimation animation1 = (MyScaleAnimation) animation;
                            int oldIndex = animation1.getIndex();
                            int newIndex = oldIndex - 1;
                            log("onAnimationEnd index = " + oldIndex);
                            getChildAt(oldIndex).setVisibility(VISIBLE);

                            if (newIndex < 0) {
                                setClickable(true);
                                return;
                            }
                            MyScaleAnimation scaleAnimation1 = new MyScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            scaleAnimation1.setDuration(50);
                            scaleAnimation1.setIndex(newIndex);
                            scaleAnimation1.setAnimationListener(this);

                            getChildAt(newIndex).startAnimation(scaleAnimation1);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });


                    getChildAt(5).startAnimation(scaleAnimation);
                }
            }
        });
    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }
}
