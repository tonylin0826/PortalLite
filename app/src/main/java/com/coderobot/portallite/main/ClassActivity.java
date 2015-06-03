package com.coderobot.portallite.main;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coderobot.portallite.R;
import com.coderobot.portallite.model.data.Course;
import com.coderobot.portallite.model.data.CourseInfo;
import com.coderobot.portallite.model.data.Homework;
import com.coderobot.portallite.model.data.Material;

import java.util.ArrayList;

public class ClassActivity extends ActionBarActivity {

    private Course mCourse;

    private ArrayList<CourseInfo> mCourseInfos;
    private ArrayList<Homework> mHomeworks;
    private ArrayList<Material> mMaterials;


    private Global global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        global = (Global) getApplicationContext();

        initActionbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initActionbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionBar);

        TextView tvCourseName = (TextView) toolbar.findViewById(R.id.course_name);

        View settings = toolbar.findViewById(R.id.action_settings);

        ViewGroup.LayoutParams settingsLayoutParams = settings.getLayoutParams();

        if (getIntent().getExtras() == null) finish();

        String courseID = getIntent().getExtras().getString(Define.IntentKey.INTENT_COURSE_KEY);

        mCourse = global.portalLiteDB.getCourse(courseID);

        if (mCourse == null) finish();

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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
