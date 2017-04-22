//package com.example.suzukitakahiro.trainalert.Uitl;
//
//import android.app.Activity;
//import android.content.ActivityNotFoundException;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.location.Criteria;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AlertDialog;
//import android.util.Log;
//
//import java.util.Date;
//
///**
// * @author suzukitakahiro on 2016/08/28.
// *         <p>
// *         Gpsで利用するUtil
// */
//public class LocationUtil {
//
//    private static final String TAG = "LocationUtil";
//    private LocationManager mLocationManager;
//    private static Context sContext;
//
//    /**
//     * 位置情報を取得できない設定
//     */
//    public final static int INVALID_GET_LOCATION = 1;
//
//    /**
//     * 重複を防ぐために既に現在地取得がスタートしているかチェックするフラグを用意
//     */
//    private static boolean sIsSearchedLocationFrag = false;
//
//    /**
//     * 位置情報取得時のコールバック
//     */
//    public interface LocationCallback {
//        void Success(Location location);
//
//        void Error(int errorCode);
//    }
//
//    private LocationCallback mCallback;
//
//    private static LocationUtil sInstance;
//
//    private LocationUtil() {
//    }
//
//    public static LocationUtil getInstance(Context context) {
//        sContext = context;
//        if (sInstance == null) {
//            sInstance = new LocationUtil();
//        }
//        return sInstance;
//    }
//
//
//    /**
//     * 位置情報を取得する
//     *
//     * @param minTime       位置情報取得の時間間隔
//     * @param minDistance   位置情報取得の距離間隔
//     * @param listener      リスナ
//     * @return              取得手続きが成功した場合、True
//     */
//    public boolean acquireLocation(long minTime, float minDistance, LocationListener listener) {
//        Log.d(TAG, "acquireLocation");
//
//        // 現在位置を取得中の場合は何もしない
//        if (sIsSearchedLocationFrag) {
//            return false;
//        }
//        mLocationManager = (LocationManager) sContext.getSystemService(Context.LOCATION_SERVICE);
//
//        // 位置情報機能非搭載端末の場合
//        if (mLocationManager == null) {
//            // 何も行わない
//            return false;
//        }
//
//        // GPSもネットワーク位置も利用できない場合
//        if (!checkEnableGps()) {
//            mCallback.Error(INVALID_GET_LOCATION);
//            return false;
//        }
//
//        String provider = getProvider();
//
//        // 現在位置の取得処理を実行する
//        mLocationManager.requestLocationUpdates(provider, minTime, minDistance, listener);
//        sIsSearchedLocationFrag = true;
//        Log.d(TAG, "start_location_search");
//        return true;
//    }
//
//    /**
//     * 現在地取得が出来ない設定の場合は設定の改善を促すダイアログを表示させる
//     */
//    public void showImproveLocationDialog(final Activity activity) {
//        // 位置情報が有効になっていない場合は、Google Maps アプリライクな [現在地機能を改善] ダイアログを起動します。
//        new AlertDialog.Builder(activity)
//                .setTitle("現在地機能を改善")
//                .setMessage("現在、位置情報は一部有効ではないものがあります。次のように設定すると、もっともすばやく正確に現在地を検出できるようになります:\n\n● 位置情報の設定でGPSとワイヤレスネットワークをオンにする\n\n● Wi-Fiをオンにする")
//                .setPositiveButton("設定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(final DialogInterface dialog, final int which) {
//
//                        // 端末の位置情報設定画面へ遷移
//                        try {
//                            activity.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
//                        } catch (final ActivityNotFoundException e) {
//
//                            // 位置情報設定画面がない糞端末の場合は、仕方ないので何もしない
//                        }
//                    }
//                })
//                .setNegativeButton("スキップ", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(final DialogInterface dialog, final int which) {
//                    }    // 何も行わない
//                })
//                .create()
//                .show();
//    }
//
//    /**
//     * 位置情報の取得を行っている場合、停止させる
//     */
//    public void stopUpdate(LocationListener listener) {
//        if (mLocationManager != null) {
//
//            // 現在位置を取得を停止させる
//            mLocationManager.removeUpdates(listener);
//            sIsSearchedLocationFrag = false;
//            Log.d(TAG, "stop_location_search");
//        }
//    }
//
//    /**
//     * 最適なプロバイダを返却する
//     *
//     * @return プロバイダ
//     */
//    private String getProvider() {
//        Criteria criteria = new Criteria();
//        // 方位
//        criteria.setBearingRequired(false);
//        // 速度
//        criteria.setSpeedRequired(false);
//        // 高度
//        criteria.setAltitudeRequired(false);
//
//        return mLocationManager.getBestProvider(criteria, true);
//    }
//
//    /**
//     * ロケーションリスナを取得する
//     *
//     * @return ロケーションリスナ
//     */
//    public LocationListener getLocationListener(LocationCallback callback) {
//        mCallback = callback;
//        return new LocationListener() {
//
//            // 現在位置情報が更新
//            @Override
//            public void onLocationChanged(Location location) {
//                mCallback.Success(location);
//            }
//
//            // プロバイダの利用状況(利用出来ている/できていないなど)情報に変更があったとき
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//            }
//
//            // LocationProviderが無効になったとき
//            @Override
//            public void onProviderEnabled(String provider) {
//            }
//
//            // LocationProviderが有効になったとき
//            @Override
//            public void onProviderDisabled(String provider) {
//            }
//        };
//    }
//
//    /**
//     * GPSまたはネットワーク位置情報の取得が可能かをチェックする
//     *
//     * @return 位置情報が取得可能の場合、True
//     */
//    public boolean checkEnableGps() {
//        mLocationManager = (LocationManager) sContext.getSystemService(Context.LOCATION_SERVICE);
//
//        // 位置情報機能非搭載端末の場合
//        if (mLocationManager == null) {
//            // 何も行わない
//            return false;
//        }
//
//        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        boolean isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//        return isGpsEnabled || isNetworkEnabled;
//    }
//
//
//    /**
//     * 最後に取得した位置情報を返却する
//     *
//     * @return 現在時間から逆算して5分以内の場合のみLocationを返却し、それ以外はNullを返す
//     */
//    @Nullable
//    public Location acquireLastLocation() {
//        mLocationManager = (LocationManager) sContext.getSystemService(Context.LOCATION_SERVICE);
//
//        // 位置情報機能非搭載端末の場合
//        if (mLocationManager == null) {
//            // 何も行わない
//            return null;
//        }
//        String provider = getProvider();
//
//        Location lastLocation = mLocationManager.getLastKnownLocation(provider);
//        if (lastLocation == null) {
//            return null;
//        }
//
//        boolean isWithin5minutes = (new Date().getTime() - lastLocation.getTime()) <= (5 * 60 * 1000L);
//        if (isWithin5minutes) {
//            return lastLocation;
//        }
//        return null;
//    }
//}
