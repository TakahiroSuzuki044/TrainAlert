package com.example.suzukitakahiro.trainalert.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.suzukitakahiro.trainalert.Db.Dto.RegisterStationDto;
import com.example.suzukitakahiro.trainalert.Db.LocationDao;
import com.example.suzukitakahiro.trainalert.Dialog.DeleteDialog;
import com.example.suzukitakahiro.trainalert.Fragment.Adapter.RegisterStationListAdapter;
import com.example.suzukitakahiro.trainalert.R;
import com.example.suzukitakahiro.trainalert.Service.LocationService;
import com.example.suzukitakahiro.trainalert.Uitl.ConstantsUtil;
import com.example.suzukitakahiro.trainalert.Uitl.GoogleApi.LocationSettingUtil;
import com.example.suzukitakahiro.trainalert.Uitl.ServiceUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.List;

import static com.example.suzukitakahiro.trainalert.Uitl.ConstantsUtil.PREF_KEY_IS_REQUESTED_STOP;
import static com.example.suzukitakahiro.trainalert.Uitl.ConstantsUtil.PREF_KEY_IS_REQUESTED_STOP_LOCATION_CHECK;

/**
 * @author suzukitakahiro on 2016/07/21.
 *         <p/>
 *         Top画面のリスト部分
 */
public class MainFragment extends BaseFragment
        implements ListView.OnItemLongClickListener,
        View.OnClickListener,
        DeleteDialog.DialogCallback {

    private static final String TAG = "MainFragment";

    /**
     * DB登録駅の全件検索
     */
    private static final int FIND_ALL = 0;

    /**
     * リクエストコード：位置情報の取得再設定を促す場合
     */
    public static final int REQUEST_CODE_SETTING_RESOLUTION = 1;
    public static final int RESOLVE_CODE_CONNECTION_RESOLUTION = 2;

    private RegisterStationListAdapter mAdapter;

    /**
     * 登録駅情報Dto
     */
    private List<RegisterStationDto> mRegStationDtos;

    private View mView;

    private Button mLocationCheckButton;

    /**
     * 位置情報の取得設定を確認するクラスのインスタンス.
     */
    private LocationSettingUtil mLocationSettingUtil;

    /**
     * 位置情報の設定をチェック中か
     */
    private boolean mIsCheckingLocationSetting;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        ViewStub tutorial = (ViewStub) mView.findViewById(R.id.fragment_main_tutorial);
        View tutorialView = tutorial.inflate();
        tutorialView.setVisibility(View.GONE);

        mLocationCheckButton = (Button) mView.findViewById(R.id.start_button);
        mView.findViewById(R.id.tutorial_detail_text2).setOnClickListener(this);
        if (mLocationCheckButton != null) {
            mLocationCheckButton.setOnClickListener(this);
        }

        // リストをセット
        setListView();

        // 全件検索
        getActivity().getSupportLoaderManager().initLoader(FIND_ALL, null, mLoaderCallbacks);
        return mView;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        boolean isStartedCheckLocation =
                ServiceUtil.checkStartedService(getActivity(), LocationService.class.getName());

        // サービス状況によって表示を変更する
        if (isStartedCheckLocation) {
            changeStartButtonOn();
        } else {
            changeStartButtonOff();
        }
    }

    /**
     * 登録したアラーム情報の一覧を作成
     */
    private void setListView() {
        mRegStationDtos = new ArrayList<>();
        mAdapter = new RegisterStationListAdapter(getContext(), R.layout.list_item_main, mRegStationDtos);

        ListView listView = (ListView) mView.findViewById(R.id.location_list_view);
        if (listView != null) {
            listView.setAdapter(mAdapter);
            listView.setOnItemLongClickListener(this);
        }
    }

    /**
     * アラート登録駅取得の処理
     */
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Loader<Cursor> cursorLoader = null;
            LocationDao locationDao = new LocationDao(getActivity());
            switch (id) {

                // 全件検索
                case FIND_ALL:
                    cursorLoader = locationDao.findAll();
                    break;
            }
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
            if (data.getCount() == 0) {
                // 登録駅情報が無い場合はチュートリアルを表示する

                mView.post(new Runnable() {
                    @Override
                    public void run() {
                        mView.findViewById(R.id.fragment_main_tutorial_stub).setVisibility(View.VISIBLE);
                        mView.findViewById(R.id.location_list_view).setVisibility(View.GONE);
                    }
                });
            } else {
                mView.post(new Runnable() {
                    @Override
                    public void run() {
                        mView.findViewById(R.id.fragment_main_tutorial_stub).setVisibility(View.GONE);
                        mView.findViewById(R.id.location_list_view).setVisibility(View.VISIBLE);
                    }
                });
            }
            mRegStationDtos = LocationDao.parseCursor(data);
            mAdapter.clear();
            mAdapter.addAll(mRegStationDtos);

            // Cursorのデータを新しく置き換え
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.notifyDataSetChanged();
        }
    };

    /**
     * リストのロングタップで削除ダイアログを表示する
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemLongClick: " + "position: " + position + "id: " + id);

        // 削除するレコードのIDを取得する
        RegisterStationDto item = (RegisterStationDto) parent.getItemAtPosition(position);
        int deleteId = item._id;

        DeleteDialog deleteDialog = DeleteDialog.getInstance(deleteId, this);
        deleteDialog.show(getActivity().getSupportFragmentManager(), "AskDeleteDialog");

        // trueを返すことでOnItemClickが呼ばれなくるなる
        return true;
    }

    /**
     * 削除ダイアログのコールバック
     *
     * @param id 削除するID
     */
    @Override
    public void onCallback(long id) {
        LocationDao memoDao = new LocationDao(getContext());

        // idを指定して削除
        boolean isSuccess = memoDao.delete(id);
        if (isSuccess) {
            Toast.makeText(getActivity(), "削除が完了しました", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "削除に失敗しました", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                Log.d(TAG, "MainFragment onClick: start_button");

                // 二重押し禁止
                mLocationCheckButton.setEnabled(false);

                boolean isStartedCheckLocation =
                        ServiceUtil.checkStartedService(getActivity(), LocationService.class.getName());
                if (isStartedCheckLocation) {
                    // サービスが開始されている場合

                    stopCheckLocation();
                } else if (!mIsCheckingLocationSetting) {
                    // 位置チェックをスタートする

                    mIsCheckingLocationSetting = true;

                    mLocationSettingUtil = new LocationSettingUtil();
                    mLocationSettingUtil.checkLocationSetting(getContext(), mSettingUtilCallback);
                }

                break;
            case R.id.tutorial_detail_text2:
                // GooglePlayストア導線

                Intent googlePlayIntent = new Intent(Intent.ACTION_VIEW);
                googlePlayIntent.setData(Uri.parse(ConstantsUtil.URI_GOOGLE_PLAY_STORE));
                startActivity(googlePlayIntent);
                break;
        }
    }

    /**
     * 位置情報の取得をしていないことを保存する
     */
    private void isRequestStopLocationCheck() {

        // 保存
        SharedPreferences sp = getContext().getSharedPreferences(PREF_KEY_IS_REQUESTED_STOP_LOCATION_CHECK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(PREF_KEY_IS_REQUESTED_STOP, true);
        editor.apply();
    }

    /**
     * 位置情報の取得を実施中であることを保存する
     */
    private void isRequestStartLocationCheck() {

        // 保存
        SharedPreferences sp = getContext().getSharedPreferences(PREF_KEY_IS_REQUESTED_STOP_LOCATION_CHECK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(PREF_KEY_IS_REQUESTED_STOP, false);
        editor.apply();
    }

    /**
     * 位置情報の取得サービスをスタートする
     */
    private void startCheckLocation() {
        boolean isStartedCheckLocation =
                ServiceUtil.checkStartedService(getActivity(), LocationService.class.getName());
        Intent intent = new Intent(getActivity(), LocationService.class);

        // サービス未実行時は実行する
        if (!isStartedCheckLocation) {
            isRequestStartLocationCheck();
            getActivity().startService(intent);
            changeStartButtonOn();
            Toast.makeText(getActivity(), "チェックを開始しました", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 位置情報の取得サービスを停止する
     */
    private void stopCheckLocation() {

        // 二重押し解禁
        mLocationCheckButton.setEnabled(true);

        Intent intent = new Intent(getActivity(), LocationService.class);

        // ロケーション取得とサービスを停止して、画面を閉じる
        isRequestStopLocationCheck();
        getActivity().stopService(intent);
        changeStartButtonOff();
        Toast.makeText(getActivity(), "チェックを終了しました", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    /**
     * 端末での位置情報の設定をチェック後のコールバック
     */
    private LocationSettingUtil.LocationSettingUtilCallback mSettingUtilCallback =
            new LocationSettingUtil.LocationSettingUtilCallback() {
                @Override
                public void onConnectedSuccess(LocationSettingsResult settingsResult) {
                    Log.d(TAG, "onConnectedSuccess: ");

                    // チェックフラグをオフ
                    mIsCheckingLocationSetting = false;

                    final Status status = settingsResult.getStatus();
                    switch (status.getStatusCode()) {

                        // 位置情報が利用できる
                        case LocationSettingsStatusCodes.SUCCESS:
                            Log.d(TAG, "onResult: SUCCESS");

                            // 二重押し解禁
                            mLocationCheckButton.setEnabled(true);

                            // 設定チェックの終了
                            mLocationSettingUtil.stopLocationSettingChecking();

                            startCheckLocation();
                            break;

                        // 改善策があるため、ユーザーに位置情報の取得設定変更を促す
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.d(TAG, "onResult: RESOLUTION_REQUIRED");

                            try {
                                // ユーザに位置情報設定を変更してもらうためのダイアログを表示する
                                status.startResolutionForResult(getActivity(), REQUEST_CODE_SETTING_RESOLUTION);
                            } catch (IntentSender.SendIntentException e) {
                                Toast.makeText(getContext(), "誠に申し訳ございません。位置情報のチェックスタートに失敗しました。", Toast.LENGTH_SHORT).show();

                                // 設定チェックの終了
                                mLocationSettingUtil.stopLocationSettingChecking();

                                // 二重押し解禁
                                mLocationCheckButton.setEnabled(true);
                            }
                            break;

                        // 位置情報が取得できず、なおかつその状態からの復帰も難しい時呼ばれるらしい
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.d(TAG, "onResult: SETTINGS_CHANGE_UNAVAILABLE");

                            Toast.makeText(getContext(), "誠に申し訳ございません。お使いの端末はサポート外です。", Toast.LENGTH_SHORT).show();

                            // 設定チェックの終了
                            mLocationSettingUtil.stopLocationSettingChecking();

                            // 二重押し解禁
                            mLocationCheckButton.setEnabled(true);
                            break;
                    }
                }

                @Override
                public void onConnectionError(ConnectionResult connectionResult) {
                    Log.d(TAG, "onConnectionError: ");

                    // チェックフラグをオフ
                    mIsCheckingLocationSetting = false;

                    //解決策があるかどうか
                    if (!connectionResult.hasResolution()) {

                        //ないので諦める
                        Toast.makeText(getContext(), "誠に申し訳ございません。お使いの端末はサポート外です。", Toast.LENGTH_SHORT).show();

                        // 設定チェックの終了
                        mLocationSettingUtil.stopLocationSettingChecking();
                        // 二重押し解禁
                        mLocationCheckButton.setEnabled(true);
                        return;
                    }

                    try {
                        //GooglePlayServicesのActivityに解決を委譲する
                        connectionResult.startResolutionForResult(getActivity(), RESOLVE_CODE_CONNECTION_RESOLUTION);

                    } catch (IntentSender.SendIntentException e) {
                        // この例外がきたらどうしようもない。諦めのメッセージを出そう
                        Toast.makeText(getContext(), "誠に申し訳ございません。お使いの端末はサポート外です。", Toast.LENGTH_SHORT).show();

                        // 設定チェックの終了
                        mLocationSettingUtil.stopLocationSettingChecking();
                        // 二重押し解禁
                        mLocationCheckButton.setEnabled(true);
                    }
                }
            };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_CODE_SETTING_RESOLUTION:
            case RESOLVE_CODE_CONNECTION_RESOLUTION:
                // 位置情報の取得設定を再設定

                // 設定チェックの終了
                mLocationSettingUtil.stopLocationSettingChecking();

                // 二重押し解禁
                mLocationCheckButton.setEnabled(true);
                break;
        }
    }

    /**
     * スタートボタンのレイアウトをON状態にする
     */
    private void changeStartButtonOn() {
        mLocationCheckButton.setText(getString(R.string.started_check_location));
        mLocationCheckButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        mLocationCheckButton.setBackgroundResource(R.drawable.selector_main_fragment_start_button_on);
    }

    /**
     * スタートボタンのレイアウトをOFF状態にする
     */
    private void changeStartButtonOff() {
        mLocationCheckButton.setText(getString(R.string.not_start_check_location));
        mLocationCheckButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        mLocationCheckButton.setBackgroundResource(R.drawable.selector_main_fragment_start_button_off);
    }
}
