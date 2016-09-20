package com.example.suzukitakahiro.trainalert.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.suzukitakahiro.trainalert.R;

/**
 * @author suzukitakahiro on 16/09/21.
 */
public class PrefFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pref, container, false);

        return view;
    }
}
