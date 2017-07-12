package com.example.suzukitakahiro.trainalert.Dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import com.example.suzukitakahiro.trainalert.Uitl.AlarmUtil;

import java.util.Calendar;

/**
 * @author suzukitakahiro on 2016/08/21.
 *         <p>
 *         時間を指定してアラートを登録するダイアログ
 */
public class TimeSelectDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // 現在時間でダイアログを表示する
        TimePickerDialog timePickerDialog =
                new TimePickerDialog(getContext(), this, hour, minute, true);

        return timePickerDialog;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        AlarmUtil alarmUtil = new AlarmUtil();

        // 入力時間でアラートを設定する
        alarmUtil.setAlarmInTime(getContext(), hourOfDay, minute);
    }
}
