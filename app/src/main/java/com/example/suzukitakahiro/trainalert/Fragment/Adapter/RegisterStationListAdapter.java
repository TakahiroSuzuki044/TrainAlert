package com.example.suzukitakahiro.trainalert.Fragment.Adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.suzukitakahiro.trainalert.Db.Dto.RegisterStationDto;
import com.example.suzukitakahiro.trainalert.R;

/**
 * 登録駅を表示するAdapter
 *
 * @author  suzukitakahiro on 2017/06/18.
 */

public class RegisterStationListAdapter extends ArrayAdapter<RegisterStationDto> {

    private LayoutInflater mLayoutInflater;

    public RegisterStationListAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = null;
        ViewHolder viewHolder = null;

        if (convertView == null) {
            view = mLayoutInflater.inflate(R.layout.list_item_main, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.stationName = (TextView) view.findViewById(R.id.main_list_item_station_name);
            viewHolder.lineName = (TextView) view.findViewById(R.id.main_list_item_line_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        RegisterStationDto dto = getItem(position);
        if (dto != null) {
            viewHolder.stationName.setText(dto.station_name);

            // 路線名はDBバージョン１では保存していないので、空の場合がある
            if (!dto.line_name.isEmpty()) {
                viewHolder.lineName.setText(dto.line_name);
                viewHolder.lineName.setVisibility(View.VISIBLE);
            }
        }

        return view;
    }

    private class ViewHolder {
        TextView stationName;
        TextView lineName;
    }
}
