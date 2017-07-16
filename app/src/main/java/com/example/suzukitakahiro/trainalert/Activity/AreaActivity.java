package com.example.suzukitakahiro.trainalert.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;

import com.example.suzukitakahiro.trainalert.Db.Dto.AreaDto;
import com.example.suzukitakahiro.trainalert.Fragment.PrefFragment;
import com.example.suzukitakahiro.trainalert.R;
import com.example.suzukitakahiro.trainalert.Uitl.PreferencesUtil;

/**
 * 都道府県選択画面
 * <p>
 * Created by suzukitakahiro on 2017/07/08.
 */

public class AreaActivity extends BaseActivity {

    AreaDto mAreaDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_station);

        // ツールバーの設定
        ActionBar actionBar = initActionBar();

        mAreaDto = (AreaDto) PreferencesUtil.getObjectPreference(
                getApplicationContext(), PreferencesUtil.PREF_KEY_GET_PREFECTURES_CODE, new AreaDto());

        if (actionBar != null) {

            // 既に都道府県情報が存在する場合は初回起動ではないので、戻るボタンを表示する
            if (mAreaDto != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            actionBar.setTitle(R.string.toolbar_select_preferences);
        }

        setFragment(new PrefFragment());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // 初回起動でバックキー
        if (keyCode == KeyEvent.KEYCODE_BACK && mAreaDto == null) {
            setResult(Activity.RESULT_CANCELED);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
