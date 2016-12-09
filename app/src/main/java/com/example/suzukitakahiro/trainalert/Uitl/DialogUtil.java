package com.example.suzukitakahiro.trainalert.Uitl;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by suzukitakahiro on 2016/12/09.
 * ダイアログUtil
 */
public class DialogUtil {

    /**
     * スピナーダイアログを表示させる
     */
    public ProgressDialog showSpinnerDialog(Context context, DialogInterface.OnCancelListener listener) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("現在地を取得中");

        // ダイアログのスタイル
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // キャンセル処理を可能かどうか
        dialog.setCancelable(true);

        // キャンセル時のリスナをセット
        dialog.setOnCancelListener(listener);
        dialog.show();

        return dialog;
    }
}
