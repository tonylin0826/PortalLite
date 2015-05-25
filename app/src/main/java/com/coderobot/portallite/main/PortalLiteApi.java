package com.coderobot.portallite.main;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.coderobot.portallite.manager.PreferenceInfoManager;
import com.coderobot.portallite.model.data.Course;
import com.coderobot.portallite.model.data.Semester;
import com.coderobot.portallite.model.data.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by the great Tony on 2/14/15.
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

    private static class LoginTask extends AsyncTask<User, Void, Boolean> {

        private PortalLiteApiLoginListener mListener;
        private Context mContext;

        public LoginTask(Context context, PortalLiteApiLoginListener listener) {
            mListener = listener;
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(User... users) {
            log("LoginTask doInBackground");

            return postLogin(mContext, LOGIN_API, users[0]);

        }

        @Override
        protected void onPostExecute(Boolean aResult) {
            super.onPostExecute(aResult);
            log("LoginTask onPostExecute");

            if (aResult) mListener.onReturn(SUCCESS, "Login Success");
            else mListener.onReturn(FAILED, "Login failed");
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
                    String result = EntityUtils.toString(httpResponse.getEntity());
                    log("result = " + result);
                    return result;
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
                Type semestersType = new TypeToken<List<Semester>>(){}.getType();
                List<Semester> semesters = new Gson().fromJson(aResult, semestersType);
                mListener.onReturn(SUCCESS, semesters, "Get semesters success");
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

            params.add(new BasicNameValuePair("year", semesters[0].year + ""));
            params.add(new BasicNameValuePair("semester", semesters[0].semester + ""));

            return post(mContext, SCHEDULE_API, params);

        }

        @Override
        protected void onPostExecute(String aResult) {
            super.onPostExecute(aResult);
            log("ScheduleTask onPostExecute");

            Type scheduleType = new TypeToken<List<Course>>(){}.getType();
            List<Course> courses = new Gson().fromJson(aResult, scheduleType);

            if (aResult != null)
                mListener.onReturn(SUCCESS, courses, "Get schedule success");
            else
                mListener.onReturn(FAILED, null, "Get schedule failed");

        }
    }

    private static String post(Context context, String uri, ArrayList<NameValuePair> params) {

        PreferenceInfoManager preferenceInfoManager = PreferenceInfoManager.getInstance(context);
        String cookie = preferenceInfoManager.getLoginCookie();

        if (cookie == null) {
            log("cookie = null");
            return null;
        }

        HttpResponse httpResponse;
        try {
            HttpPost post = new HttpPost(new URI(uri));

            post.addHeader("Cookie", cookie);

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);

            post.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpResponse = httpClient.execute(post);

            int statusCode = httpResponse.getStatusLine().getStatusCode();

            log("status code = " + statusCode);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(httpResponse.getEntity(), "ISO-8859-1");
                log("post result = " + result);
                return result;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private static boolean postLogin(Context context, String uri, User user) {

        PreferenceInfoManager preferenceInfoManager = PreferenceInfoManager.getInstance(context);
        HttpResponse httpResponse;
        try {
            HttpPost post = new HttpPost(new URI(uri));

            ArrayList<NameValuePair> params = new ArrayList<>();

            params.add(new BasicNameValuePair("account", user.account));
            params.add(new BasicNameValuePair("password", user.password));

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);

            post.setEntity(entity);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpResponse = httpClient.execute(post);

            int statusCode = httpResponse.getStatusLine().getStatusCode();

            log("status code = " + statusCode);

            if (statusCode == 200) {
                String cookie = Arrays.toString(httpResponse.getHeaders("Set-Cookie"));
                preferenceInfoManager.setLoginCookie(cookie);
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    public interface PortalLiteApiLoginListener {

        void onReturn(int retCode, String message);
    }

    public interface PortalLiteSemestersListener {

        void onReturn(int retCode, List<Semester> semesters, String message);
    }

    public interface PortalLiteApiScheduleListener {

        void onReturn(int retCode, List<Course> courses, String message);
    }

    private static void log(String msg) {
        Log.d(TAG, msg);
    }
}
