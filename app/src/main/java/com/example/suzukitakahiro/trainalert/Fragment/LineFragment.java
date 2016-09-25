package com.example.suzukitakahiro.trainalert.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.ListView;

import com.example.suzukitakahiro.trainalert.Db.MasterDb.LineDao;
import com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns;
import com.example.suzukitakahiro.trainalert.Db.MasterDb.StationDao;
import com.example.suzukitakahiro.trainalert.R;

import java.util.ArrayList;

/**
 * @author suzukitakahiro on 16/09/23.
 *
 * - 選択された都道府県IDで駅DBの内、IDと被る駅の路線IDを取得
 * - 取得した路線IDの内、重複を取り除く
 * - 重複を取り除いた路線IDで路線テーブル検索をして、路線名/IDを取得、Listで表示
 */
public class LineFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    private Context mContext;
    private View mView;
    private SimpleCursorAdapter mSimpleCursorAdapter;

    private static final int FIND_STATION_BY_PREF_CD = 1;
    private static final int FIND_LINE_BY_LINE_CD = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        mView = inflater.inflate(R.layout.fragment_select, container, false);

        // 選択された都道府県CDを取得
        Bundle args = getArguments();

        setListView();

        // リスト読み込みスタート
        getActivity().getSupportLoaderManager().initLoader(FIND_STATION_BY_PREF_CD, args, this);
        return mView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> cursorLoader = null;
        switch (id) {

            // 都道府県コードで駅検索
            case FIND_STATION_BY_PREF_CD:
                long prefCd = args.getLong(MasterColumns.PREF_CD);
                StationDao stationDao = new StationDao(getActivity());
                cursorLoader = stationDao.findByPrefCd(prefCd);
                break;

            // 路線コードで路線コード/路線名を検索
            case FIND_LINE_BY_LINE_CD:
                String[] lineCds = args.getStringArray(MasterColumns.LINE_CD);
                LineDao lineDao = new LineDao(getActivity());
                cursorLoader = lineDao.findByLineCds(lineCds);
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {

            // 駅テーブルで選択された都道府県コードに一致する路線コードを取得した場合
            case FIND_STATION_BY_PREF_CD:

                int lineCdColumn = 1;
                ArrayList<String> lineCdList = new ArrayList<>();

                // 取得した路線コードの重複を無くしてリスト化する
                while (cursor.moveToNext()) {
                    String lineCd = String.valueOf(cursor.getInt(lineCdColumn));
                    boolean isSingle = checkOverlap(lineCdList, lineCd);
                    if (isSingle) {
                        lineCdList.add(lineCd);
                    }
                }

                // TODO: 16/09/25 lineCdsが空のときの挙動確認
                // 路線コードで路線検索
                String[] lineCds = (String[])lineCdList.toArray(new String[0]);
                Bundle bundle = new Bundle();
                bundle.putStringArray(MasterColumns.LINE_CD, lineCds);
                getActivity().getSupportLoaderManager().initLoader(FIND_LINE_BY_LINE_CD, bundle, this);
                break;

            // 路線テーブルで路線コードに一致する路線情報を取得した場合
            case FIND_LINE_BY_LINE_CD:
                mSimpleCursorAdapter.swapCursor(cursor);
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    /**
     * 一覧を作成
     */
    private void setListView() {
        String[] from = {MasterColumns.LINE_NAME};
        int[] to = {R.id.select_list_item_title};

        mSimpleCursorAdapter = new SimpleCursorAdapter
                (mContext, R.layout.list_item_select, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        ListView listView = (ListView)mView.findViewById(R.id.select_list_view);
        listView.setAdapter(mSimpleCursorAdapter);

        listView.setOnItemClickListener(this);
    }

    /**
     * リスト選択時の処理
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO: 16/09/23 position情報から情報を取得できるか
    }

    /**
     * リストに重複があるかチェックする。
     * 重複がない場合はtrue。
     */
    private boolean checkOverlap(ArrayList lineCdList, String checkLineCd) {
        int exist = lineCdList.indexOf(checkLineCd);
        return exist == -1;
    }
}
