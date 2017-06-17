package com.example.suzukitakahiro.trainalert.Db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author suzukitakahiro on 2016/09/01.
 *
 * アラーム位置のOpenHelper
 */
public class LocationOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "LocationOpenHelper";

    /** データベース名 */
    private static final String DATABASE_NAME = "Location.db";

    /** データベースバージョン */
    private static final int DATABASE_VERSION = 2;

    /** 現在地テーブルのクリエイト文 */
    private static final String LOCATION_TABLE_CREATE =
            "CREATE TABLE " + LocationColumns.LOCATION_TABLE_NAME + " (" +
                    LocationColumns._ID + " INTEGER PRIMARY KEY, " +
                    LocationColumns.TITLE + " TEXT NOT NULL, " +
                    LocationColumns.LATITUDE + " DOUBLE, " +
                    LocationColumns.LONGITUDE + " DOUBLE, " +
                    LocationColumns.LINE_NAME + " TEXT );";

    /** シングルトン対応のインスタンス */
    private static LocationOpenHelper sLocationOpenHelper;

    /** コンテキスト */
    private LocationOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 本クラスインスタンスのゲッター
     *
     * @param context   コンテキスト
     * @return 本クラスのインスタンス
     */
    public static synchronized LocationOpenHelper getInstance(Context context) {
        if (sLocationOpenHelper == null) {
            sLocationOpenHelper = new LocationOpenHelper(context);
        }
        return sLocationOpenHelper;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // テーブルを作成
        db.execSQL(LOCATION_TABLE_CREATE);
        Log.d(TAG, "create table");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: ");

        int oldVersionTemp = oldVersion;

        // DBのバージョンアップがあるか
        while (newVersion > oldVersionTemp) {
            ++oldVersionTemp;

            switch (oldVersionTemp) {

                // 登録駅データの路線名を拡張
                case 2:
                    LocationDao.alterTableLineName();
                    break;
            }
        }
    }
}
