package com.example.suzukitakahiro.trainalert.Db.MasterDb;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import static android.provider.BaseColumns._ID;

/**
 * マスターDBのコンテンツプロバイダー
 *
 * @author suzukitakahiro on 16/09/19.
 */
public class MasterContentProvider extends ContentProvider {

    /** コンテンツUri */
    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.example.suzukitakahiro.trainalert.MasterContentProvider";

    private MasterOpenHelper mMasterOpenHelper;

    @Override
    public boolean onCreate() {
        mMasterOpenHelper = MasterOpenHelper.getInstance(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String groupBy = null;
        String having = null;

        // 読込専用でデータベースを開く
        SQLiteDatabase db = mMasterOpenHelper.getReadableDatabase();

        // テーブル名を取得
        String uriPath = uri.getPath();
        int index = uriPath.indexOf("/");
        String tableName = uriPath.substring(index + 1);

        // selectionが指定されている場合は成型する
        if (selection != null) {
            getSelection(selection);
        }

        // カーソル初期位置：−１
        Cursor cursor = db.query(tableName, projection, selection, selectionArgs, groupBy, having, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * 指定されたIDで検索できるようにselectionを成型する
     *
     * @param selection 削除するID
     * @return 成型したselection
     */
    private String getSelection(String selection) {
        if (selection != null) {
            return _ID + " = ?";
        } else {
            return null;
        }
    }
}
