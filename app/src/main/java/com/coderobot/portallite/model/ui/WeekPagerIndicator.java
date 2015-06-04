package com.coderobot.portallite.model.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.coderobot.portallite.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Tony on 2015/3/23.
 */
public class WeekPagerIndicator extends RelativeLayout {

    private static final String TAG = "WeekPagerIndicator";
    private ArrayList<View> mViews = new ArrayList<>();
    private int mWidth = 0;
    private int mHeight = 0;
    private WeakReference<ViewPager> mPagerRef;
    private int mLastPosition = 0;
    private boolean mDontDoScroll = false;

    public WeekPagerIndicator(Context context) {
        super(context);
        init();
    }

    public WeekPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeekPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mWidth = r - l;
        mHeight = b - t;
    }

    private void init() {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View root = layoutInflater.inflate(R.layout.layout_week_indicator, this, false);

        int tvIds[] = {R.id.tv_mon, R.id.tv_tue, R.id.tv_wed, R.id.tv_thu, R.id.tv_fri, R.id.tv_sat};
        int viweIds[] = {R.id.v_mon, R.id.v_tue, R.id.v_wed, R.id.v_thu, R.id.v_fri, R.id.v_sat};
        String strArray[] = getResources().getStringArray(R.array.str_array_weekdays);


        int color = getResources().getColor(R.color.color_bg);
        for (int i = 0; i < 6; i++) {
            SeeThroughTextView seeThroughTextView = (SeeThroughTextView) root.findViewById(tvIds[i]);
            seeThroughTextView.setBackgroundColor(color);
            seeThroughTextView.setText(strArray[i]);

            View v = root.findViewById(viweIds[i]);
            if (i == 0) v.setAlpha(1);
            v.setTag(i);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPagerRef == null) return;

                    mPagerRef.get().setCurrentItem((Integer) v.getTag());
                }
            });
            mViews.add(v);
        }

        addView(root, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setViewPager(ViewPager viewPager) {
        mPagerRef = new WeakReference<>(viewPager);
        mPagerRef.get().setCurrentItem(0);
        mPagerRef.get().setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                log("onPageScrolled " + positionOffset);
//                log("position = " + position);
//                log("mLastPosition = " + mLastPosition);
//                log("positionOffset = " + positionOffset);

                if (position + 1 < mViews.size() && !mDontDoScroll) {
                    mViews.get(position + 1).setAlpha(positionOffset);
                    mViews.get(position).setAlpha(1 - positionOffset);
                }

                if (positionOffset == 0.0f) {
                    for (int i=0; i<6; i++) {
                        if (i != position)
                            mViews.get(i).setAlpha(0);
                    }
                    mDontDoScroll = false;
                }
            }

            @Override
            public void onPageSelected(int position) {
                log("onPageSelected");
                mDontDoScroll = true;
                mViews.get(mLastPosition).setAlpha(0);
                mLastPosition = position;
                mViews.get(position).setAlpha(1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }
}
