package com.example.suzukitakahiro.trainalert.Fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.suzukitakahiro.trainalert.R;

/**
 * ベースフラグメント
 *
 * @author suzukitakahiro on 2016/07/21.
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

    /**
     * Toolbarを設定する
     */
    @Nullable
    protected ActionBar initActionBar() {
        if (getActivity() != null) {
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            if (toolbar != null) {
                ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            }
        }
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }
}
