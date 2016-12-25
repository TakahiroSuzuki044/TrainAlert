package com.example.suzukitakahiro.trainalert.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.suzukitakahiro.trainalert.Db.LocationDao;
import com.example.suzukitakahiro.trainalert.Uitl.LocationUtil;
import com.example.suzukitakahiro.trainalert.Uitl.NotificationUtil;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.suzukitakahiro.trainalert.Uitl.ConstantsUtil.*;

/**
 * 位置情報取得サービス
 *
 * @author suzukitakahiro on 2016/09/14.
 */
public class LocationService extends Service {

    private static final String TAG = "Service_Tag";


    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind");
        return null;
    }

    /**
     * Context#startService(Intent) の呼び出しで呼ばれる。
     * このメソッドの処理は、Context#startService(Intent)を呼び出したスレッドと同じスレッドで実行されるので
     * メインスレッドで {@link Service} を起動した場合に、ここでネットワーク通信などスレッドをブロックする処理をしてしまうと
     * UI の処理がブロックされ AND となる。
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "onStartCommand");

        // 現在位置情報の初期化
        long initLatitude = 0;
        long initLongitude = 0;
        saveLocationAtPreference(initLatitude, initLongitude);

        LocationUtil locationUtil = LocationUtil.getInstance(getApplicationContext());

        // 常時チェックのため100メートル且つ30秒ごとでチェックを行う
        long minTime = 10000;
        float minDistance = 50;

        // 位置情報取得スタート
        locationUtil.acquireLocation(minTime, minDistance, mLocationCallback);

        // 10秒ごとにチェックをスタート
        start10SecondsLocationCheck();

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * {@link Service} のライフサイクルの終了。
     */
    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
    }

    /**
     * 現在地取得用のコールバック
     */
    private LocationUtil.LocationCallback mLocationCallback = new LocationUtil.LocationCallback() {

        /**
         * 登録地と比較して200m圏内の場合はアラートを出す
         */
        @Override
        public void Success(Location location) {
            Log.d(TAG, "Success");

            // 現在位置情報を更新
            saveLocationAtPreference(location.getLatitude(), location.getLongitude());
        }

        @Override
        public void Error(int errorCode) {
            Log.d(TAG, "Error");
            LocationUtil util = LocationUtil.getInstance(getApplicationContext());

            // 位置情報の取得を停止
            util.stopUpdate();
            stopSelf();
        }
    };

    /**
     * 10秒ごとに現在位置と登録位置情報の距離差が200ｍ以内かチェックする。
     * 200ｍ以内の場合は通知を出す。
     */
    private void start10SecondsLocationCheck() {

        // 初期化
        final Timer timer = new Timer();
        // 0秒後にタスクをスケジューリング
        long startTime = 0;
        // 10秒間隔でタスクを実行させる
        long lapMilliTime = 10000;

        // 10秒ごとに現在位置と登録位置距離差を比較する
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                // 最新の現在位置情報を保持データから取得
                SharedPreferences  sp = getSharedPreferences(PREF_KEY_LOCATION, MODE_PRIVATE);
                long lLatitude = sp.getLong(PREF_KEY_LATITUDE, 0);
                long lLongitude = sp.getLong(PREF_KEY_LONGITUDE, 0);
                Double latitude = Double.longBitsToDouble(lLatitude);
                Double longitude = Double.longBitsToDouble(lLongitude);

                Context context = getApplicationContext();
                LocationDao locationDao = new LocationDao(context);
                boolean isLess200meters = locationDao.collateLocationDb(latitude, longitude);

                // 200ｍ以内のため通知する
                if (isLess200meters) {
                    NotificationUtil notificationUtil = new NotificationUtil();
                    notificationUtil.createHeadsUpNotification(context);

                    LocationUtil util = LocationUtil.getInstance(context);

                    // 位置情報の取得を停止
                    util.stopUpdate();
                    // タイマーの停止
                    timer.cancel();
                    // サービスの停止
                    stopSelf();
                }
            }
        }, startTime, lapMilliTime);
    }

    /**
     * プリファレンスで位置情報を保存する
     *
     * @param latitude      緯度
     * @param longitude     経度
     */
    private void saveLocationAtPreference(double latitude, double longitude) {

        // プリファレンスではダブル型を保存できないのでデータ型変換
        long lLatitude = Double.doubleToLongBits(latitude);
        long lLongitude = Double.doubleToLongBits(longitude);

        // 保存
        SharedPreferences  sp = getSharedPreferences(PREF_KEY_LOCATION, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(PREF_KEY_LATITUDE, lLatitude);
        editor.putLong(PREF_KEY_LONGITUDE, lLongitude);
        editor.apply();
    }
}
