package com.example.suzukitakahiro.trainalert.Uitl;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * @author suzukitakahiro on 2016/08/28.
 *         <p>
 *         Gpsで利用するUtil
 */
public class LocationUtil {

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private static Context sContext;

    /**
     * 位置情報を取得できない設定
     */
    public final static int INVALID_GET_LOCATION = 1;

    /**
     * 位置情報取得時のコールバック
     */
    public interface LocationCallback {
        void Success(Location location);

        void Error(int errorCode);
    }

    private LocationCallback mCallback;

    private static LocationUtil sInstance;

    private LocationUtil() {
    }

    public static LocationUtil getInstance(Context context) {
        sContext = context;
        if (sInstance == null) {
            sInstance = new LocationUtil();
        }
        return sInstance;
    }


    /**
     * 位置情報を取得する
     *
     * @param minTime     位置情報取得の時間間隔
     * @param minDistance 位置情報取得の距離間隔
     * @param callback    位置情報取得時のコールバック
     */
    public boolean acquireLocation(long minTime, float minDistance, LocationCallback callback) {
        mCallback = callback;
        mLocationManager = (LocationManager) sContext.getSystemService(Context.LOCATION_SERVICE);

        // 位置情報機能非搭載端末の場合
        if (mLocationManager == null) {
            // 何も行わない
            return false;
        }

        Criteria criteria = new Criteria();
        // 方位
        criteria.setBearingRequired(false);
        // 速度
        criteria.setSpeedRequired(false);
        // 高度
        criteria.setAltitudeRequired(false);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        mLocationListener = new LocationListener() {

            // 現在位置情報が更新
            @Override
            public void onLocationChanged(Location location) {
                mCallback.Success(location);
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
        String provider = mLocationManager.getBestProvider(criteria, true);

        // 位置情報の取得が可能な設定か
        if (!isGpsEnabled || !isNetworkEnabled) {
            mCallback.Error(INVALID_GET_LOCATION);
            return false;
        }

        // ランタイムパーミッションチェック
        if (Build.VERSION.SDK_INT >= 23
                && ActivityCompat.checkSelfPermission
                (sContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mCallback.Error(INVALID_GET_LOCATION);
            return false;
        }

        // 現在位置の取得処理を実行する
        mLocationManager.requestLocationUpdates(provider, minTime, minDistance, mLocationListener);
        Log.d("locationUtil", "start_location_search");
        return true;
    }

    /**
     * 現在地取得が出来ない設定の場合は設定の改善を促すダイアログを表示させる
     */
    public void showImproveLocationDialog(final Activity activity) {
        // 位置情報が有効になっていない場合は、Google Maps アプリライクな [現在地機能を改善] ダイアログを起動します。
        new AlertDialog.Builder(activity)
                .setTitle("現在地機能を改善")
                .setMessage("現在、位置情報は一部有効ではないものがあります。次のように設定すると、もっともすばやく正確に現在地を検出できるようになります:\n\n● 位置情報の設定でGPSとワイヤレスネットワークをオンにする\n\n● Wi-Fiをオンにする")
                .setPositiveButton("設定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        // 端末の位置情報設定画面へ遷移
                        try {
                            activity.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                        } catch (final ActivityNotFoundException e) {
                            // 位置情報設定画面がない糞端末の場合は、仕方ないので何もしない
                        }
                    }
                })
                .setNegativeButton("スキップ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                    }    // 何も行わない
                })
                .create()
                .show();
    }

    /**
     * 位置情報の取得を行っている場合、停止させる
     */
    public void stopUpdate() {
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(sContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            mLocationManager.removeUpdates(mLocationListener);
            Log.d("locationUtil", "stop_location_search");
        }
    }
}
