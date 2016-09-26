package com.example.suzukitakahiro.trainalert.Fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.example.suzukitakahiro.trainalert.R;

/**
 * ベースフラグメント
 *
 * @author  suzukitakahiro on 2016/07/21.
 */
public class BaseFragment extends Fragment {

    /**
     * フラグメントを設定
     */
    protected void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
