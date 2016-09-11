package com.example.suzukitakahiro.trainalert.Db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * @author suzukitakahiro on 2016/09/04.
 *
 * アラーム位置テーブルのコンテンツプロバイダー
 */
public class LocationContentProvider extends ContentProvider {

    /** コンテンツUri */
    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.example.suzukitakahiro.trainalert.LocationContentProvider";

    /** テーブルのMIMEタイプの設定 */
    public static final String CONTENT_TYPE =
            "vnd.android.cursor.dir" + "/" + AUTHORITY + "." + LocationColumns.LOCATION_TABLE_NAME;
    public static final String CONTENT_ITEM_TYPE =
            "vnd.android.cursor.item/*" + "/" + AUTHORITY + "." + LocationColumns.LOCATION_TABLE_NAME;

    /** UriMatcher用の識別コード */
    private static final int ALL_CODE = 0;
    private static final int BY_ID_CODE = 1;

    /** UriMatcher */
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, LocationColumns.LOCATION_TABLE_NAME, ALL_CODE);
        sUriMatcher.addURI(AUTHORITY, LocationColumns.LOCATION_TABLE_NAME + "/#", BY_ID_CODE);
    }

    /** 現在地OpenHelper */
    private LocationOpenHelper mLocationOpenHelper;

    @Override
    public boolean onCreate() {
        mLocationOpenHelper = LocationOpenHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String groupBy = null;
        String having = null;

        // 読込専用でデータベースを開く
        SQLiteDatabase db = mLocationOpenHelper.getReadableDatabase();
        // カーソル初期位置：−１
        Cursor cursor = db.query(LocationColumns.LOCATION_TABLE_NAME, projection, selection, selectionArgs, groupBy, having, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        int matchCode = sUriMatcher.match(uri);

        switch (matchCode) {
            case ALL_CODE:
                return CONTENT_TYPE;
            case BY_ID_CODE:
                return CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // データベースを開く
        SQLiteDatabase db = mLocationOpenHelper.getWritableDatabase();

        // 戻り値はRowID（_ID）
        // エラーの場合は-1になる
        long rowId = db.insert(LocationColumns.LOCATION_TABLE_NAME, null, values);

        // インサートされた行のUriを生成
        Uri insertedUri = ContentUris.withAppendedId(uri, rowId);
        getContext().getContentResolver().notifyChange(insertedUri, null);
        return insertedUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
