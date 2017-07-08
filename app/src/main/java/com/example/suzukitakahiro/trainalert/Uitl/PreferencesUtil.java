package com.example.suzukitakahiro.trainalert.Uitl;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

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
     * 初回起動を判別するKey
     */
    public static final String PREF_KEY_IS_FIRST_START_UP = "pref_key_is_first_start_up";

    /**
     * 初回起動が完了した場合に保存するValue
     */
    public static final String PREF_VALUE_AFTER_START_UP = "pref_value_after_start_up";

    /**
     * プリファレンスで文字列を保存する
     *
     * @param context コンテキスト
     * @param prefKey 保存するKey
     * @param value   保存するValue
     */
    public static void saveStringPreference(Context context, String prefKey, String value) {
        SharedPreferences sp = context.getSharedPreferences(PREF_KEY_FOR_PREFERENCES_UTIL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(prefKey, value);
        editor.apply();
    }

    /**
     * プリファレンスで保存した文字列を返却する
     *
     * @param context コンテキスト
     * @param prefKey 保存で利用したKey
     * @return 保存したValue
     */
    @Nullable
    public static String getStringPreference(Context context, String prefKey) {
        SharedPreferences sp = context.getSharedPreferences(PREF_KEY_FOR_PREFERENCES_UTIL, Context.MODE_PRIVATE);
        return sp.getString(prefKey, null);
    }
}
