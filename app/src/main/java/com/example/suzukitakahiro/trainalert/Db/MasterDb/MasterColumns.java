package com.example.suzukitakahiro.trainalert.Db.MasterDb;

import android.provider.BaseColumns;

/**
 * マスターDBのカラム
 *
 * @author suzukitakahiro on 16/09/19.
 */
public class MasterColumns implements BaseColumns {

    /** 都道府県テーブル */
    public static final String PREF_TABLE_NAME ="pref";
    public static final String PREF_CD = "pref_cd";
    public static final int PREF_CD_COLUMN = 0;
    public static final String PREF_NAME = "pref_name";
    public static final int PREF_NAME_COLUMN = 1;

    /** 路線テーブル */
    public static final String LINE_TABEL_NAME = "line";
    public static final String LINE_CD = "line_cd";
    public static final int LINE_CD_COLUMN = 0;
    public static final String LINE_NAME = "line_name";
    public static final int LINE_NAME_COLUMN = 1;

    /**
     * 駅テーブル
     * STATION_LINE_CDは路線テーブルの外部キー
     * STATION_PREF_CDは都道府県テーブルの外部キー
     */
    public static final String STATION_TABLE_NAME = "station";
    public static final String STATION_CD = "station_cd";
    public static final int STATION_CD_COLUMN = 0;
    public static final String STATION_G_CD = "station_g_cd";
    public static final int STATION_G_CD_COLUMN = 1;
    public static final String STATION_NAME = "station_name";
    public static final int STATION_NAME_COLUMN = 2;
    public static final int STATION_LINE_CD = 3;
    public static final int STATION_PREF_CD = 4;
    public static final String ST_LATITUDE = "st_latitude";
    public static final int ST_LATITUDE_COLUMN = 5;
    public static final String ST_LONGITUDE = "st_longitude";
    public static final int ST_LONGITUDE_COLUMN = 6;
}