package com.coderobot.portallite.main;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.coderobot.portallite.R;
import com.coderobot.portallite.model.data.Course;
import com.coderobot.portallite.model.data.CourseInfo;
import com.coderobot.portallite.model.data.Homework;
import com.coderobot.portallite.model.data.Material;
import com.coderobot.portallite.model.ui.ClassPagerIndicator;
import com.coderobot.portallite.model.ui.FontTextView;
import com.coderobot.portallite.util.CommonUtil;

import java.util.ArrayList;

public class ClassActivity extends ActionBarActivity {

    private Course mCourse;

    private ArrayList<CourseInfo> mCourseInfos;
    private ArrayList<Homework> mHomeworks;
    private ArrayList<Material> mMaterials;
    private Global global;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        global = (Global) getApplicationContext();
        webView = (WebView) findViewById(R.id.webview);

        init();

        initActionbar();

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initActionbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionBar);

        FontTextView tvCourseName = (FontTextView) toolbar.findViewById(R.id.course_name);

        View settings = toolbar.findViewById(R.id.action_settings);

        ViewGroup.LayoutParams settingsLayoutParams = settings.getLayoutParams();

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int l = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            settingsLayoutParams.width = l;
            settingsLayoutParams.height = l;
        }

        settings.setLayoutParams(settingsLayoutParams);

        tvCourseName.setText(mCourse.name);

        setSupportActionBar(toolbar);
    }

    private void init() {

        // load data
        if (getIntent().getExtras() == null) finish();

        String courseID = getIntent().getExtras().getString(Define.IntentKey.INTENT_COURSE_KEY);

        mCourse = global.portalLiteDB.getCourse(courseID);

        if (mCourse == null) finish();

        mHomeworks = global.portalLiteDB.getHomeworks(mCourse);
        mCourseInfos = global.portalLiteDB.getCourseInfo(mCourse);
        mMaterials = global.portalLiteDB.getMaterial(mCourse);
    }

    private void initView() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new ClassPagerAdapter(mCourseInfos, mMaterials, mHomeworks));

        ClassPagerIndicator indicator = (ClassPagerIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
    }

    private class ClassPagerAdapter extends PagerAdapter {

        private ArrayList<View> mViews = new ArrayList<>();

        public ClassPagerAdapter(ArrayList<CourseInfo> courseInfos, ArrayList<Material> materials, ArrayList<Homework> homeworks) {

            // init course info view
            LinearLayout infoLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_class_detail, null);
            ListView infoListView = (ListView) infoLayout.findViewById(R.id.listview);

            infoListView.setAdapter(new CourseInfoAdapter(courseInfos));

            mViews.add(infoLayout);

            // init material view
            LinearLayout materialLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_class_detail, null);
            ListView materialListView = (ListView) materialLayout.findViewById(R.id.listview);

            materialListView.setAdapter(new MaterialAdapter(materials));

            mViews.add(materialLayout);

            mViews.add(new Button(ClassActivity.this));

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            container.addView(mViews.get(position));

            return mViews.get(position);
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


    private class CourseInfoAdapter extends BaseAdapter {

        private ArrayList<CourseInfo> mCourseInfos;

        public CourseInfoAdapter(ArrayList<CourseInfo> courseInfos) {
            mCourseInfos = courseInfos;
        }

        @Override
        public int getCount() {
            return mCourseInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return mCourseInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) convertView = getLayoutInflater().inflate(R.layout.layout_course_info_listview_item, parent, false);

            CourseInfo courseInfo = mCourseInfos.get(position);

            FontTextView tvDate = (FontTextView) convertView.findViewById(R.id.tv_date);
            FontTextView tvSubject = (FontTextView) convertView.findViewById(R.id.tv_subject);
            FontTextView tvDetail = (FontTextView) convertView.findViewById(R.id.tv_detail);
            ImageView imgDownload = (ImageView) convertView.findViewById(R.id.attachment);

            tvDate.setText(courseInfo.date);
            tvSubject.setText(courseInfo.subject);
            tvDetail.setText(courseInfo.detail);

            if (courseInfo.attachment == null) imgDownload.setVisibility(View.GONE);

            return convertView;
        }
    }

    private class MaterialAdapter extends BaseAdapter {

        private ArrayList<Material> mMaterials;

        public MaterialAdapter(ArrayList<Material> materials) {
            mMaterials = materials;
        }

        @Override
        public int getCount() {
            return mMaterials.size();
        }

        @Override
        public Object getItem(int position) {
            return mMaterials.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) convertView = getLayoutInflater().inflate(R.layout.layout_material_listview_item, parent, false);

            final Material material = mMaterials.get(position);

            FontTextView tvDetail = (FontTextView) convertView.findViewById(R.id.tv_detail);
            ImageView imgDownload = (ImageView) convertView.findViewById(R.id.attachment);

            tvDetail.setText(material.detail);

            imgDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonUtil.download(ClassActivity.this, "https://portal.yzu.edu.tw/VC2/" + material.attachment.replace("../", ""));

//                    Map<String, String> headers = new HashMap<>();
//                    String cookie = global.preferenceInfoManager.getLoginCookie();
//                    headers.put("Cookie", cookie);
//
//                    webView.loadUrl("https://portal.yzu.edu.tw/VC2/" + material.attachment.replace("../", ""), headers);
                }
            });

            if (material.attachment == null) imgDownload.setVisibility(View.GONE);

            return convertView;
        }
    }

}
