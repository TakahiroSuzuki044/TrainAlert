package com.example.suzukitakahiro.trainalert.Db;

import android.provider.BaseColumns;

/**
 * @author suzukitakahiro on 2016/09/01.
 *
 * アラーム位置データベースのカラム
 */
public class LocationColumns implements BaseColumns {

    /** テーブル名 */
    public static final String LOCATION_TABLE_NAME = "location";

    /** タイトル */
    public static final String TITLE = "title";
    /** タイトルの列番号 */
    public static final int TITLE_COLUMN = 1;

    /** 緯度 */
    public static final String LATITUDE = "latitude";
    /** 緯度の列番号 */
    public static final int LATITUDE_COLUMN = 2;

    /** 経度 */
    public static final String LONGITUDE = "longitude";
    /** 経度の列番号 */
    public static final int LONGITUDE_COLUMN = 3;
}
