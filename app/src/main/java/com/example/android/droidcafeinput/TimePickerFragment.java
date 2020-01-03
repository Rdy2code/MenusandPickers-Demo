package com.example.android.droidcafeinput;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private final Calendar c = Calendar.getInstance();


    public TimePickerFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String amPm = null;

        //Get the activity the fragment is currently associated with
        OrderActivity orderActivity = (OrderActivity) getActivity();

        if (hourOfDay > 12) {
            hourOfDay -= 12;
            amPm = "PM";
        } else if (hourOfDay == 0) {
            hourOfDay += 12;
            amPm = "AM";
        } else if (hourOfDay == 12) {
            amPm = "PM";
        } else {
            amPm = "AM";
        }

        orderActivity.processTimeResult(hourOfDay, minute, amPm);
    }
}
