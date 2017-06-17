package com.example.suzukitakahiro.trainalert.Fragment;

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
import android.widget.Toast;

import com.example.suzukitakahiro.trainalert.Db.Dto.RegisterStationDto;
import com.example.suzukitakahiro.trainalert.Db.LocationDao;
import com.example.suzukitakahiro.trainalert.Db.MasterDb.StationDao;
import com.example.suzukitakahiro.trainalert.R;

import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.STATION_CD;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.STATION_CD_COLUMN;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.STATION_NAME;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.STATION_NAME_COLUMN;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.ST_LATITUDE_COLUMN;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.ST_LONGITUDE_COLUMN;

/**
 * 駅指定画面フラグメント
 *
 * @author suzukitakahiro on 16/09/23.
 */
public class StationFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener {

    /** 登録駅情報DtoのArgsKey */
    public static final String ARGS_KEY_REGISTER_STATION_DTO = "args_key_register_station_dto";

    /** 駅テーブルで路線コードから駅名を取得 */
    private static final int FIND_STATION_BY_LINE_ID = 3;
    /** 駅テーブルで駅コードから駅情報を取得 */
    private static final int FIND_STATION_BY_STATION_ID = 4;

    private Context mContext;
    private View mView;
    private SimpleCursorAdapter mSimpleCursorAdapter;

    /** 選択された駅の情報を保持する */
    private RegisterStationDto mRegStationDto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getContext();
        mView = inflater.inflate(R.layout.fragment_select, container, false);

        // 選択された都道府県CDを取得
        Bundle args = getArguments();
        mRegStationDto = getArguments().getParcelable(ARGS_KEY_REGISTER_STATION_DTO);

        setListView();

        // 駅リスト読み込みスタート
        getActivity().getSupportLoaderManager().initLoader(FIND_STATION_BY_LINE_ID, args, this);
        return mView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> cursorLoader = null;
        StationDao stationDao = new StationDao(mContext);
        switch (id) {

            // 路線コードから該当する駅名を取得
            case FIND_STATION_BY_LINE_ID:
                String lineCd;

                // lineCdが取得出来る場合のみ検索する
                if (mRegStationDto != null) {
                    lineCd = mRegStationDto.line_cd;
                    if (lineCd != null) {
                        cursorLoader = stationDao.findByLineCd(lineCd);
                    }
                }
                break;

            // 駅コードから駅情報（緯度/経度）を取得
            case FIND_STATION_BY_STATION_ID:
                int stationCd = args.getInt(STATION_CD);
                cursorLoader = stationDao.findByStationCd(stationCd);
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {

            // 駅一覧を表示する
            case FIND_STATION_BY_LINE_ID:
                mSimpleCursorAdapter.swapCursor(cursor);
                break;

            // 緯度/経度を取得して登録する
            case FIND_STATION_BY_STATION_ID:
                boolean isSuccess = insertStationLocation(cursor);
                if (isSuccess) {
                    Toast.makeText(mContext, cursor.getString(STATION_NAME_COLUMN) + "駅を登録しました", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "登録に失敗しました", Toast.LENGTH_SHORT).show();
                }

                // メイン画面に戻る
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    /**
     * 一覧を作成
     */
    private void setListView() {
        String[] from = {STATION_NAME};
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

        // 選択された駅コードを取得
        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        int stationCd = cursor.getInt(STATION_CD_COLUMN);

        // 駅詳細情報検索
        Bundle bundle = new Bundle();
        bundle.putInt(STATION_CD, stationCd);
        getActivity().getSupportLoaderManager().initLoader(FIND_STATION_BY_STATION_ID, bundle, this);
    }

    /**
     * 駅情報をアラーム登録する
     *
     * @param cursor 登録する駅情報のカーソル
     * @return 登録出来た場合true
     */
    private boolean insertStationLocation(Cursor cursor) {
        cursor.moveToNext();
        mRegStationDto.station_cd = String.valueOf(cursor.getInt(STATION_CD_COLUMN));
        mRegStationDto.station_name = cursor.getString(STATION_NAME_COLUMN);
        mRegStationDto.st_latitude = cursor.getDouble(ST_LATITUDE_COLUMN);
        mRegStationDto.st_longitude = cursor.getDouble(ST_LONGITUDE_COLUMN);

        LocationDao dao = new LocationDao(getActivity());
        return dao.insert(mRegStationDto);
    }
}
