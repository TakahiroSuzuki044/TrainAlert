package com.example.suzukitakahiro.trainalert.Db.MasterDb;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 都道府県テーブル、路線テーブル、液テーブルをマスターDB内に作成する
 *
 * @author suzukitakahiro on 16/09/18.
 */
public class MasterOpenHelper extends SQLiteOpenHelper {

    /**
     * データベース名
     */
    private static final String DATABASE_NAME = "master.db";

    /**
     * データベースバージョン
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * シングルトン対応のインスタンス
     */
    private static MasterOpenHelper sMasterOpenHelper;

    private Context mContext;

    private MasterOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static synchronized MasterOpenHelper getInstance(Context context) {
        if (sMasterOpenHelper == null) {
            sMasterOpenHelper = new MasterOpenHelper(context);
        }
        return sMasterOpenHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // トランザクション開始
        db.beginTransaction();
        try {
            AssetManager as = mContext.getResources().getAssets();
            String assetsDir = "sql";
            String files[] = as.list(assetsDir);

            // assets/sqlフォルダ配下のsqlファイルを読み込みテーブルを作成する
            for (String file : files) {
                BufferedReader br = new BufferedReader(new InputStreamReader(as.open(assetsDir + "/" + file),
                        "UTF-8"));
                String str;
                while ((str = br.readLine()) != null) {
                    if (!TextUtils.isEmpty(str)) {
                        db.execSQL(str);
                    }
                }
                br.close();
            }

            // コミット
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
