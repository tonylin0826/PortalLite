package com.coderobot.portallite.main;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.coderobot.portallite.R;
import com.coderobot.portallite.model.data.ClassTime;
import com.coderobot.portallite.model.data.Course;
import com.coderobot.portallite.model.data.Semester;
import com.coderobot.portallite.model.ui.FontTextView;
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
    private Semester mCurrentSemester;

    private View root;
    private int width;

    private Global global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        global = (Global) getApplicationContext();

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
        mCurrentSemester = global.preferenceInfoManager.getCurrentSemester();

        root = findViewById(R.id.root);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new ScheduleAdapter(mCurrentSemester);

        mViewPager.setAdapter(mAdapter);
        WeekPagerIndicator weekPagerIndicator = (WeekPagerIndicator) findViewById(R.id.indicator);
        weekPagerIndicator.setViewPager(mViewPager);
    }

    private class ScheduleAdapter extends PagerAdapter {

        private ArrayList<LinearLayout> mViews = new ArrayList<>();
        private ArrayList<Course> mCourses;

        public ScheduleAdapter(Semester semester) {
            mCourses = global.portalLiteDB.getCourses(semester);

            for (int i = 0; i < 6; i++) {
                LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_schedule, null);
                ListView listView = (ListView) linearLayout.findViewById(R.id.listview);

                listView.setAdapter(new CourseAdapter(mCourses, i + 1));

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        log("click " + view.getTag());
                        Intent intent = new Intent(MainActivity.this, ClassActivity.class);
                        Bundle bundle = new Bundle();

                        bundle.putString(Define.IntentKey.INTENT_COURSE_KEY, (String) view.getTag());
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                });

                mViews.add(linearLayout);
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LinearLayout linearLayout = mViews.get(position);

            container.addView(linearLayout);
            return linearLayout;
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }


    private class CourseAdapter extends BaseAdapter {


        private ArrayList<String> mNames = new ArrayList<>();
        private ArrayList<String> mRooms = new ArrayList<>();
        private ArrayList<String> mIDs = new ArrayList<>();
        private ArrayList<ClassTime> mClasstimes = new ArrayList<>();


        public CourseAdapter(ArrayList<Course> courses, int day) {

            log("1course count = " + courses.size());
            for (Course course : courses) {
                for (ClassTime classTime : course.ctimes) {
                    if (classTime.day_of_week == day) {
                        mNames.add(course.name);
                        mRooms.add(course.classroom);
                        mIDs.add(course.id);
                        mClasstimes.add(classTime);
                    }
                }
            }
        }

        @Override
        public int getCount() {
            return mNames.size();
        }

        @Override
        public Object getItem(int position) {
            return mNames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View linearLayout;
            if (convertView != null) linearLayout = convertView;
            else
                linearLayout = getLayoutInflater().inflate(R.layout.layout_schedule_listview_item2, parent, false);

            FontTextView tvTime = (FontTextView) linearLayout.findViewById(R.id.time);


            ClassTime classTime = mClasstimes.get(position);
            String name = mNames.get(position);
            String room = mRooms.get(position);


            tvTime.setText("第" + classTime.time_of_day + "節 " + String.format("%d:10~%d:00", classTime.time_of_day + 7, classTime.time_of_day + 8));

            FontTextView tvName = (FontTextView) linearLayout.findViewById(R.id.name);
            tvName.setText(name);

            FontTextView tvRoom = (FontTextView) linearLayout.findViewById(R.id.room);
            tvRoom.setText(room);


            ImageView imID = (ImageView) linearLayout.findViewById(R.id.course_id);

            TextDrawable textDrawable = TextDrawable.builder()
                    .beginConfig()
                    .fontSize(40)
                    .textColor(Color.WHITE)
                    .endConfig()
                    .buildRound(mIDs.get(position), Color.parseColor("#78909c"));

            imID.setImageDrawable(textDrawable);

            linearLayout.setTag(mIDs.get(position));

            return linearLayout;
        }
    }
}
