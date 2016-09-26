package com.example.suzukitakahiro.trainalert.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.suzukitakahiro.trainalert.Db.LocationColumns;

/**
 * アラーム登録した位置情報テーブルの削除確認ダイアログ
 *
 * @author suzukitakahiro on 2016/09/11.
 */
public class DeleteDialog extends DialogFragment {
    /** コールバック用リスナ */
    DialogCallback mListener;

    /**
     * コールバック用のインターフェース
     */
    public interface DialogCallback {
        public void onCallback(long id);
    }

    /**
     * 削除するIDを取得し、インスタンスを返す。
     *
     * @param id 削除するid
     * @return AskDeleteDialog
     */
    public static DeleteDialog getInstance(long id) {
        DeleteDialog deleteDialog = new DeleteDialog();
        Bundle bundle = new Bundle();
        bundle.putLong(LocationColumns._ID, id);
        deleteDialog.setArguments(bundle);
        return deleteDialog;
    }

    /**
     * クラス生成時にリスナを生成する
     *
     * @param activity リスナ
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DialogCallback) {
            mListener = (DialogCallback) activity;
        } else {
            mListener = null;
        }
    }

    /**
     * 削除確認ダイアログを表示する
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final long id = getArguments().getLong(LocationColumns._ID);

        builder.setTitle("削除してよろしいですか？")
                .setPositiveButton("削除する", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // リスナとidが存在する場合
                        if (mListener != null && id != -1) {
                            mListener.onCallback(id);
                        }
                    }
                });
        return builder.create();
    }
}
