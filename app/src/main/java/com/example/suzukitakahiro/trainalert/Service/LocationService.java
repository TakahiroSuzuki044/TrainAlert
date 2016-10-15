package com.example.suzukitakahiro.trainalert.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.suzukitakahiro.trainalert.Uitl.LocationUtil;

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

        // 位置情報取得スタート
        LocationUtil locationUtil = new LocationUtil();
        locationUtil.checkLocation(this);
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
}
