package com.example.suzukitakahiro.trainalert.Db;

import android.provider.BaseColumns;

/**
 * アラーム位置データベースのカラム
 *
 * @author suzukitakahiro on 2016/09/01.
 */
public class LocationColumns implements BaseColumns {

    /**
     * アラーム登録した位置情報テーブル
     *
     * タイトル（駅名）
     * 経度
     * 緯度
     * 路線名
     */
    public static final String LOCATION_TABLE_NAME = "location";
    public static final String TITLE = "title";
    public static final int TITLE_COLUMN = 1;
    public static final String LATITUDE = "latitude";
    public static final int LATITUDE_COLUMN = 2;
    public static final String LONGITUDE = "longitude";
    public static final int LONGITUDE_COLUMN = 3;
    public static final String LINE_NAME = "line_name";
    public static final int LINE_NAME_COLUMN = 4;

    /**
     * ALTER文
     */
    public static final String ALTER_TABLE = "ALTER TABLE ";
    public static final String ADD_COLUMN = " ADD COLUMN ";
    public static final String TEXT = " TEXT";
}
