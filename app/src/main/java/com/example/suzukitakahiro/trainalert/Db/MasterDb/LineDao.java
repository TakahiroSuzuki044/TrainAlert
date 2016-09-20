package com.example.suzukitakahiro.trainalert.Db.MasterDb;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.LINE_CD;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.LINE_NAME;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.LINE_TABEL_NAME;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterContentProvider.AUTHORITY;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterContentProvider.SCHEME;

/**
 * @author suzukitakahiro on 16/09/20.
 */
public class LineDao {
    public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + "/" + LINE_TABEL_NAME);

    private Context mContext;

    public LineDao(Context context) {
        mContext = context;
    }

    /**
     * 路線テーブルから全レコード情報を取得する
     */
    public Loader<Cursor> findAll() {

        // 取得する情報
        String[] projection = {
                LINE_CD,
                LINE_NAME
        };

        String[] selectionArgs = null;
        String selection = null;
        String sortOrder = null;

        // カーソル初期位置：−１
        return new CursorLoader(mContext, CONTENT_URI, projection, selection, selectionArgs, sortOrder);
    }
}
