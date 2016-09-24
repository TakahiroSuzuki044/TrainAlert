package com.example.suzukitakahiro.trainalert.Db.MasterDb;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterContentProvider.*;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.*;

/**
 * 都道府県Dao
 *
 * @author suzukitakahiro on 16/09/19.
 */
public class PrefDao {

    private static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + "/" + PREF_TABLE_NAME);

    private Context mContext;

    public PrefDao(Context context) {
        mContext = context;
    }

    /**
     * 都道府県テーブルから全レコード情報を取得する
     */
    public Loader<Cursor> findAll() {

        // 取得する情報
        String[] projection = {
                "rowid as _id",
                PREF_CD,
                PREF_NAME
        };

        String[] selectionArgs = null;
        String selection = null;
        String sortOrder = null;

        // カーソル初期位置：−１
        return new CursorLoader(mContext, CONTENT_URI, projection, selection, selectionArgs, sortOrder);
    }

}
