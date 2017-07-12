package com.example.suzukitakahiro.trainalert.Db.MasterDb;

import android.provider.BaseColumns;

/**
 * マスターDBのカラム
 * http://www.ekidata.jp/
 *
 * @author suzukitakahiro on 16/09/19.
 */
public class MasterColumns implements BaseColumns {

    /**
     * マスターDBのrowidは_idとして取得する
     */
    public static final String ROWID = "rowid as _id";

    /**
     * 都道府県テーブル
     * <p>
     * 都道府県コード
     * 都道府県名
     **/
    public static final String PREF_TABLE_NAME = "pref";
    public static final String PREF_CD = "pref_cd";
    public static final int PREF_CD_COLUMN = 1;
    public static final String PREF_NAME = "pref_name";
    public static final int PREF_NAME_COLUMN = 2;

    /**
     * 路線テーブル
     * <p>
     * 路線コード
     * 路線名
     **/
    public static final String LINE_TABEL_NAME = "line";
    public static final String LINE_CD = "line_cd";
    public static final int LINE_CD_COLUMN = 1;
    public static final String LINE_NAME = "line_name";
    public static final int LINE_NAME_COLUMN = 2;

    /**
     * 駅テーブル
     * <p>
     * 駅コード
     * 駅名
     * 駅グループコード（路線が複数ある駅で識別するために利用する）
     * 都道府県コード
     * 路線コード
     * 経度
     * 緯度
     */
    public static final String STATION_TABLE_NAME = "station";
    public static final String STATION_CD = "station_cd";
    public static final int STATION_CD_COLUMN = 1;
    public static final String STATION_NAME = "station_name";
    public static final int STATION_NAME_COLUMN = 2;
    public static final String ST_LATITUDE = "st_latitude";
    public static final int ST_LATITUDE_COLUMN = 3;
    public static final String ST_LONGITUDE = "st_longitude";
    public static final int ST_LONGITUDE_COLUMN = 4;
}