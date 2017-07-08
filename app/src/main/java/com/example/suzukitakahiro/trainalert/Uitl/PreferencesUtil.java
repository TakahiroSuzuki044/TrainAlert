package com.example.suzukitakahiro.trainalert.Uitl;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferencesを管理するUtilクラス
 * <p>
 * Created by suzukitakahiro on 2017/07/07.
 */

public class PreferencesUtil {

    /**
     * 本クラスで保存する際のプリファレンスファイル名
     */
    private static final String PREF_KEY_FOR_PREFERENCES_UTIL = "pref_key_for_preferences_util";

    /**
     * 都道府県の保存を管理するKey
     */
    public static final String PREF_KEY_GET_PREFECTURES_CODE = "pref_key_get_prefectures_code";

    /**
     * 存在しない場合の返却値
     */
    public static final int PREF_VALUE_NOT_EXIST = -1;

    /**
     * データが保存されているか
     *
     * @param value 保存の確認をしたいデータ
     * @return 存在している場合、True.
     */
    public static boolean isExistForInt(int value) {
        return value != PREF_VALUE_NOT_EXIST;
    }

    /**
     * プリファレンスで文字列を保存する
     *
     * @param context コンテキスト
     * @param prefKey 保存するKey
     * @param value   保存するValue
     */
    public static void saveIntPreference(Context context, String prefKey, int value) {
        SharedPreferences sp = context.getSharedPreferences(PREF_KEY_FOR_PREFERENCES_UTIL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(prefKey, value);
        editor.apply();
    }

    /**
     * プリファレンスで保存した文字列を返却する
     *
     * @param context コンテキスト
     * @param prefKey 保存で利用したKey
     * @return 保存したValue、取得できない場合は-1.
     */
    public static int getIntPreference(Context context, String prefKey) {
        SharedPreferences sp = context.getSharedPreferences(PREF_KEY_FOR_PREFERENCES_UTIL, Context.MODE_PRIVATE);
        return sp.getInt(prefKey, PREF_VALUE_NOT_EXIST);
    }
}
