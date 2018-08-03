package io.github.rgdagir.mpr.utils;

import android.app.DatePickerDialog;
import android.os.Bundle;

import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

import io.github.rgdagir.mpr.DatePickerFragment;

public class Utils {

    private Date dateFromPicker;


    private void showDatePicker() {
        DatePickerFragment dateFragment = new DatePickerFragment();

        // Set up current date Into dialog
        Calendar cal = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", cal.get(Calendar.YEAR));
        args.putInt("month", cal.get(Calendar.MONTH));
        args.putInt("day", cal.get(Calendar.DAY_OF_MONTH));
        dateFragment.setArguments(args);

//        // Set up callback to retrieve date info
//        dateFragment.setCallBack(new DatePickerDialog.OnDateSetListener(){
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
//                editBirthDate.setText(String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear+1) + "/"+String.valueOf(year));
//            }
//        });
//        dateFragment.show(getFragmentManager(), "Date Picker");

    }

}
