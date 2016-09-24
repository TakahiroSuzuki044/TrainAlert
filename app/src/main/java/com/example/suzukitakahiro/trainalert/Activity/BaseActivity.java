package com.example.suzukitakahiro.trainalert.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.suzukitakahiro.trainalert.R;

/**
 * ベースActivity
 *
 * @author suzukitakahiro on 2016/07/21.
 */
public class BaseActivity extends AppCompatActivity {

    /**
     * Fragmentを置き換える
     * @param fragment 置き換えるFragment
     */
    protected void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, fragment);
        transaction.commit();
    }


}
