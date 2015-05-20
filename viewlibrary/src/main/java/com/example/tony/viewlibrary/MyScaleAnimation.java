package com.example.tony.viewlibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.ScaleAnimation;

/**
 * Created by Tony on 2015/4/4.
 */
public class MyScaleAnimation extends ScaleAnimation {
    private int mIndex = 0;
    public MyScaleAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScaleAnimation(float fromX, float toX, float fromY, float toY) {
        super(fromX, toX, fromY, toY);
    }

    public MyScaleAnimation(float fromX, float toX, float fromY, float toY, float pivotX, float pivotY) {
        super(fromX, toX, fromY, toY, pivotX, pivotY);
    }

    public MyScaleAnimation(float fromX, float toX, float fromY, float toY, int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        super(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue);
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public int getIndex() {
        return mIndex;
    }
}
