package com.example.suzukitakahiro.trainalert.Db.MasterDb;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * マスターDBのコンテンツプロバイダー
 *
 * @author suzukitakahiro on 16/09/19.
 */
public class MasterContentProvider extends ContentProvider {

    /**
     * コンテンツUri
     */
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

        // selectionArgsの要素数分の「selection = ?」を生成するために長さを取得
        int selectionArgsLength = 0;
        if (selectionArgs != null) {
            selectionArgsLength = selectionArgs.length;
        }

        // 「selection = ?」に成型する
        if (selection != null) {
            selection = getSelection(selection, selectionArgsLength);
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
     * length分のselection = ? を文字列として生成する
     * length > 2 の場合はselection = ? OR selection = ? といった具合で連結
     *
     * @return 成型したselection
     */
    private String getSelection(String selection, int length) {
        if (selection != null) {
            String selections = selection + " = ?";
            for (int i = 1; i < length; ++i) {
                selections = selections + " OR " + selection + " = ?";
            }
            return selections;
        } else {
            return null;
        }
    }
}
