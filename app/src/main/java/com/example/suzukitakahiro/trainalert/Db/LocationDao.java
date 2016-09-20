package com.example.suzukitakahiro.trainalert.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.util.HashMap;

import static com.example.suzukitakahiro.trainalert.Db.LocationColumns.LATITUDE_COLUMN;
import static com.example.suzukitakahiro.trainalert.Db.LocationColumns.LOCATION_TABLE_NAME;
import static com.example.suzukitakahiro.trainalert.Db.LocationColumns.LONGITUDE_COLUMN;
import static com.example.suzukitakahiro.trainalert.Db.LocationContentProvider.AUTHORITY;
import static com.example.suzukitakahiro.trainalert.Db.LocationContentProvider.SCHEME;

/**
 * @author suzukitakahiro on 2016/09/04.
 */
public class LocationDao {

    private static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + "/" + LOCATION_TABLE_NAME);

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
     * LocationDBと照合し200m圏内の場合はアラートを促す
     */
    public boolean collateLocationDb(Location location) {
        Cursor cursor = findAllReturnCursor();

        Double latitude;
        Double longitude;

        while (cursor.moveToNext()) {

            // DBの列を指定して値を取得
            latitude = cursor.getDouble(LATITUDE_COLUMN);
            longitude = cursor.getDouble(LONGITUDE_COLUMN);

            // {二点間距離[m], 始点から見た方位角, 終点から見た方位角}が格納される
            float[] distance = new float[3];

            Location.distanceBetween(latitude, longitude, location.getLatitude(), location.getLongitude(), distance);

            // 現在地と登録地の距離が200m圏内の場合はアラートを促す
            if (distance[0] < 200) {
                return true;
            }
        }

        // アラートを上げる必要なし
        return false;
    }

    /**
     * LocationDBの全件検索
     *
     * @return 全件情報
     */
    public Cursor findAllReturnCursor() {

        // 取得する情報
        String[] projection = {
                LocationColumns._ID,
                LocationColumns.TITLE,
                LocationColumns.LATITUDE,
                LocationColumns.LONGITUDE
        };

        // WhereのKey部分
        String selection = null;

        // WhereのValue部分
        String[] selectionArgs = null;

        // GroupBy
        String sortOrder = null;

        // カーソル初期位置：−１
        return mContext.getContentResolver().query(CONTENT_URI, projection, selection, selectionArgs, sortOrder);
    }

    /**
     * アラーム位置DBに位置情報を挿入する
     * @param title 挿入するタイトル
     * @param hashMap LocationColumnsのTITLE/LATITUDE/LONGITUDEをキーとして、タイトル/緯度/経度を指定する
     * @return Cursor
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
     */
    public Loader<Cursor> findAll() {

        // 取得する情報
        String[] projection = {
                LocationColumns._ID,
                LocationColumns.TITLE,
                LocationColumns.LATITUDE,
                LocationColumns.LONGITUDE
        };

        String[] selectionArgs = null;
        String selection = null;
        String sortOrder = null;

        // カーソル初期位置：−１
        return new CursorLoader(mContext, CONTENT_URI, projection, selection, selectionArgs, sortOrder);
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
