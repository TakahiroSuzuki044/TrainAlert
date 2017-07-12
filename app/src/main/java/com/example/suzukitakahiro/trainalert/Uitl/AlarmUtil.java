package com.example.suzukitakahiro.trainalert.Uitl;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.example.suzukitakahiro.trainalert.Db.Dto.RegisterStationDto;
import com.example.suzukitakahiro.trainalert.Db.LocationDao;
import com.example.suzukitakahiro.trainalert.Receiver.TimeReceiver;

import java.util.Calendar;

/**
 * @author suzukitakahiro on 2016/08/19.
 *         <p>
 *         アラームを登録するためのユーティリティ
 */
public class AlarmUtil {

    private static final String TIME_RECEIVER = "time_receiver";

    /**
     * 時間を指定したその日の設定時刻に通知がくるようアラートを登録する
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
        timeReceiver.setAction(TIME_RECEIVER);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, 0, timeReceiver, PendingIntent.FLAG_UPDATE_CURRENT);

        // 設定した時間でブロードキャストする
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }

    /**
     * 取得した位置情報をアラーム位置として登録する
     *
     * @param context  コンテキスト
     * @param location 位置情報
     * @return DB挿入が成功したかどうか
     */
    public boolean setAlarmInLocation(Context context, Location location) {

        LocationDao dao = new LocationDao(context);

        RegisterStationDto dto = new RegisterStationDto();
        dto.station_name = "test";
        dto.line_name = "路線名はない";
        dto.st_latitude = location.getLatitude();
        dto.st_longitude = location.getLongitude();

        return dao.insert(dto);
    }
}
