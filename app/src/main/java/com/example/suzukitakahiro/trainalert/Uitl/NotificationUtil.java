package com.example.suzukitakahiro.trainalert.Uitl;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import com.example.suzukitakahiro.trainalert.R;

/**
 * @author suzukitakahiro on 2016/08/18.
 *
 * ノティフィケーションユーティリティ
 */
public class NotificationUtil {

    /**
     * HeadsUpNotificationを通知する
     *
     * @param context   コンテキスト
     */
    public void createHeadsUpNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder
                .setContentTitle("時間です")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("降りる時刻になりました")
                .setAutoCancel(true)

                // 一般的な優先度よりちょっと高い値に設定する
                .setPriority(Notification.PRIORITY_HIGH)

                // バイブレーション(1秒振動、1秒休止、2秒振動...)のパターンを設定
                .setVibrate(new long[]{0, 1000, 1000, 2000, 1000, 1000, 1000, 2000, 1000, 1000, 1000, 2000, 1000, 1000, 1000, 2000, 1000, 1000, 1000, 2000, 1000, 1000, 1000, 2000, 1000, 1000, 1000, 2000, 1000, 1000, 1000, 2000, 1000, 1000, 1000, 2000, 1000, 1000, 1000, 2000, 1000, 1000, 1000, 2000, 1000, 1000, 1000, 2000, 1000})
                .build();

        NotificationManager manager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification);
    }
}
