package com.example.suzukitakahiro.trainalert.Uitl;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

/**
 * @author suzukitakahiro on 2016/08/28.
 *         <p/>
 *         Gpsで利用するUtil
 */
public class LocationUtil {

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Context mContext;

    /**
     * 重複を防ぐために既に現在位置取得がスタートしているかチェックするフラグを用意
     */
    private boolean isSearchedLocationFrag = false;

    public void getLocation(Context context) {

        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mContext = context;

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
                stopUpdate();
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

            // 現在位置の取得を行っていない場合は取得処理を実行する
            if (!isSearchedLocationFrag) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, mLocationListener);
                isSearchedLocationFrag = true;
            }
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
            if (isSearchedLocationFrag) {
                mLocationManager.removeUpdates(mLocationListener);
            }
        }
    }
}
