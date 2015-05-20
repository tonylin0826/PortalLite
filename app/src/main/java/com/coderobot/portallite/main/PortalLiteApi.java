package com.coderobot.portallite.main;

import android.os.AsyncTask;
import android.util.Log;

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

import java.net.URI;
import java.util.ArrayList;

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

    public static void login(User user, PortalLiteApiLoginListener listener) {

        new LoginTask(listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, user);
    }

    private static class LoginTask extends AsyncTask<User, Void, Void> {

        private PortalLiteApiLoginListener mListener;
        private LoginResult mLoginResult;

        public LoginTask(PortalLiteApiLoginListener listener) {
            this.mListener = listener;
        }

        @Override
        protected Void doInBackground(User... users) {
            log("LoginTask doInBackground");


            String result = postLogin(LOGIN_API, users[0]);

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

    public static void getSemesters(PortalLiteSemestersListener listener) {

        new SemestersTask(listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class SemestersTask extends AsyncTask<Void, Void, String> {

        private PortalLiteSemestersListener mListener;

        public SemestersTask(PortalLiteSemestersListener listener) {
            this.mListener = listener;
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpGet httpGet = new HttpGet(SEMESTERS_API);
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
                mListener.onReturn(SUCCESS, new Gson().fromJson(aResult, SemestersResult.class), "Get semesters success");
            } else
                mListener.onReturn(FAILED, null, "Get semester failed");
        }
    }

    public static void getSchedule(Semester semester, PortalLiteApiScheduleListener listener) {

        new ScheduleTask(listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, semester);
    }

    private static class ScheduleTask extends AsyncTask<Semester, Void, String> {

        private PortalLiteApiScheduleListener mListener;

        public ScheduleTask(PortalLiteApiScheduleListener listener) {
            this.mListener = listener;
        }

        @Override
        protected String doInBackground(Semester... semesters) {
            log("LoginTask doInBackground");

            return post(SCHEDULE_API, semesters[0]);

        }

        @Override
        protected void onPostExecute(String aResult) {
            super.onPostExecute(aResult);
            log("LoginTask onPostExecute");

            if (aResult != null)
                mListener.onReturn(SUCCESS, new Gson().fromJson(aResult, ScheduleResult.class), "Get schedule success");
            else
                mListener.onReturn(FAILED, null, "Get schedule failed");

        }
    }

    private static String post(String uri, Object content) {
        Gson gson = new Gson();
        String entity = gson.toJson(content);

        HttpResponse httpResponse;
        try {
            HttpPost post = new HttpPost(new URI(uri));

            post.setHeader("Content-Type", "application/json; charset=utf-8");

            log("post = " + entity);

            post.setEntity(new StringEntity(entity));

            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpResponse = httpClient.execute(post);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(httpResponse.getEntity());
                log("post result = " + result);
                return result;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        log("post result = null");
        return null;
    }

    private static String postLogin(String uri, User user) {
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
                log("post result = " + result);
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
