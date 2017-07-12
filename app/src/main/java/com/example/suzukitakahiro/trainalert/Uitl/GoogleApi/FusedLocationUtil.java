package com.example.suzukitakahiro.trainalert.Uitl.GoogleApi;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by suzukitakahiro on 2017/03/13.
 * <p>
 * GooglePlayServiceを利用した現在位置取得クラス
 */
public class FusedLocationUtil extends GoogleApiUtil implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "FusedLocationUtil";

    /**
     * startResolutionForResultの識別子
     */
    public static final int ERROR_CODE_NO_RESOLVE = -1;

    /**
     * 標準となる更新頻度を指定する。
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * 更新頻度の上限を指定する。
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /**
     * 最新のロケーション
     */
    private Location mCurrentLocation;

    /**
     * GoogleApiClient
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * ロケーション取得時のコールバック
     */
    private PlayLocationCallback mCallback;

    /**
     * ロケーションの最終更新時間
     */
    private String mLastUpdateTime;

    /**
     * ロケーション取得をリクエストされているか
     */
    protected Boolean mRequestingLocationUpdates;

    private static FusedLocationUtil sInstance;

    private FusedLocationUtil() {
    }

    /**
     * 本クラスのインスタンスを返却する（シングルトン）
     */
    public static FusedLocationUtil getInstance() {
        if (sInstance == null) {
            sInstance = new FusedLocationUtil();
        }
        return sInstance;
    }

    /**
     * ロケーションチェック処理をキックする
     *
     * @param context コンテキスト
     */
    public void kickOffLocationRequest(Context context, PlayLocationCallback callback) {
        mLastUpdateTime = "";
        mCallback = callback;
        mGoogleApiClient = connectGoogleApiClient(context, this, this);
        mLocationRequest = getLocationRequest();

        // GoogleAPIに接続
        mRequestingLocationUpdates = true;
    }

    /**
     * ロケーションチェックを開始する
     */
    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * ロケーションチェックを停止する
     */
    public void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates: ");

        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;

            // 既に接続されている場合はロケーションチェックを停止する
            if (mGoogleApiClient.isConnected()) {
                Log.d(TAG, "stopLocationUpdates: removeLocationUpdates");
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }

            sInstance = null;
        }
    }

    /**
     * GoogleAPIと接続が確立した後の処理
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connected to GoogleApiClient");

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * GoogleAPIとの接続が再確立した場合の処理
     * <p>
     * GoogleAPIとの接続は様々な理由で解除されるため、再接続の処理を記述する必要がある。
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended ");

        mGoogleApiClient.reconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // GoogleAPIとの接続が失敗した場合の処理を記述する
        Log.d(TAG, "onConnectionFailed: ");

        if (!connectionResult.hasResolution()) {

            // 解決策がないので、エラー処理
            mCallback.onConnectionError(ERROR_CODE_NO_RESOLVE);
        } else {

            // エラーコードを返却
            mCallback.onConnectionError(connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: ");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        mCallback.onLocationChanged(mCurrentLocation, mLastUpdateTime);
    }

    /**
     * ロケーション取得時のコールバック
     */
    public interface PlayLocationCallback {
        void onLocationChanged(Location location, String lastUpdateTime);

        void onConnectionError(int ErrorCode);
    }
}
