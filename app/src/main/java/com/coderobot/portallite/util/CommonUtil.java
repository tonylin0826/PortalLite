package com.coderobot.portallite.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.coderobot.portallite.manager.PreferenceInfoManager;
import com.coderobot.portallite.model.data.ClassTime;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Tony on 2/14/15.
 */
public class CommonUtil {

    private static final String TAG = "CommonUtil";

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        } else {

            NetworkInfo[] infos = cm.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo networkInfo : infos) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getVersionName(Context context) {
        String version = "v1.0";
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = "v" + pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    public static String getBuildDate(Context context) {
        String date = "";
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            ZipFile zf = new ZipFile(ai.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            long time = ze.getTime();
            date = new SimpleDateFormat("dd/M/yyyy").format(new java.util.Date(time));
            zf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String classTimeListToString(List<ClassTime> classTimes) {
        StringBuilder result = new StringBuilder("");
        for (ClassTime classTime : classTimes) {
            result.append("__,__").append(classTime);
        }

        return result.toString();
    }

    public static void download(Context context, String url) {

        log("download " + url);

        get(context, url);

    }

    private static void get(final Context context, final String url) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                PreferenceInfoManager preferenceInfoManager = PreferenceInfoManager.getInstance(context);
                String[] cookies = preferenceInfoManager.getLoginCookie().replace("sid_key", "ASP.NET_SessionId").replace("]", "").replace("[", "").split("Set-Cookie: ");
                String cookie = cookies[cookies.length - 1].replace("; Path=/", "");
                log("cookie = " + cookie);
                if (cookie == null) return;

                HttpGet httpGet = new HttpGet(url);
                httpGet.addHeader("Cookie", cookie);
                HttpClient client = new DefaultHttpClient();

                HttpResponse httpResponse;
                try {
                    httpResponse = client.execute(httpGet);

                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        String result = EntityUtils.toString(httpResponse.getEntity());
                        log("result = " + result);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private static void log(String msg) {
        Log.d(TAG, msg);
    }
}
