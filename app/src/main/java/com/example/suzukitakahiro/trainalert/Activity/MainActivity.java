package com.example.suzukitakahiro.trainalert.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.suzukitakahiro.trainalert.Db.Dto.AreaDto;
import com.example.suzukitakahiro.trainalert.Dialog.TimeSelectDialog;
import com.example.suzukitakahiro.trainalert.Fragment.MainFragment;
import com.example.suzukitakahiro.trainalert.Fragment.PrefFragment;
import com.example.suzukitakahiro.trainalert.R;
import com.example.suzukitakahiro.trainalert.Uitl.AlarmUtil;
import com.example.suzukitakahiro.trainalert.Uitl.DialogUtil;
import com.example.suzukitakahiro.trainalert.Uitl.GoogleApi.FusedLocationUtil;
import com.example.suzukitakahiro.trainalert.Uitl.PreferencesUtil;

import static com.example.suzukitakahiro.trainalert.R.id.prefectures_name;
import static com.example.suzukitakahiro.trainalert.Uitl.ConstantsUtil.PREF_KEY_IS_REQUESTED_STOP;
import static com.example.suzukitakahiro.trainalert.Uitl.ConstantsUtil.PREF_KEY_IS_REQUESTED_STOP_LOCATION_CHECK;

public class MainActivity extends BaseActivity {

    /**
     * リクエストコード：都道府県選択画面へ
     */
    private static final int REQ_CODE_AREA_ACTIVITY = 10;

    private ProgressDialog mProgressDialog;

    /**
     * 登録地と現在地の毎時照合がスタートしているか
     */
    private boolean mIsStartedLocationSearch = false;

    private FusedLocationUtil mFusedLocationUtil;

    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ツールバーの設定
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AreaDto areaDto = (AreaDto) PreferencesUtil
                .getObjectPreference(getApplicationContext(), PreferencesUtil.PREF_KEY_GET_PREFECTURES_CODE, new AreaDto());

        // 都道府県が保存されていない場合、都道府県を選ばせる
        if (areaDto == null) {
            Intent intent = new Intent(getApplicationContext(), AreaActivity.class);

            // MainFragmentに値を返却する
            startActivityForResult(intent, REQ_CODE_AREA_ACTIVITY);
        }

        mFragment = new MainFragment();
        setFragment(mFragment);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MainFragment.REQUEST_CODE_SETTING_RESOLUTION:
            case MainFragment.RESOLVE_CODE_CONNECTION_RESOLUTION:
                // GoogleApiClientを切断する
                mFragment.onActivityResult(requestCode, resultCode, data);
                break;
            case REQ_CODE_AREA_ACTIVITY:
                // 都道府県画面からの戻り

                if (resultCode == Activity.RESULT_OK) {

                    // オプションメニューを書き換える
                    invalidateOptionsMenu();
                }
                break;
        }
    }

    /**
     * ツールバーのメニューを生成する
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        final MenuItem item = menu.findItem(prefectures_name);
        LinearLayout layout = (LinearLayout) item.getActionView();
        TextView prefecturesNameTextView = (TextView) layout.findViewById(R.id.prefectures_name_menu_text_view);

        AreaDto areaDto = (AreaDto) PreferencesUtil.getObjectPreference(
                getApplicationContext(), PreferencesUtil.PREF_KEY_GET_PREFECTURES_CODE, new AreaDto());
        if (areaDto != null && !TextUtils.isEmpty(areaDto.pref_name)) {
            prefecturesNameTextView.setText(areaDto.pref_name);
        }
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(item);
            }
        });
        return true;
    }

    /**
     * ツールバーのメニュー選択時の挙動を定義
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // メニューごとの挙動を設定
        switch (item.getItemId()) {
            case R.id.select_station:
                // 駅指定画面へ遷移

                AreaDto areaDto = (AreaDto) PreferencesUtil.getObjectPreference(
                        getApplicationContext(), PreferencesUtil.PREF_KEY_GET_PREFECTURES_CODE, new AreaDto());

                if (areaDto != null) {
                    // 沿線選択画面へ

                    Intent intent = new Intent(this, SearchStationActivity.class);
                    intent.putExtra(PrefFragment.INTENT_KEY_PREFECTURES, areaDto.pref_cd);
                    startActivity(intent);
                } else {
                    // 都道府県が選択されていないので、都道府県を選ばせる

                    Intent intent = new Intent(this, AreaActivity.class);
                    startActivityForResult(intent, REQ_CODE_AREA_ACTIVITY);
                }
                break;

            case R.id.select_time:
                // 時間指定ダイアログを表示

                FragmentManager manager = getSupportFragmentManager();
                TimeSelectDialog dialog = new TimeSelectDialog();
                dialog.show(manager, "timeSelectDialog");
                break;

            case R.id.select_location:
                // 現在地をアラーム情報として登録する
                // 初回アップデートはこの機能を閉じてリリースする

                // 二重の現在地取得を防ぐため
                if (!mIsStartedLocationSearch) {
                    mIsStartedLocationSearch = true;

                    mFusedLocationUtil = FusedLocationUtil.getInstance();
                    mFusedLocationUtil.kickOffLocationRequest(getApplicationContext(), mPlayLocationCallback);

                    // 現在地取得中はダイアログを表示する
                    DialogUtil dialogUtil = new DialogUtil();
                    mProgressDialog = dialogUtil.showSpinnerDialog(this, mListener);
                }
                break;
            case prefectures_name:
                // 都道府県選択

                Intent intent = new Intent(getApplicationContext(), AreaActivity.class);
                startActivityForResult(intent, REQ_CODE_AREA_ACTIVITY);
                break;

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
