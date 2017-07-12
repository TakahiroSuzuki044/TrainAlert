package com.example.suzukitakahiro.trainalert.Activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.example.suzukitakahiro.trainalert.Fragment.PrefFragment;
import com.example.suzukitakahiro.trainalert.R;

/**
 * 都道府県選択画面
 * <p>
 * Created by suzukitakahiro on 2017/07/08.
 */

public class AreaActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_station);

        // ツールバーの設定
        ActionBar actionBar = initActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.toolbar_select_preferences);
        }

        setFragment(new PrefFragment());
    }
}
