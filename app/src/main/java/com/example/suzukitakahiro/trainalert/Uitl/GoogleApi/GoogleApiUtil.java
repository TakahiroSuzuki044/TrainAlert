package com.example.suzukitakahiro.trainalert.Uitl.GoogleApi;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static com.example.suzukitakahiro.trainalert.Uitl.GoogleApi.FusedLocationUtil.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.example.suzukitakahiro.trainalert.Uitl.GoogleApi.FusedLocationUtil.UPDATE_INTERVAL_IN_MILLISECONDS;

/**
 * Created by suzukitakahiro on 2017/03/26.
 * <p>
 * GoogleApiを管理するクラス
 */
public class GoogleApiUtil {

    private static final String TAG = "GoogleApiUtil";

    /**
     * GoogleAPIのインスタンス
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * ロケーションリクエストのインスタンス
     */
    protected LocationRequest mLocationRequest;

    /**
     * GoogleAPIの構築を行う
     */
    private void buildGoogleApiClient(
            Context context, GoogleApiClient.ConnectionCallbacks connectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        Log.d(TAG, "Building GoogleApiClient");

        // GoogleAPIとの接続を確立する
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(connectionFailedListener)

                // ロケーションAPIの利用
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * ロケーションAPIの接続を行うGoogleApiClientを返却する
     *
     * @return GoogleApiClient
     */
    protected GoogleApiClient connectGoogleApiClient(
            Context context, GoogleApiClient.ConnectionCallbacks connectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        Log.d(TAG, "connectGoogleApiClient: ");

        if (mGoogleApiClient == null) {
            buildGoogleApiClient(context, connectionCallbacks, connectionFailedListener);
        }

        // GoogleAPIに接続
        if (!(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting())) {
            mGoogleApiClient.connect();
        }

        return mGoogleApiClient;
    }

    /**
     * GoogleApiClientを切断する
     */
    protected void disconnectGoogleApiClient() {
        if (mGoogleApiClient == null) {
            return;
        }

        if (mGoogleApiClient.isConnected()) {
            Log.d(TAG, "disconnectGoogleApiClient: disconnect");

            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
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
     * ロケーションリクエストを返却する
     *
     * @return LocationRequest
     */
    protected LocationRequest getLocationRequest() {
        if (mLocationRequest == null) {
            createLocationRequest();
        }
        return mLocationRequest;
    }
}
