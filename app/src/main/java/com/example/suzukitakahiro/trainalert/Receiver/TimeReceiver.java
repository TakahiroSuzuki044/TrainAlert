package com.example.suzukitakahiro.trainalert.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.suzukitakahiro.trainalert.Uitl.NotificationUtil;

/**
 * @author suzukitakahiro on 2016/08/21.
 *
 * 時間指定でのブロードキャストレシーバー
 */
public class TimeReceiver extends BroadcastReceiver {

    /**
     * ブロードキャストをキャッチした場合、HeadsUpNotificationを通知する
     *
     * @param context   コンテキスト
     * @param intent    TimeReceiver
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtil notificationUtil = new NotificationUtil();
        notificationUtil.createHeadsUpNotifForTime(context);
    }
}
