package com.example.suzukitakahiro.trainalert.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
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
import com.example.suzukitakahiro.trainalert.Uitl.GoogleApi.FusedLocationUtil;

import static com.example.suzukitakahiro.trainalert.Uitl.ConstantsUtil.PREF_KEY_IS_REQUESTED_STOP;
import static com.example.suzukitakahiro.trainalert.Uitl.ConstantsUtil.PREF_KEY_IS_REQUESTED_STOP_LOCATION_CHECK;

public class MainActivity extends BaseActivity {

    private ProgressDialog mProgressDialog;

    /**
     * 登録地と現在地の毎時照合がスタートしているか
     */
    private boolean mIsStartedLocationSearch = false;

    private FusedLocationUtil mFusedLocationUtil;

    private Activity mActivity;

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
        if (mFusedLocationUtil != null) {
            mFusedLocationUtil = null;
        }
        mIsStartedLocationSearch = false;
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
            // 初回アップデートはこの機能を閉じてリリースする
            case R.id.select_location:

                // 二重の現在地取得を防ぐため
                if (!mIsStartedLocationSearch) {
                    mIsStartedLocationSearch = true;

                    mFusedLocationUtil = FusedLocationUtil.getInstance();
                    mFusedLocationUtil.kickOffLocationRequest(getApplicationContext(), mPlayLocationCallback);

                    // 現在地取得中はダイアログを表示する
                    DialogUtil dialogUtil = new DialogUtil();
                    mProgressDialog = dialogUtil.showSpinnerDialog(this, mListener);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private FusedLocationUtil.PlayLocationCallback mPlayLocationCallback = new FusedLocationUtil.PlayLocationCallback() {
        @Override
        public void onLocationChanged(Location location, String lastUpdateTime) {

            // ダイアログが表示されていた場合消す
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            // アラーム位置を登録する
            AlarmUtil alarmUtil = new AlarmUtil();
            alarmUtil.setAlarmInLocation(getApplicationContext(), location);

            mFusedLocationUtil.stopLocationUpdates();
            mIsStartedLocationSearch = false;
        }

        @Override
        public void onConnectionError(int ErrorCode) {

            // ダイアログが表示されていた場合消す
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            mFusedLocationUtil.stopLocationUpdates();
            mIsStartedLocationSearch = false;
        }
    };

    /**
     * 位置情報取得時に表示するスピナーダイアログのキャンセルイベントリスナ
     */
    private DialogInterface.OnCancelListener mListener = new DialogInterface.OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {

            // 現在地取得を停止する
            if (mFusedLocationUtil != null) {
                mFusedLocationUtil.stopLocationUpdates();
                mFusedLocationUtil = null;
            }
            mIsStartedLocationSearch = false;
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
