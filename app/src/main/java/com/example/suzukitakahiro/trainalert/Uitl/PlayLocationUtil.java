package com.example.suzukitakahiro.trainalert.Uitl;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by suzukitakahiro on 2017/03/13.
 *
 * GooglePlayServiceを利用した現在位置取得クラス
 */
public class PlayLocationUtil implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected static final String TAG = "location-updates-sample";

    /**
     * startResolutionForResultの識別子
     */
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 1;

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
     * ロケーション取得時のコールバック
     */
    public interface PlayLocationCallback {
        void onLocationChanged(Location location, String lastUpdateTime);
        void onError();
    }

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    /**
     * 最新のロケーション
     */
    private Location mCurrentLocation;

    private PlayLocationCallback mCallback;

    /**
     * ロケーション取得をリクエストされているか
     */
    private Boolean mRequestingLocationUpdates;

    /**
     * ロケーションの最終更新時間
     */
    private String mLastUpdateTime;

    private static PlayLocationUtil sInstance;

    private PlayLocationUtil() {}

    public static PlayLocationUtil getInstance() {
        if (sInstance == null) {
            sInstance = new PlayLocationUtil();
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

        buildGoogleApiClient(context);

        // GoogleAPIに接続
        if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
        mRequestingLocationUpdates = true;
    }

    /**
     * GoogleAPIの構築を行う
     *
     * @param context コンテキスト
     */
    private synchronized void buildGoogleApiClient(Context context) {
        Log.d(TAG, "Building GoogleApiClient");

        // GoogleAPIとの接続を確立する
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)

                // ロケーションAPIの利用
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * ロケーションの取得設定を行う
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // 取得頻度
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        // 高精度を要求する
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            // GoogleAPIとの接続を解除する
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }
    }

    /**
     * GoogleAPIと接続が確立した後の処理
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connected to GoogleApiClient");

        // ロケーション取得がリクエストされている場合、取得を行う
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * GoogleAPIとの接続が再確立した場合の処理
     *
     * GoogleAPIとの接続は様々な理由で解除されるため、再接続の処理を記述する必要がある。
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");

        mGoogleApiClient.reconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // GoogleAPIとの接続が失敗した場合の処理を記述する
        Log.d(TAG, "onConnectionFailed: ");

        if (!connectionResult.hasResolution()) {

            // 解決策がないので、エラー処理
            mCallback.onError();
            return;
        }

        try {
            connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        mCallback.onLocationChanged(mCurrentLocation, mLastUpdateTime);
    }
}
