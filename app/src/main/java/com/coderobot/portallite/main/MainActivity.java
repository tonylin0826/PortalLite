package com.coderobot.portallite.main;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coderobot.portallite.R;
import com.coderobot.portallite.model.ui.WeekPagerIndicator;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    private ViewPager mViewPager;
    private ScheduleAdapter mAdapter;
    private ArrayList<View> mScheduleViews;
    private ContextMenuDialogFragment mContextMenuDialogFragment;

    private View root;
    private int width;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initActionBar();

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x / 3;
        log("width = " + width);
    }

    public void initContextMenuDialog(int height) {
        List<MenuObject> menuObjects = new ArrayList<>();

        String[] titleArray = getResources().getStringArray(R.array.str_array_menus);
        int[] resArray = {R.drawable.ic_schedule, R.drawable.ic_grade, R.drawable.ic_mail, R.drawable.ic_graduate, R.drawable.ic_survey, R.drawable.ic_schedule, R.drawable.ic_location};
        for (int i = 0; i < titleArray.length; i++) {
            MenuObject menuObject = new MenuObject(titleArray[i]);
            menuObject.setResource(resArray[i]);
            menuObject.setBgColor(Color.parseColor("#00000000"));
            menuObjects.add(menuObject);
        }


        mContextMenuDialogFragment = ContextMenuDialogFragment.newInstance(height, menuObjects);

    }

    public void initActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.portal_actionBar);

        View settings = toolbar.findViewById(R.id.action_settings);

        ViewGroup.LayoutParams settingsLayoutParams = settings.getLayoutParams();

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int l = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            settingsLayoutParams.width = l;
            settingsLayoutParams.height = l;
            initContextMenuDialog(l);
        }

        settings.setLayoutParams(settingsLayoutParams);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContextMenuDialogFragment.show(getSupportFragmentManager(), "dialog");
            }
        });

        setSupportActionBar(toolbar);
    }

    public void initView() {
        root = findViewById(R.id.root);

        mScheduleViews = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            TextView v = new TextView(this);
            v.setBackgroundColor(Color.WHITE);
            v.setText("" + (i + 1));
            mScheduleViews.add(v);
        }

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new ScheduleAdapter(mScheduleViews);

        mViewPager.setAdapter(mAdapter);
        WeekPagerIndicator weekPagerIndicator = (WeekPagerIndicator) findViewById(R.id.indicator);
        weekPagerIndicator.setViewPager(mViewPager);
    }

    private class ScheduleAdapter extends PagerAdapter {

        private ArrayList<View> mViews;

        public ScheduleAdapter(ArrayList<View> views) {
            mViews = views;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mViews.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public void addView(View view) {
            if (mViews == null) return;

            mViews.add(view);
            notifyDataSetChanged();
        }
    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }
}
