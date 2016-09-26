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
     * タイトル
     * 経度
     * 緯度
     */
    public static final String LOCATION_TABLE_NAME = "location";
    public static final String TITLE = "title";
    public static final int TITLE_COLUMN = 1;
    public static final String LATITUDE = "latitude";
    public static final int LATITUDE_COLUMN = 2;
    public static final String LONGITUDE = "longitude";
    public static final int LONGITUDE_COLUMN = 3;
}
