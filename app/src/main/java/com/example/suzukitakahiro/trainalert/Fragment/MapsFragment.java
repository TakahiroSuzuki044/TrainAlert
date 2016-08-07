package com.example.suzukitakahiro.trainalert.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.suzukitakahiro.trainalert.R;
import com.google.android.gms.maps.MapFragment;

/**
 * @author suzukitakahiro on 2016/07/30.
 */
public class MapsFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        MapFragment mapFragment = MapFragment.newInstance();

        return view;
    }
}
