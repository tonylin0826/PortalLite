package com.coderobot.portallite.main;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.coderobot.portallite.manager.PreferenceInfoManager;
import com.coderobot.portallite.model.data.Semester;
import com.coderobot.portallite.model.data.User;
import com.coderobot.portallite.model.response.LoginResult;
import com.coderobot.portallite.model.response.ScheduleResult;
import com.coderobot.portallite.model.response.SemestersResult;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Tony on 2/14/15.
 */
public class PortalLiteApi {
    public static final int SUCCESS = 0;
    public static final int FAILED = -1;
    private static final String TAG = "PortalLiteApi";

    private static final String BASE_URL = "http://revo.so:8888/";
    private static final String LOGIN_API = BASE_URL + "login";
    private static final String SEMESTERS_API = BASE_URL + "semesters";
    private static final String SCHEDULE_API = BASE_URL + "schedule";

    public static void login(Context context, User user, PortalLiteApiLoginListener listener) {

        new LoginTask(context, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user);
    }

    private static class LoginTask extends AsyncTask<User, Void, Void> {

        private PortalLiteApiLoginListener mListener;
        private LoginResult mLoginResult;
        private Context mContext;

        public LoginTask(Context context, PortalLiteApiLoginListener listener) {
            mListener = listener;
            mContext = context;
        }

        @Override
        protected Void doInBackground(User... users) {
            log("LoginTask doInBackground");


            String result = postLogin(mContext, LOGIN_API, users[0]);

            if (result != null) {
                mLoginResult = new Gson().fromJson(result, LoginResult.class);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aResult) {
            super.onPostExecute(aResult);
            log("LoginTask onPostExecute");

            if (mLoginResult != null && mLoginResult.status_code == 0)
                mListener.onReturn(SUCCESS, "Login Success");
            else
                mListener.onReturn(FAILED, "Login failed");

        }
    }

    public static void getSemesters(Context context, PortalLiteSemestersListener listener) {

        new SemestersTask(context, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class SemestersTask extends AsyncTask<Void, Void, String> {

        private Context mContext;
        private PortalLiteSemestersListener mListener;

        public SemestersTask(Context context, PortalLiteSemestersListener listener) {
            mContext = context;
            mListener = listener;
        }

        @Override
        protected String doInBackground(Void... params) {
            PreferenceInfoManager preferenceInfoManager = PreferenceInfoManager.getInstance(mContext);
            String cookie = preferenceInfoManager.getLoginCookie();
            if (cookie == null) return null;

            HttpGet httpGet = new HttpGet(SEMESTERS_API);
            httpGet.addHeader("Cookie", cookie);
            HttpClient client = new DefaultHttpClient();

            HttpResponse httpResponse;
            try {
                httpResponse = client.execute(httpGet);

                if (httpResponse.getStatusLine().getStatusCode() == 200) {

                    return EntityUtils.toString(httpResponse.getEntity());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(String aResult) {
            super.onPostExecute(aResult);

            if (aResult != null) {
                log("semester = " + aResult);
                mListener.onReturn(SUCCESS, new Gson().fromJson(aResult, SemestersResult.class), "Get semesters success");
            } else
                mListener.onReturn(FAILED, null, "Get semester failed");
        }
    }

    public static void getSchedule(Context context, Semester semester, PortalLiteApiScheduleListener listener) {

        new ScheduleTask(context, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, semester);
    }

    private static class ScheduleTask extends AsyncTask<Semester, Void, String> {

        private Context mContext;
        private PortalLiteApiScheduleListener mListener;

        public ScheduleTask(Context context, PortalLiteApiScheduleListener listener) {
            mContext = context;
            mListener = listener;
        }

        @Override
        protected String doInBackground(Semester... semesters) {
            log("ScheduleTask doInBackground");

            ArrayList<NameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair("semester", semesters[0].toString()));

            return post(mContext, SCHEDULE_API, params);

        }

        @Override
        protected void onPostExecute(String aResult) {
            super.onPostExecute(aResult);
            log("ScheduleTask onPostExecute");

            if (aResult != null)
                mListener.onReturn(SUCCESS, new Gson().fromJson(aResult, ScheduleResult.class), "Get schedule success");
            else
                mListener.onReturn(FAILED, null, "Get schedule failed");

        }
    }

    private static String post(Context context, String uri, ArrayList<NameValuePair> params) {

        PreferenceInfoManager preferenceInfoManager = PreferenceInfoManager.getInstance(context);
        String cookie = preferenceInfoManager.getLoginCookie();


        if (cookie == null) {
            log("1 post result = null");
            return null;
        }


        HttpResponse httpResponse;
        try {
            HttpPost post = new HttpPost(new URI(uri));

            post.addHeader("Cookie", cookie);

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            log("post = " + entity);

            post.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpResponse = httpClient.execute(post);

            log("httpResponse.getStatusLine().getStatusCode() = " + httpResponse.getStatusLine().getStatusCode());

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(httpResponse.getEntity());
                log("2 post result = " + result);
                return result;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        log("3 post result = null");
        return null;
    }

    private static String postLogin(Context context, String uri, User user) {
        PreferenceInfoManager preferenceInfoManager = PreferenceInfoManager.getInstance(context);
        HttpResponse httpResponse;
        try {
            HttpPost post = new HttpPost(new URI(uri));


            ArrayList<NameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair("account", user.account));
            params.add(new BasicNameValuePair("password", user.password));

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);

            log("post = " + entity);

            post.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpResponse = httpClient.execute(post);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                String result = EntityUtils.toString(httpResponse.getEntity());
                String cookie = Arrays.toString(httpResponse.getHeaders("Set-Cookie"));
                preferenceInfoManager.setLoginCookie(cookie);
                log("post result = " + result);
                log("login cookie = " + cookie);
                return result;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        log("post result = null");
        return null;

    }

    public interface PortalLiteApiLoginListener {

        public void onReturn(int retCode, String message);
    }

    public interface PortalLiteSemestersListener {

        public void onReturn(int retCode, SemestersResult semestersResult, String message);
    }

    public interface PortalLiteApiScheduleListener {

        public void onReturn(int retCode, ScheduleResult scheduleResult, String message);
    }

    private static void log(String msg) {
        Log.d(TAG, msg);
    }
}
