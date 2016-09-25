package com.example.suzukitakahiro.trainalert.Db.MasterDb;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.LINE_CD;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.PREF_CD;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.ROWID;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.STATION_CD;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.STATION_NAME;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.STATION_TABLE_NAME;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.ST_LATITUDE;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.ST_LONGITUDE;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterContentProvider.AUTHORITY;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterContentProvider.SCHEME;

/**
 * @author suzukitakahiro on 16/09/20.
 */
public class StationDao {

    private static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + "/" + STATION_TABLE_NAME);

    private Context mContext;

    public StationDao(Context context) {
        mContext = context;
    }

    /**
     * 都道府県コードに一致する沿線コードを取得する
     */
    public Loader<Cursor> findByPrefCd(long prefCd) {

        // 取得する情報
        String[] projection = {
                ROWID,
                LINE_CD
        };

        String[] selectionArgs = {String.valueOf(prefCd)};
        String selection = PREF_CD;
        String sortOrder = null;

        // カーソル初期位置：−１
        return new CursorLoader(mContext, CONTENT_URI, projection, selection, selectionArgs, sortOrder);
    }


    /**
     * 路線CDで駅テーブルからレコード情報を取得する
     * 一覧で表示するために利用するため、CDとNAMEしか取得しない
     */
    public Loader<Cursor> findByLineCd(int lineCd) {

        // 取得する情報
        String[] projection = {
                ROWID,
                STATION_CD,
                STATION_NAME
        };

        String[] selectionArgs = {String.valueOf(lineCd)};
        String selection = LINE_CD;
        String sortOrder = null;

        // カーソル初期位置：−１
        return new CursorLoader(mContext, CONTENT_URI, projection, selection, selectionArgs, sortOrder);
    }

    /**
     * 駅CDで一件レコードを取得する
     * 緯度経度を登録するために利用する
     */
    public Loader<Cursor> findByStationCd(int stationCd) {

        // 取得する情報
        String[] projection = {
                ROWID,
                STATION_CD,
                STATION_NAME,
                ST_LATITUDE,
                ST_LONGITUDE
        };

        String[] selectionArgs = {String.valueOf(stationCd)};
        String selection = STATION_CD;
        String sortOrder = null;

        // カーソル初期位置：−１
        return new CursorLoader(mContext, CONTENT_URI, projection, selection, selectionArgs, sortOrder);
    }
}
