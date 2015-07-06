package com.coderobot.portallite.main;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadService extends IntentService {

    private static int DOWNLOAD_COUNT = 1;
    private static final String ACTION_DOWNLOAD = "com.coderobot.portallite.main.action.DOWNLOAD";

    private static final String EXTRA_URL = "com.coderobot.portallite.main.extra.URL";
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotifyBuilder;

    public static void startActionDownload(Context context, String url) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_URL, url);
        context.startService(intent);
    }


    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                final String url = intent.getStringExtra(EXTRA_URL);
                handleActionDownload(url);
            }
        }
    }

    private void handleActionDownload(String url) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);



        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Download file")
                .setProgress(100, 0, true);


        Pattern p = Pattern.compile("ame=(\\S+)&id");
        Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            builder.setContentText(matcher.group(1));
        }

        mNotificationManager.notify(DOWNLOAD_COUNT, builder.build());

        PortalLiteApi.downloadPortalFile(DownloadService.this, url);


        builder.setProgress(100, 100, false).setSmallIcon(android.R.drawable.stat_sys_download_done);

        mNotificationManager.notify(DOWNLOAD_COUNT++, builder.build());
    }
}
