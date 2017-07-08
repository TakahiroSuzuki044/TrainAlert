package com.example.suzukitakahiro.trainalert.Activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setFragment(new PrefFragment());
    }
}
