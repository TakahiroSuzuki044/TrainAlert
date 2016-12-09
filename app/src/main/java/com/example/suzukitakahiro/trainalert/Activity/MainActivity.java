package com.example.suzukitakahiro.trainalert.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.suzukitakahiro.trainalert.Db.LocationColumns;
import com.example.suzukitakahiro.trainalert.Db.LocationDao;
import com.example.suzukitakahiro.trainalert.Dialog.DeleteDialog;
import com.example.suzukitakahiro.trainalert.Dialog.TimeSelectDialog;
import com.example.suzukitakahiro.trainalert.R;
import com.example.suzukitakahiro.trainalert.Service.LocationService;
import com.example.suzukitakahiro.trainalert.Uitl.AlarmUtil;
import com.example.suzukitakahiro.trainalert.Uitl.DialogUtil;
import com.example.suzukitakahiro.trainalert.Uitl.LocationUtil;

public class MainActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        ListView.OnItemLongClickListener, DeleteDialog.DialogCallback, View.OnClickListener {

    private static final int FIND_ALL = 0;

    /**
     * 登録地と現在地の毎時照合がスタートしているか
     */
    private static boolean sIsStartedLocationSearch = false;

    private SimpleCursorAdapter mSimpleCursorAdapter;

    private LocationUtil mLocationUtil;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.start_button);
        button.setOnClickListener(this);

        // ツールバーの設定
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // リストをセット
        setListView();

        // 全件検索
        getSupportLoaderManager().initLoader(FIND_ALL, null, this);
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

                // 即時現在地を取得する
                long minTime = 0;
                float minDistance = 0;
                mLocationUtil.acquireLocation(minTime, minDistance, mLocationCallback);

                // 現在地取得中はダイアログを表示する

                DialogUtil dialogUtil = new DialogUtil();
                mProgressDialog = dialogUtil.showSpinnerDialog(this, mListener);

                Toast.makeText(this, "毎時チェックを開始しました", Toast.LENGTH_SHORT).show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 登録したアラーム情報の一覧を作成
     */
    private void setListView() {
        String[] from = {LocationColumns.TITLE};
        int[] to = {R.id.main_list_item_title};

        mSimpleCursorAdapter = new SimpleCursorAdapter
                (this, R.layout.list_item_main, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        ListView listView = (ListView) findViewById(R.id.location_list_view);
        listView.setAdapter(mSimpleCursorAdapter);

        listView.setOnItemLongClickListener(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> cursorLoader = null;
        LocationDao locationDao = new LocationDao(this);
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

    /**
     * リストのロングタップで削除ダイアログを表示する
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        DeleteDialog deleteDialog = DeleteDialog.getInstance(id);
        deleteDialog.show(getSupportFragmentManager(), "AskDeleteDialog");

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
        LocationDao memoDao = new LocationDao(this);

        // idを指定して削除
        boolean isSuccess = memoDao.delete(id);
        if (isSuccess) {
            Toast.makeText(this, "削除が完了しました", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "削除に失敗しました", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 位置チェックをスタートする
     */
    @Override
    public void onClick(View v) {
        Intent locationService = new Intent(this, LocationService.class);
        startService(locationService);
        Toast.makeText(this, "毎時チェックを開始しました", Toast.LENGTH_SHORT).show();
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
            mLocationUtil.stopUpdate();
        }

        @Override
        public void Error() {

        }
    };

    /**
     * 位置情報取得時に表示するスピナーダイアログのキャンセルイベントリスナ
     */
    private DialogInterface.OnCancelListener mListener = new DialogInterface.OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {

            // 現在地取得を停止する
            mLocationUtil.stopUpdate();
        }
    };
}
