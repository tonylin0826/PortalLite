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
import android.widget.TextView;
import android.widget.Toast;

import com.coderobot.portallite.R;
import com.coderobot.portallite.manager.PreferenceInfoManager;
import com.coderobot.portallite.model.data.Course;
import com.coderobot.portallite.model.data.Semester;
import com.coderobot.portallite.model.data.User;
import com.coderobot.portallite.model.response.ScheduleResult;
import com.coderobot.portallite.model.response.SemestersResult;
import com.coderobot.portallite.util.AnimUtil;
import com.coderobot.portallite.util.CommonUtil;
import com.coderobot.portallite.util.UIUtil;
import com.daimajia.numberprogressbar.NumberProgressBar;

import java.lang.ref.WeakReference;
import java.util.List;


public class LoginActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    private NumberProgressBar mPgbLogin;
    private EditText mEdtID;
    private EditText mEdtPassWd;
    private TextView mTvAppName;
    private Dialog mAboutDialog;
    private UIHandler mHandler = new UIHandler(this);
    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();

        initDialog();

        animateLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();

        PreferenceInfoManager preferenceInfoManager = PreferenceInfoManager.getInstance(this);

        if (true || preferenceInfoManager.getIsLogin()) {

            setUIEnable(false);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
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

                mHandler.obtainMessage(Define.Message.MSG_API_LOGIN, new User(mEdtID.getText().toString(), mEdtPassWd.getText().toString())).sendToTarget();

                break;
            case R.id.tv_app_name:
                mAboutDialog.show();
                break;
        }
    }

    private void handleMessage(Message msg) {
        switch (msg.what) {
            case Define.Message.MSG_API_LOGIN:
                User user = (User) msg.obj;

                PortalLiteApi.login(user, new PortalLiteApi.PortalLiteApiLoginListener() {

                    @Override
                    public void onReturn(int retCode, String message) {
                        if (retCode < 0) {
                            setUIEnable(true);
                            toast(message);
                            return;
                        }

                        mPgbLogin.setProgress(10);

                        mHandler.obtainMessage(Define.Message.MSG_API_GET_SEMESTERS).sendToTarget();
                    }

                });

                break;
            case Define.Message.MSG_API_GET_SEMESTERS:

                PortalLiteApi.getSemesters(new PortalLiteApi.PortalLiteSemestersListener() {
                    @Override
                    public void onReturn(int retCode, SemestersResult semestersResult, String message) {

                        if (semestersResult == null || semestersResult.semesters == null || semestersResult.semesters.isEmpty()) {
                            setUIEnable(true);
                            toast(message);
                            return;
                        }

                        mPgbLogin.setProgress(30);

                        Semester currentSemester = semestersResult.semesters.get(0);
                        mHandler.obtainMessage(Define.Message.MSG_API_GET_SCHEDULE, currentSemester).sendToTarget();

                    }
                });
                break;

            case Define.Message.MSG_API_GET_SCHEDULE:

                if (msg.obj == null) break;

                Semester semester = (Semester) msg.obj;

                PortalLiteApi.getSchedule(semester, new PortalLiteApi.PortalLiteApiScheduleListener() {
                    @Override
                    public void onReturn(int retCode, ScheduleResult scheduleResult, String message) {

                        if (scheduleResult == null || scheduleResult.courses == null || scheduleResult.courses.isEmpty()) {
                            setUIEnable(true);
                            toast(message);
                            return;
                        }

                        mPgbLogin.setProgress(50);

                        List<Course> courses = scheduleResult.courses;

                    }
                });

                break;

        }
    }

    private void initView() {

        View root = findViewById(R.id.root);
        mBtnLogin = (Button) findViewById(R.id.btn_login);

        mEdtID = (EditText) findViewById(R.id.edt_id);
        mEdtPassWd = (EditText) findViewById(R.id.edt_password);
        mPgbLogin = (NumberProgressBar) findViewById(R.id.pgb_login_progress);
        mTvAppName = (TextView) findViewById(R.id.tv_app_name);


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

        TextView tvAppVersionBuildTime = (TextView) mAboutDialog.findViewById(R.id.tv_version_build_date);
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
        TextView tv = new TextView(this);
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
