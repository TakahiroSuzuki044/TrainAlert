package com.example.suzukitakahiro.trainalert.Activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.suzukitakahiro.trainalert.Fragment.PrefFragment;
import com.example.suzukitakahiro.trainalert.R;

/**
 * 都道府県画面、路線画面、駅画面を表示するアクティビティ
 * 表示ロジックはフラグメントに移譲している。
 *
 * @author suzukitakahiro on 16/09/21.
 */
public class SearchStationActivity extends BaseActivity {

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
