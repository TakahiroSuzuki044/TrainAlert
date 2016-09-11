package com.example.suzukitakahiro.trainalert.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.util.HashMap;

/**
 * @author suzukitakahiro on 2016/09/04.
 */
public class LocationDao {

    public static final Uri CONTENT_URI = Uri.parse
            (LocationContentProvider.SCHEME + LocationContentProvider.AUTHORITY + "/" + LocationColumns.LOCATION_TABLE_NAME);

    private Context mContext;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     */
    public LocationDao(Context context) {
        mContext = context;
    }

    /**
     * アラーム位置DBに位置情報を挿入する
     * @param title 挿入するタイトル
     * @param hashMap LocationColumnsのTITLE/LATITUDE/LONGITUDEをキーとして、タイトル/緯度/経度を指定する
     * @return
     */
    public boolean insert(String title, HashMap hashMap) {
        ContentValues values = new ContentValues();

        // レコードのタイトル、緯度、経度を挿入値に設定
        values.put(LocationColumns.TITLE, title);
        values.put(LocationColumns.LATITUDE, (Double) hashMap.get(LocationColumns.LATITUDE));
        values.put(LocationColumns.LONGITUDE, (Double) hashMap.get(LocationColumns.LONGITUDE));

        Uri uri = mContext.getContentResolver().insert(CONTENT_URI, values);

        // Uriが入っている場合はDB挿入が出来たと判断する
        return uri != null;
    }

    /**
     * アラーム位置DBから全レコード情報を取得する
     * @return
     */
    public Loader<Cursor> findAll() {

        // 取得する情報
        String[] columns = {
                LocationColumns._ID,
                LocationColumns.TITLE,
                LocationColumns.LATITUDE,
                LocationColumns.LONGITUDE
        };

        String[] selectionArgs = null;
        String selection = null;
        String orderBy = null;

        // カーソル初期位置：−１
        Loader<Cursor> cursorLoader = new CursorLoader(mContext, CONTENT_URI, columns, selection, selectionArgs, orderBy);
        return cursorLoader;
    }

    /**
     * 指定したIDのレコードを削除する
     *
     * @param id 削除するID
     * @return 削除に成功した場合True。失敗はFalse。
     */
    public boolean delete(long id) {

        // 削除するidを指定
        String selectionArgs[] = {Long.toString(id)};

        // コンテンツリゾルバー経由で対象レコードを削除
        int deleteCount = mContext.getContentResolver().delete(CONTENT_URI, LocationColumns._ID, selectionArgs);

        // deleteCountが1レコード以上削除できていればTrue、その他はFalse
        return deleteCount != -1;
    }
}
