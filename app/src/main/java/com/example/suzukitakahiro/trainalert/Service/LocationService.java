package com.example.suzukitakahiro.trainalert.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.suzukitakahiro.trainalert.Db.LocationDao;
import com.example.suzukitakahiro.trainalert.Uitl.GoogleApi.FusedLocationUtil;
import com.example.suzukitakahiro.trainalert.Uitl.NotificationUtil;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.suzukitakahiro.trainalert.Uitl.ConstantsUtil.PREF_KEY_LATITUDE;
import static com.example.suzukitakahiro.trainalert.Uitl.ConstantsUtil.PREF_KEY_LOCATION;
import static com.example.suzukitakahiro.trainalert.Uitl.ConstantsUtil.PREF_KEY_LONGITUDE;

/**
 * 位置情報取得サービス
 *
 * @author suzukitakahiro on 2016/09/14.
 */
public class LocationService extends Service {

    private static final String TAG = "LocationService";
    private Timer mTimer;
    private FusedLocationUtil mFusedLocationUtil;

    /**
     * ロケーションリスナ
     */
    private LocationListener mListener;

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

        if (mFusedLocationUtil == null) {
            mFusedLocationUtil = FusedLocationUtil.getInstance();
        }
        mFusedLocationUtil.kickOffLocationRequest(getApplicationContext(), mPlayLocationCallback);

        // 10秒ごとにチェックをスタート
        start10SecondsLocationCheck();

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * {@link Service} のライフサイクルの終了。
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
        if (mFusedLocationUtil != null) {

            // GoogleApiを利用したロケーション取得を停止
            mFusedLocationUtil.stopLocationUpdates();
        }
        mListener = null;

        // タイマーの停止
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    /**
     * GoogleApiを利用したロケーション取得処理のコールバック
     */
    private FusedLocationUtil.PlayLocationCallback mPlayLocationCallback = new FusedLocationUtil.PlayLocationCallback() {
        @Override
        public void onLocationChanged(Location location, String lastUpdateTime) {
            Log.d(TAG, "onLocationChanged: ");

            // 現在位置情報を更新
            saveLocationAtPreference(location.getLatitude(), location.getLongitude());
        }

        /**
         * GoogleApiを利用したロケーション取得が不可の場合は通常のLocation取得処理を行う
         *
         * @param ErrorCode エラーコード
         */
        @Override
        public void onConnectionError(int ErrorCode) {

            // GoogleApiを利用したロケーション取得の接続を切る
            mFusedLocationUtil.stopLocationUpdates();

            stopSelf();
        }
    };

    /**
     * 10秒ごとに現在位置と登録位置情報の距離差が200ｍ以内かチェックする。
     * 200ｍ以内の場合は通知を出す。
     */
    private void start10SecondsLocationCheck() {

        // 初期化
        mTimer = new Timer();
        // 0秒後にタスクをスケジューリング
        long startTime = 0;
        // 10秒間隔でタスクを実行させる
        long lapMilliTime = 10000;

        // 10秒ごとに現在位置と登録位置距離差を比較する
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "timer_task_run");

                // 最新の現在位置情報を保持データから取得
                SharedPreferences sp = getSharedPreferences(PREF_KEY_LOCATION, MODE_PRIVATE);
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
                    notificationUtil.createHeadsUpNotifForStation(context);

                    // サービスの停止
                    stopSelf();
                }
            }
        }, startTime, lapMilliTime);
    }

    /**
     * プリファレンスで位置情報を保存する
     *
     * @param latitude  緯度
     * @param longitude 経度
     */
    private void saveLocationAtPreference(double latitude, double longitude) {

        // プリファレンスではダブル型を保存できないのでデータ型変換
        long lLatitude = Double.doubleToLongBits(latitude);
        long lLongitude = Double.doubleToLongBits(longitude);

        // 保存
        SharedPreferences sp = getSharedPreferences(PREF_KEY_LOCATION, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(PREF_KEY_LATITUDE, lLatitude);
        editor.putLong(PREF_KEY_LONGITUDE, lLongitude);
        editor.apply();
    }
}
