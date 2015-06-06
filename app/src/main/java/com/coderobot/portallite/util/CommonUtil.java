package com.coderobot.portallite.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.coderobot.portallite.main.Define;
import com.coderobot.portallite.model.data.ClassTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public static int getFileType(String fileName) {
        log("getFileType file = " + fileName);
        if (fileName.contains(".zip") || fileName.contains(".rar") || fileName.contains(".7z") || fileName.contains(".gz"))
            return Define.AttachmentFileType.ZIP;
        else if (fileName.contains(".ppt"))
            return Define.AttachmentFileType.PPT;
        else if (fileName.contains(".xls"))
            return Define.AttachmentFileType.XLS;
        else if (fileName.contains(".pdf"))
            return Define.AttachmentFileType.PDF;
        else
            return Define.AttachmentFileType.OTHER;
    }

    public static String[] removeEmptyString(String[] strings) {
        ArrayList<String> strings1 = new ArrayList<>();

        for (String s : strings) {
            if (s != null && s.length() != 0) strings1.add(s);
        }

        return strings1.toArray(new String[strings1.size()]);
    }

    private static void log(String msg) {
        Log.d(TAG, msg);
    }
}
