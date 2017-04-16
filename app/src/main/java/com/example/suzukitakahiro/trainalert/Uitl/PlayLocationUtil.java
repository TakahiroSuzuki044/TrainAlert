package com.example.suzukitakahiro.trainalert.Uitl;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by suzukitakahiro on 2017/03/13.
 *
 * GooglePlayServiceを利用した現在位置取得クラス
 */
public class PlayLocationUtil implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected static final String TAG = "PlayLocationUtil";

    /** リクエストコード */
    private static final int REQUEST_LOCATION_SET = 123;

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
     * GoogleAPIのインスタンス
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * ロケーションリクエストのインスタンス
     */
    private LocationRequest mLocationRequest;

    /**
     * クライアントが使用したいロケーションサービスのタイプを格納します。チェックに使用
     * 設定を使用して、デバイスに最適なロケーション設定があるかどうかを判断します。
     */
    protected LocationSettingsRequest mLocationSettingsRequest;

    /**
     * 最新のロケーション
     */
    private Location mCurrentLocation;

    /**
     * ロケーション取得時のコールバック
     */
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

    /**
     * 本クラスのインスタンスを返却する（シングルトン）
     */
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
        if (!(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())) {
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

            // ロケーション取得がリクエストされている場合、取得を行う
            startLocationUpdates();
            Log.d(TAG, "onConnected: startLocationUpdates");
        } else {

            // リクエストが無い状態でコネクトした場合は、切断する
            mGoogleApiClient.disconnect();
            Log.d(TAG, "onConnected: disconnect");
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
            mCallback.onError(ERROR_CODE_NO_RESOLVE);
        } else {

            // エラーコードを返却
            mCallback.onError(connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: ");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        mCallback.onLocationChanged(mCurrentLocation, mLastUpdateTime);
    }


    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    public void checkLocationSettings() {
        buildLocationSettingsRequest();

        // 1. ユーザが必要な位置情報設定を満たしているか確認する
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // 位置情報が利用できる
                        // FusedLocationApi.requestLocationUpdatesなどを呼び出す
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        mCallback.onError(LocationSettingsStatusCodes.RESOLUTION_REQUIRED);
//                        try {
//                            // 2. ユーザに位置情報設定を変更してもらうためのダイアログを表示する
//                            status.startResolutionForResult(activity, requestCode);
//                        } catch (IntentSender.SendIntentException e) {
//                            // ignore
//                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // 位置情報が取得できず、なおかつその状態からの復帰も難しい時呼ばれるらしい
                        mCallback.onError(LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE);
                        break;
                }}});
    }

    /**
     * ロケーション取得時のコールバック
     */
    public interface PlayLocationCallback {
        void onLocationChanged(Location location, String lastUpdateTime);
        void onError(int ErrorCode);
    }
}
