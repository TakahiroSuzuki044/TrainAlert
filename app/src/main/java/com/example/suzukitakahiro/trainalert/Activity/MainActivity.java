package com.example.suzukitakahiro.trainalert.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.suzukitakahiro.trainalert.Fragment.MainFragment;
import com.example.suzukitakahiro.trainalert.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fragmentの設定
        setFragment(new MainFragment());

        // ツールバーの設定
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * ツールバーのメニューを生成する
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * ツールバーのメニュー選択時の挙動を定義
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // メニューごとの挙動を設定
        switch (item.getItemId()) {

            // 駅指定ボタン押下時
            case R.id.select_station:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
