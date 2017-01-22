package com.example.suzukitakahiro.trainalert.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.suzukitakahiro.trainalert.Db.LocationColumns;
import com.example.suzukitakahiro.trainalert.Db.LocationDao;
import com.example.suzukitakahiro.trainalert.Dialog.DeleteDialog;
import com.example.suzukitakahiro.trainalert.R;
import com.example.suzukitakahiro.trainalert.Service.LocationService;
import com.example.suzukitakahiro.trainalert.Uitl.LocationUtil;
import com.example.suzukitakahiro.trainalert.Uitl.ServiceUtil;

import static com.example.suzukitakahiro.trainalert.Uitl.ConstantsUtil.PREF_KEY_IS_REQUESTED_STOP;
import static com.example.suzukitakahiro.trainalert.Uitl.ConstantsUtil.PREF_KEY_IS_REQUESTED_STOP_LOCATION_CHECK;

/**
 * @author suzukitakahiro on 2016/07/21.
 *         <p/>
 *         Top画面のリスト部分
 */
public class MainFragment extends BaseFragment implements ListView.OnItemLongClickListener,
        View.OnClickListener, DeleteDialog.DialogCallback {

    private static final String TAG = "MainFragment";

    private SimpleCursorAdapter mSimpleCursorAdapter;

    private View mView;

    private Button mLocationCheckButton;

    /**
     * DB登録駅の全件検索
     */
    private static final int FIND_ALL = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        mLocationCheckButton = (Button) mView.findViewById(R.id.start_button);
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
            mLocationCheckButton.setText(getString(R.string.started_check_location));
        } else {
            mLocationCheckButton.setText(getString(R.string.not_start_check_location));
        }
    }

    /**
     * 登録したアラーム情報の一覧を作成
     */
    private void setListView() {
        String[] from = {LocationColumns.TITLE};
        int[] to = {R.id.main_list_item_title};

        mSimpleCursorAdapter = new SimpleCursorAdapter
                (getActivity(), R.layout.list_item_main, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        ListView listView = (ListView) mView.findViewById(R.id.location_list_view);
        if (listView != null) {
            listView.setAdapter(mSimpleCursorAdapter);
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
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            // Cursorのデータを新しく置き換え
            mSimpleCursorAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mSimpleCursorAdapter.swapCursor(null);
        }
    };

    /**
     * リストのロングタップで削除ダイアログを表示する
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        DeleteDialog deleteDialog = DeleteDialog.getInstance(id, this);
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

    /**
     * 位置チェックをスタートする
     */
    @Override
    public void onClick(View v) {
        LocationUtil util = LocationUtil.getInstance(getContext());

        // 位置情報が取得可能か
        boolean isEnableGps = util.checkEnableGps();

        // 位置情報取得不可の場合は改善ダイアログを表示する
        if (!isEnableGps) {
            util.showImproveLocationDialog(getActivity());
            return;
        }

        boolean isStartedCheckLocation =
                ServiceUtil.checkStartedService(getActivity(), LocationService.class.getName());
        Intent intent = new Intent(getActivity(), LocationService.class);

        // サービス未実行時は実行に、実行時は停止する
        if (isStartedCheckLocation) {
            isRequestStopLocationCheck();
            getActivity().stopService(intent);
            mLocationCheckButton.setText(getString(R.string.not_start_check_location));
            Toast.makeText(getActivity(), "チェックを終了しました", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        } else {
            isRequestStartLocationCheck();
            getActivity().startService(intent);
            mLocationCheckButton.setText(getString(R.string.started_check_location));
            Toast.makeText(getActivity(), "チェックを開始しました", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 現在位置の取得停止をリクエストする
     */
    private void isRequestStopLocationCheck() {

        // 保存
        SharedPreferences sp = getContext().getSharedPreferences(PREF_KEY_IS_REQUESTED_STOP_LOCATION_CHECK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(PREF_KEY_IS_REQUESTED_STOP, true);
        editor.apply();
    }

    private void isRequestStartLocationCheck() {

        // 保存
        SharedPreferences sp = getContext().getSharedPreferences(PREF_KEY_IS_REQUESTED_STOP_LOCATION_CHECK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(PREF_KEY_IS_REQUESTED_STOP, false);
        editor.apply();
    }
}
