package com.example.suzukitakahiro.trainalert.Uitl;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.suzukitakahiro.trainalert.Receiver.TimeReceiver;

import java.util.Calendar;

/**
 * @author suzukitakahiro on 2016/08/19.
 *
 * アラームユーティリティ
 */
public class AlarmUtil {

    /**
     * 時間を指定したその日の設定時刻にアラートを表示させる
     *
     * @param context   コンテキスト
     * @param hourOfDay アラートを表示させる、時間
     * @param minute    アラートを表示させる、分
     */
    public void setAlarmInTime(Context context, int hourOfDay, int minute) {

        // 時間を指定
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, date, hourOfDay, minute, 0);

        // TimeReceiverにキャッチしてもらう
        Intent timeReceiver = new Intent(context, TimeReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, 0, timeReceiver, PendingIntent.FLAG_UPDATE_CURRENT);

        // 設定した時間でブロードキャストする
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }
}
