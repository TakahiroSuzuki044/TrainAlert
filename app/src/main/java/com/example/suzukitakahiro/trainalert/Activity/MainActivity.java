package com.example.suzukitakahiro.trainalert.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.suzukitakahiro.trainalert.Dialog.TimeSelectDialog;
import com.example.suzukitakahiro.trainalert.Fragment.MainFragment;
import com.example.suzukitakahiro.trainalert.R;
import com.example.suzukitakahiro.trainalert.Uitl.AlarmUtil;
import com.example.suzukitakahiro.trainalert.Uitl.DialogUtil;
import com.example.suzukitakahiro.trainalert.Uitl.LocationUtil;

import static com.example.suzukitakahiro.trainalert.Uitl.ConstantsUtil.PREF_KEY_IS_REQUESTED_STOP;
import static com.example.suzukitakahiro.trainalert.Uitl.ConstantsUtil.PREF_KEY_IS_REQUESTED_STOP_LOCATION_CHECK;

public class MainActivity extends BaseActivity {

    private ProgressDialog mProgressDialog;

    /**
     * 登録地と現在地の毎時照合がスタートしているか
     */
    private static boolean sIsStartedLocationSearch = false;

    private LocationUtil mLocationUtil;

    private Activity mActivity;

    private LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;

        // ツールバーの設定
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setFragment(new MainFragment());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLocationListener != null) {
            mLocationUtil.stopUpdate(mLocationListener);
            mLocationListener = null;
        }
        sIsStartedLocationSearch = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isCheckRequestedStopLocation()) {
            Process.killProcess(Process.myPid());
        }
    }

    /**
     * ツールバーのメニューを生成する
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * ツールバーのメニュー選択時の挙動を定義
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // メニューごとの挙動を設定
        switch (item.getItemId()) {

            // 都道府県指定画面へ遷移
            case R.id.select_station:
                Intent intent = new Intent(this, SearchStationActivity.class);
                startActivity(intent);
                break;

            // 時間指定ダイアログを表示
            case R.id.select_time:
                FragmentManager manager = getSupportFragmentManager();
                TimeSelectDialog dialog = new TimeSelectDialog();
                dialog.show(manager, "timeSelectDialog");
                break;

            // 現在地をアラーム情報として登録する
            case R.id.select_location:

                // 二重の現在地取得を防ぐため
                if (sIsStartedLocationSearch) {
                    break;
                }
                sIsStartedLocationSearch = true;
                mLocationUtil = LocationUtil.getInstance(getApplicationContext());

                // まず現在時間から逆算して5分以内に取得した位置情報があるか確認する
                Location lastLocation = mLocationUtil.acquireLastLocation();

                // 5分以内に取得した位置情報がない場合は普通に取得する
                if (lastLocation == null) {

                    // 即時現在地を取得する
                    long minTime = 0;
                    float minDistance = 0;
                    mLocationListener = mLocationUtil.getLocationListener(mLocationCallback);
                    boolean isObtain = mLocationUtil.acquireLocation(minTime, minDistance, mLocationListener);

                    if (isObtain) {

                        // 現在地取得中はダイアログを表示する
                        DialogUtil dialogUtil = new DialogUtil();
                        mProgressDialog = dialogUtil.showSpinnerDialog(this, mListener);
                    }
                } else {

                    // アラーム位置を登録する
                    AlarmUtil alarmUtil = new AlarmUtil();
                    alarmUtil.setAlarmInLocation(getApplicationContext(), lastLocation);
                    sIsStartedLocationSearch = false;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 位置情報取得時のコールバック
     * 取得成功：アラーム位置を登録する
     */
    private LocationUtil.LocationCallback mLocationCallback = new LocationUtil.LocationCallback() {
        @Override
        public void Success(Location location) {
            // ダイアログが表示されていた場合消す
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            // アラーム位置を登録する
            AlarmUtil alarmUtil = new AlarmUtil();
            alarmUtil.setAlarmInLocation(getApplicationContext(), location);

            // 現在地取得を停止する
            mLocationUtil.stopUpdate(mLocationListener);
            mLocationListener = null;
            sIsStartedLocationSearch = false;
        }

        @Override
        public void Error(int errorCode) {
            switch (errorCode) {
                case LocationUtil.INVALID_GET_LOCATION:
                    mLocationUtil.showImproveLocationDialog(mActivity);
            }
            // ダイアログが表示されていた場合消す
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            // 現在地取得を停止する
            if (mLocationListener != null) {
                mLocationUtil.stopUpdate(mLocationListener);
                mLocationListener = null;
            }
            sIsStartedLocationSearch = false;
        }
    };

    /**
     * 位置情報取得時に表示するスピナーダイアログのキャンセルイベントリスナ
     */
    private DialogInterface.OnCancelListener mListener = new DialogInterface.OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {

            // 現在地取得を停止する
            if (mLocationListener != null) {
                mLocationUtil.stopUpdate(mLocationListener);
                mLocationListener = null;
            }
            sIsStartedLocationSearch = false;
        }
    };



    /**
     * 現在位置の取得停止をリクエストがされているかチェックする
     */
    private boolean isCheckRequestedStopLocation() {

        // 最新の現在位置情報を保持データから取得
        SharedPreferences sp = getSharedPreferences(PREF_KEY_IS_REQUESTED_STOP_LOCATION_CHECK, MODE_PRIVATE);
        return sp.getBoolean(PREF_KEY_IS_REQUESTED_STOP, false);
    }
}
