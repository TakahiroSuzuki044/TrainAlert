package com.example.suzukitakahiro.trainalert.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
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

/**
 * @author suzukitakahiro on 2016/07/21.
 *         <p/>
 *         Top画面のリスト部分
 */
public class MainFragment extends BaseFragment implements ListView.OnItemLongClickListener,
        View.OnClickListener, DeleteDialog.DialogCallback {

    private SimpleCursorAdapter mSimpleCursorAdapter;

    private View mView;

    /**
     * DB登録駅の全件検索
     */
    private static final int FIND_ALL = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main, container, false);

        Button button = (Button) mView.findViewById(R.id.start_button);
        if (button != null) {
            button.setOnClickListener(this);
        }

        // リストをセット
        setListView();

        // 全件検索
        getActivity().getSupportLoaderManager().initLoader(FIND_ALL, null, mLoaderCallbacks);
        return mView;
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
        Intent locationService = new Intent(getContext(), LocationService.class);
        getActivity().startService(locationService);
        Toast.makeText(getActivity(), "毎時チェックを開始しました", Toast.LENGTH_SHORT).show();
    }
}
