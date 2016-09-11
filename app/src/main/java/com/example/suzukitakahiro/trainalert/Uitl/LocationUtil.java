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

import com.example.suzukitakahiro.trainalert.Db.LocationColumns;
import com.example.suzukitakahiro.trainalert.Db.LocationDao;

import java.util.HashMap;

/**
 * @author suzukitakahiro on 2016/08/28.
 *         <p/>
 *         Gpsで利用するUtil
 */
public class LocationUtil implements DialogInterface.OnCancelListener {

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Context mContext;
    private ProgressDialog mProgressDialog;

    /**
     * 重複を防ぐために既に現在地取得がスタートしているかチェックするフラグを用意
     */
    private static boolean isSearchedLocationFrag = false;

    public void savedLocation(Context context) {

        // 現在位置を取得中の場合は何もしない(二度押し禁止)
        if (isSearchedLocationFrag) {
            Log.d("locationUtil", "二度押し禁止通過");
            return;
        }

        isSearchedLocationFrag = true;

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

                // ダイアログが表示されていた場合消す
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

                // アラーム位置を登録する
                insertLocation(location);
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

            // 現在位置の取得処理を実行する
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
            setSpinnerDialog();
            Log.d("locationUtil", "start_location_search");
        }

    }

    /**
     * 取得した位置情報をアラーム位置として登録する
     *
     * @param location 位置情報
     * @return DB挿入が成功したかどうか
     */
    private boolean insertLocation(Location location) {
        LocationDao dao = new LocationDao(mContext);

        // TODO: 2016/09/05 運用時には入力されたタイトルを設定する
        String title = "test";

        // 緯度、経度を格納
        HashMap<String, Double> hashMap = new HashMap<>();
        hashMap.put(LocationColumns.LATITUDE, location.getLatitude());
        hashMap.put(LocationColumns.LONGITUDE, location.getLongitude());

        return dao.insert(title, hashMap);
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
                Log.d("locationUtil", "stop_location_search");
                isSearchedLocationFrag = false;
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
}
