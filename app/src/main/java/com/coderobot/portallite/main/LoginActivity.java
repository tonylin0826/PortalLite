package com.coderobot.portallite.main;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.coderobot.portallite.R;
import com.coderobot.portallite.model.data.Course;
import com.coderobot.portallite.model.data.CourseInfo;
import com.coderobot.portallite.model.data.Homework;
import com.coderobot.portallite.model.data.Material;
import com.coderobot.portallite.model.data.Semester;
import com.coderobot.portallite.model.data.User;
import com.coderobot.portallite.model.ui.FontTextView;
import com.coderobot.portallite.util.AnimUtil;
import com.coderobot.portallite.util.CommonUtil;
import com.coderobot.portallite.util.UIUtil;
import com.daimajia.numberprogressbar.NumberProgressBar;

import java.lang.ref.WeakReference;
import java.util.List;


public class LoginActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private Global global;

    private NumberProgressBar mPgbLogin;
    private EditText mEdtID;
    private EditText mEdtPassWd;
    private FontTextView mTvAppName;
    private Dialog mAboutDialog;
    private UIHandler mHandler = new UIHandler(this);
    private Button mBtnLogin;

    private int mDownloadCount = 0;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        global = (Global) getApplicationContext();

        initView();

        initDialog();

        animateLayout();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://www.google.com";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        log("Response is: " + response.substring(0, 500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                log("That didn't work!");
            }
        });

        queue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (global.preferenceInfoManager.getIsLogin()) {

            setUIEnable(false);

            PortalLiteApi.login(this, global.preferenceInfoManager.getUser(), new PortalLiteApi.PortalLiteApiLoginListener() {
                @Override
                public void onReturn(int retCode, String message) {
                    if (retCode == PortalLiteApi.SUCCESS)
                        log("relogin");
                }
            });

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    // add finish
                }
            }, 1000);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (mEdtPassWd.getText().toString().isEmpty() || mEdtID.getText().toString().isEmpty()) {
                    toast(getString(R.string.str_empty_passwd_id));
                    break;
                }

                if (!CommonUtil.isNetworkConnected(this)) {
                    toast(getString(R.string.str_no_network));
                    break;
                }

                setUIEnable(false);

                mPgbLogin.setVisibility(View.VISIBLE);
                mPgbLogin.setProgress(0);

                mUser = new User(mEdtID.getText().toString(), mEdtPassWd.getText().toString());

                mHandler.obtainMessage(Define.Message.MSG_API_LOGIN, mUser).sendToTarget();

                break;
            case R.id.tv_app_name:
                mAboutDialog.show();
                break;
        }
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case Define.Message.MSG_API_LOGIN:
                final User user = (User) msg.obj;

                PortalLiteApi.login(this, user, new PortalLiteApi.PortalLiteApiLoginListener() {
                    @Override
                    public void onReturn(int retCode, String message) {
                        if (retCode < 0) {
                            setUIEnable(true);
                            toast(message);
                            return;
                        }

                        mPgbLogin.setProgress(10);
                        global.preferenceInfoManager.setUser(user);
                        mHandler.obtainMessage(Define.Message.MSG_API_GET_SEMESTERS).sendToTarget();
                    }
                });

                break;
            case Define.Message.MSG_API_GET_SEMESTERS:

                PortalLiteApi.getSemesters(this, new PortalLiteApi.PortalLiteSemestersListener() {
                    @Override
                    public void onReturn(int retCode, List<Semester> semesters, String message) {
                        if (semesters == null || semesters.isEmpty()) {
                            setUIEnable(true);
                            toast(message);
                            return;
                        }

                        mPgbLogin.setProgress(15);
                        global.portalLiteDB.insert(semesters);

                        mPgbLogin.setProgress(20);
                        Semester currentSemester = semesters.get(semesters.size() - 3);
                        global.preferenceInfoManager.setCurrentSemester(currentSemester);
                        log(currentSemester.toString());
                        mHandler.obtainMessage(Define.Message.MSG_API_GET_SCHEDULE, currentSemester).sendToTarget();
                    }
                });
                break;

            case Define.Message.MSG_API_GET_SCHEDULE:

                if (msg.obj == null) break;

                Semester semester = (Semester) msg.obj;

                PortalLiteApi.getSchedule(this, semester, new PortalLiteApi.PortalLiteApiScheduleListener() {
                    @Override
                    public void onReturn(int retCode, List<Course> courses, String message) {
                        if (courses == null || courses.isEmpty()) {
                            setUIEnable(true);
                            toast(message);
                            return;
                        }

                        mPgbLogin.setProgress(25);

                        global.portalLiteDB.insert(courses);

                        mPgbLogin.setProgress(30);

                        mHandler.obtainMessage(Define.Message.MSG_API_GET_COURSE_DETAIL, courses).sendToTarget();

                    }
                });
                break;

            case Define.Message.MSG_API_GET_COURSE_DETAIL:

                if (msg.obj == null) break;

                log("MSG_API_GET_COURSE_DETAIL");

                final List<Course> courses = (List<Course>) msg.obj;

                final int delta = 70 / courses.size() / 3;

                for (Course course : courses) {

                    PortalLiteApi.getCourseInfo(this, course, new PortalLiteApi.PortalLiteApiCourseInfoListener() {
                        @Override
                        public void onReturn(int retCode, List<CourseInfo> courseInfos, String message) {
                            log("getCourseInfo onReturn");
                            if (courseInfos == null) {
                                toast(message);
                                return;
                            }

                            mPgbLogin.setProgress(mPgbLogin.getProgress() + delta);

                            global.portalLiteDB.insert(courseInfos);

                        }
                    });

                    PortalLiteApi.getMaterial(this, course, new PortalLiteApi.PortalLiteApiMaterialListener() {
                        @Override
                        public void onReturn(int retCode, List<Material> materials, String message) {

                            log("getMaterial onReturn");
                            if (materials == null) {
                                toast(message);
                                return;
                            }

                            mPgbLogin.setProgress(mPgbLogin.getProgress() + delta);
                            global.portalLiteDB.insert(materials);

                        }
                    });

                    PortalLiteApi.getHomework(this, course, new PortalLiteApi.PortalLiteApiHomeworkListener() {
                        @Override
                        public void onReturn(int retCode, List<Homework> homeworks, String message) {
                            log("getHomework onReturn");
                            if (homeworks == null) {
                                toast(message);
                                return;
                            }

                            mPgbLogin.setProgress(mPgbLogin.getProgress() + delta);
                            global.portalLiteDB.insert(homeworks);


                            mHandler.obtainMessage(Define.Message.MSG_LOGIN_FINISHED, courses.size()).sendToTarget();
                        }
                    });
                }

                break;

            case Define.Message.MSG_LOGIN_FINISHED:
                mDownloadCount++;
                int size = (int) msg.obj;
                log("MSG_LOGIN_FINISHED mDownloadCount = " + mDownloadCount + " size = " + size);

                if (size != mDownloadCount) return;

                mPgbLogin.setProgress(100);

                global.portalLiteDB.logAll();
                global.preferenceInfoManager.setIsLogin(true);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

                break;

        }
    }

    private void initView() {

        View root = findViewById(R.id.root);
        mBtnLogin = (Button) findViewById(R.id.btn_login);

        mEdtID = (EditText) findViewById(R.id.edt_id);
        mEdtPassWd = (EditText) findViewById(R.id.edt_password);
        mPgbLogin = (NumberProgressBar) findViewById(R.id.pgb_login_progress);
        mTvAppName = (FontTextView) findViewById(R.id.tv_app_name);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            root.setPadding(0, UIUtil.getStatusBarHeight(this), 0, UIUtil.getNavigetionBartHeight(this));
        }

        mTvAppName.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);

        mEdtID.setText("s1003344");
        mEdtPassWd.setText("19921011");
    }

    private void initDialog() {
        mAboutDialog = new Dialog(this, R.style.DialogStyle);

        mAboutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        mAboutDialog.setContentView(R.layout.layout_about);

        mAboutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mAboutDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        FontTextView tvAppVersionBuildTime = (FontTextView) mAboutDialog.findViewById(R.id.tv_version_build_date);
        tvAppVersionBuildTime.setText(CommonUtil.getVersionName(this) + " " + CommonUtil.getBuildDate(this));
    }

    private void animateLayout() {
        View formLayout = findViewById(R.id.form_layout);
        View tvSlogan = findViewById(R.id.tv_slogan);

        AnimUtil.startAlphaInAnimation(this, mTvAppName);
        AnimUtil.startAlphaInAnimation(this, tvSlogan);
        AnimUtil.startTranslateInAnimation(this, formLayout);
    }

    private void setUIEnable(boolean enable) {

        if (mEdtPassWd != null) mEdtPassWd.setEnabled(enable);
        if (mEdtID != null) mEdtID.setEnabled(enable);
        if (mBtnLogin != null) mBtnLogin.setEnabled(enable);

    }

    private void toast(String msg) {
        Toast t = new Toast(this);
        t.setDuration(Toast.LENGTH_SHORT);
        FontTextView tv = new FontTextView(this);
        tv.setText(msg);
        tv.setTextColor(Color.parseColor("#228B22"));
        tv.setBackgroundResource(R.drawable.toast_bg);
        t.setView(tv);
        t.show();
    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }

    private static class UIHandler extends Handler {
        private final WeakReference<LoginActivity> mActivity;

        public UIHandler(LoginActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoginActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }
}
