package com.example.suzukitakahiro.trainalert.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.suzukitakahiro.trainalert.Db.LocationDao;
import com.example.suzukitakahiro.trainalert.Uitl.LocationUtil;
import com.example.suzukitakahiro.trainalert.Uitl.NotificationUtil;

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
        LocationUtil locationUtil = LocationUtil.getInstance(getApplicationContext());

        // 常時チェックのため100メートル且つ30秒ごとでチェックを行う
        long minTime = 10000;
        float minDistance = 50;

        // 位置情報取得スタート
        locationUtil.acquireLocation(minTime, minDistance, mLocationCallback);
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
            Context context = getApplicationContext();
            LocationDao locationDao = new LocationDao(context);
            boolean isLess200meters = locationDao.collateLocationDb(location);

            if (isLess200meters) {
                NotificationUtil notificationUtil = new NotificationUtil();
                notificationUtil.createHeadsUpNotification(context);

                stopSelf();
            }
        }

        @Override
        public void Error(int errorCode) {
            Log.d(TAG, "Error");
            stopSelf();
        }
    };
}
