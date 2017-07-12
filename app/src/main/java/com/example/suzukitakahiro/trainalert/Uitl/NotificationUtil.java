package com.example.suzukitakahiro.trainalert.Uitl;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.example.suzukitakahiro.trainalert.Activity.MainActivity;
import com.example.suzukitakahiro.trainalert.R;

/**
 * @author suzukitakahiro on 2016/08/18.
 *         <p>
 *         ノティフィケーションユーティリティ
 */
public class NotificationUtil {

    /**
     * 時刻指定の通知を発行する
     */
    public void createHeadsUpNotifForTime(Context context) {
        String contentText = context.getResources().getString(R.string.notification_text_for_time);
        createHeadsUpNotification(context, contentText);
    }

    /**
     * 駅到着の通知を発行する
     */
    public void createHeadsUpNotifForStation(Context context) {
        String contentText = context.getResources().getString(R.string.notification_text_for_station);
        createHeadsUpNotification(context, contentText);
    }

    /**
     * HeadsUpNotificationを通知する
     *
     * @param context     コンテキスト
     * @param contentText 通知の詳細メッセージ
     */
    private void createHeadsUpNotification(Context context, String contentText) {

        // 通知に必要な情報の準備
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent mainActPendIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        String title = context.getResources().getString(R.string.notification_title);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(contentText)
                .setContentIntent(mainActPendIntent)
                .setAutoCancel(true)

                // 一般的な優先度よりちょっと高い値に設定する
                .setPriority(Notification.PRIORITY_HIGH)

                // バイブレーション(1秒振動、1秒休止、2秒振動...)のパターンを設定
                .setVibrate(new long[]{0, 1000, 1000, 2000, 1000, 1000, 1000, 2000, 1000, 1000})
                .build();

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);
    }
}
