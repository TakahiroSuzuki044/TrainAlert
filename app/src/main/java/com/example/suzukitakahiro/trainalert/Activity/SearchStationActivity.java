package com.example.suzukitakahiro.trainalert.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.example.suzukitakahiro.trainalert.Fragment.LineFragment;
import com.example.suzukitakahiro.trainalert.Fragment.PrefFragment;
import com.example.suzukitakahiro.trainalert.R;

import static com.example.suzukitakahiro.trainalert.Db.MasterDb.MasterColumns.PREF_CD;

/**
 * 路線画面、駅画面を表示するアクティビティ
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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        int prefCd = getIntent().getIntExtra(
                PrefFragment.INTENT_KEY_PREFECTURES, -1);
        if (prefCd != -1) {

            // 路線画面へ遷移
            Bundle bundle = new Bundle();
            bundle.putInt(PREF_CD, prefCd);
            Fragment fragment = new LineFragment();
            fragment.setArguments(bundle);
            setFragmentAddBackStack(fragment, LineFragment.TAG);
        } else {
            finish();
        }
    }

    /**
     * 現在のFragment をバックスタックに保存しつつ、引数のFragment をreplace する
     *
     * @param fragment 遷移先のFragment
     * @param tag      フラグメントのタグ
     */
    public void setFragmentAddBackStack(Fragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if (!TextUtils.isEmpty(tag)) {
            ft.replace(R.id.fragment_container, fragment, tag);
        } else {
            ft.replace(R.id.fragment_container, fragment);
        }
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            FragmentManager manager = getSupportFragmentManager();

            if (manager.getBackStackEntryCount() <= 1) {
                // 現在のFragment数が1でバックキー押下の場合は、Fragment自体が消えるのでFinish.

                finish();
                return true;
            } else {
                // スタックが複数存在している場合は一つポップする

                manager.popBackStack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                // バックキー処置と同じ挙動にする
                onKeyDown(KeyEvent.KEYCODE_BACK, null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
