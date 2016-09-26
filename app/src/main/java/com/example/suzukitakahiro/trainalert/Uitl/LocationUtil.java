package com.example.suzukitakahiro.trainalert.Uitl;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import static com.example.suzukitakahiro.trainalert.Db.LocationColumns.*;
import com.example.suzukitakahiro.trainalert.Db.LocationDao;

import java.util.HashMap;

/**
 * @author suzukitakahiro on 2016/08/28.
 *         <p/>
 *         Gpsで利用するUtil
 */
public class LocationUtil implements DialogInterface.OnCancelListener {

    /** 現在地取得の目的 */
    private static final int SAVE_LOCATION = 1;
    private static final int CHECK_LOCATION = 2;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Context mContext;

    /** 現在地取得時に表示させるダイアログのインスタンス */
    private ProgressDialog mProgressDialog;

    /**
     * 重複を防ぐために既に現在地取得がスタートしているかチェックするフラグを用意
     */
    private static boolean sIsSearchedLocationFrag = false;

    /** アラートチェックと位置情報登録のどちらで現在地を取得しているのか判断するフラグ */
    private int mDecideAcquireStatusFrag = -1;

    private long mMinTime = 0;
    private float mMinDistance = 0;

    /**
     * 現在地をLocationDBに保存する
     */
    public void saveLocation(Context context) {

        mContext = context;

        // 現在地をDBに保存するフラグを立たせる
        mDecideAcquireStatusFrag = SAVE_LOCATION;

        // 現在位置を取得中の場合は何もしない(二度押し禁止)
        if (sIsSearchedLocationFrag) {
            Log.d("locationUtil", "二度押し禁止通過");
            return;
        }

        // 現在地の取得
        acquireLocation();
        sIsSearchedLocationFrag = true;

        // 現在地取得中はダイアログを表示する
        setSpinnerDialog();
    }

    public void checkLocation(Context context) {
        mContext = context;

        // 現在地チェックフラグを立たせる
        mDecideAcquireStatusFrag = CHECK_LOCATION;

        // 常時チェックのため100メートル且つ30秒ごとでチェックを行う
        mMinTime = 30000;
        mMinDistance = 50;

        // 現在地取得
        acquireLocation();
    }

    /**
     * 位置情報を取得する
     */
    private void acquireLocation() {
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        // 方位
        criteria.setBearingRequired(false);
        // 速度
        criteria.setSpeedRequired(false);
        // 高度
        criteria.setAltitudeRequired(false);

        mLocationListener = new LocationListener() {

            // 現在位置情報が更新
            @Override
            public void onLocationChanged(Location location) {

                // 現在地をDBに保存する
                if (mDecideAcquireStatusFrag == SAVE_LOCATION) {

                    // ダイアログが表示されていた場合消す
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }

                    // アラーム位置を登録する
                    AlarmUtil alarmUtil = new AlarmUtil();
                    alarmUtil.setAlarmInLocation(mContext, location);
                    stopUpdate();
                }

                // 現在地チェック
                if (mDecideAcquireStatusFrag == CHECK_LOCATION) {
                    checkShowAlert(location);
                }
            }

            // プロバイダの利用状況(利用出来ている/できていないなど)情報に変更があったとき
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            // LocationProviderが無効になったとき
            @Override
            public void onProviderEnabled(String provider) {
            }

            // LocationProviderが有効になったとき
            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isNetworkEnabled) {

            // ランタイムパーミッションチェック
            if (Build.VERSION.SDK_INT >= 23
                    && ActivityCompat.checkSelfPermission
                    (mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            // 現在位置の取得処理を実行する
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mMinTime, mMinDistance, mLocationListener);
            Log.d("locationUtil", "start_location_search");
        }
    }

    /**
     * 登録地と比較して200m圏内の場合はアラートを出す
     *
     * @param location
     */
    private void checkShowAlert(Location location) {
        LocationDao locationDao = new LocationDao(mContext);
        boolean isLess200meters = locationDao.collateLocationDb(location);

        if (isLess200meters) {
            NotificationUtil notificationUtil = new NotificationUtil();
            notificationUtil.createHeadsUpNotification(mContext);
        }
    }

    /**
     * 現在地取得を行っている場合、停止させる
     */
    private void stopUpdate() {
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            // 現在位置を取得を停止させる
            if (sIsSearchedLocationFrag) {
                mLocationManager.removeUpdates(mLocationListener);
                Log.d("locationUtil", "stop_location_search");
                sIsSearchedLocationFrag = false;
            }
        }
    }

    /**
     * スピナーダイアログを表示させる
     */
    private void setSpinnerDialog() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setMessage("現在地を取得中");

        // ダイアログのスタイル
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // キャンセル処理を可能かどうか
        mProgressDialog.setCancelable(true);

        // キャンセル時のリスナをセット
        mProgressDialog.setOnCancelListener(this);

        mProgressDialog.show();
    }

    /**
     * スピナーダイアログのキャンセル
     */
    @Override
    public void onCancel(DialogInterface dialog) {

        // 現在地取得を停止する
        stopUpdate();
    }

    // TODO: 2016/09/12 Networkが取得出来ない場合はGPSでチェックするように変更
    // TODO: 2016/09/12 電車に乗ったと判断したときの毎時チェックも必要
}
