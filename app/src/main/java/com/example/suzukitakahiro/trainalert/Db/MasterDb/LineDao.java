package com.example.suzukitakahiro.trainalert.Db.MasterDb;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.LINE_CD;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.LINE_NAME;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.LINE_TABEL_NAME;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.ROWID;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterContentProvider.AUTHORITY;
import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterContentProvider.SCHEME;

/**
 * 路線Dao
 *
 * @author suzukitakahiro on 16/09/20.
 */
public class LineDao {

    private static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + "/" + LINE_TABEL_NAME);

    private Context mContext;

    public LineDao(Context context) {
        mContext = context;
    }

    /**
     * 路線コード配列に一致する路線コード/路線名を取得する
     *
     * @param lineCds 検索条件
     */
    public Loader<Cursor> findByLineCds(String[] lineCds) {

        // 取得する情報
        String[] projection = {
                ROWID,
                LINE_CD,
                LINE_NAME
        };

        String[] selectionArgs = lineCds;
        String selection = LINE_CD;
        String sortOrder = null;

        // カーソル初期位置：−１
        return new CursorLoader(mContext, CONTENT_URI, projection, selection, selectionArgs, sortOrder);
    }
}
