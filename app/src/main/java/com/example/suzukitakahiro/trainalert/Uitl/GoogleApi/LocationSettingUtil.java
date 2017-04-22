package com.example.suzukitakahiro.trainalert.Uitl.GoogleApi;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

/**
 * GoogleApiを利用して位置情報の取得が可能かのチェックを管理するクラス
 */
public class LocationSettingUtil extends GoogleApiUtil implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LocationSettingUtil";

    /**
     * クライアントが使用したいロケーションサービスのタイプを格納します。チェックに使用
     * 設定を使用して、デバイスに最適なロケーション設定があるかどうかを判断します。
     */
    protected LocationSettingsRequest mLocationSettingsRequest;

    /**
     * GoogleAPIのインスタンス
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * 位置情報の設定チェックを依頼されているか
     */
    private boolean mIsRequestCheckSetting = false;

    /**
     * 本クラスのコールバック
     */
    private LocationSettingUtilCallback mLocationSettingUtilCallback;

    /**
     * 位置情報の設定確認のリクエストを構築する
     */
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(getLocationRequest());

        // 取得出来ない場合は毎回アラートを出す
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }

    /**
     * 端末での位置情報の設定をチェックする
     *
     * @param context コンテキスト
     */
    public void checkLocationSetting(Context context, LocationSettingUtilCallback callback) {
        mLocationSettingUtilCallback = callback;
        buildLocationSettingsRequest();

        // GoogleApiの接続を試みる
        mGoogleApiClient = connectGoogleApiClient(context, this, this);
        mIsRequestCheckSetting = true;
    }

    /**
     * 設定チェックを停止する
     */
    public void stopLocationSettingChecking() {

        // 初期化
        mIsRequestCheckSetting = false;
        mLocationSettingUtilCallback = null;
    }

    /**
     * GoogleApiClientを返却する
     *
     * @return GoogleApiClient
     */
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: ");

        // リクエストされている
        if (mIsRequestCheckSetting) {

            // 1. ユーザが必要な位置情報設定を満たしているか確認する
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(
                            mGoogleApiClient,
                            mLocationSettingsRequest
                    );
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult settingsResult) {
                    mLocationSettingUtilCallback.onConnectedSuccess(settingsResult);
                }
            });
        }
        mIsRequestCheckSetting = false;

        // チェックが完了したのでコネクションは切断する
//        disconnectGoogleApiClient();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: ");

        mGoogleApiClient.reconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: ");

        mLocationSettingUtilCallback.onConnectionError(connectionResult);
        mIsRequestCheckSetting = false;
        disconnectGoogleApiClient();
    }

    /**
     * 本クラスのコールバック
     */
    public interface LocationSettingUtilCallback {

        /**
         * GoogleApiClientと接続した後、位置情報の設定と引数として返却する
         *
         * @param settingsResult GoogleApiを利用した位置情報の取得が出来るかの情報
         */
        void onConnectedSuccess(LocationSettingsResult settingsResult);

        /**
         * GoogleApiClientを切断した後、呼ばれる
         *
         * @param connectionResult 接続結果
         */
        void onConnectionError(ConnectionResult connectionResult);
    }
}
