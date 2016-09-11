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

    /** 緯度 */
    public static final String LATITUDE = "latitude";

    /** 経度 */
    public static final String LONGITUDE = "longitude";
}
