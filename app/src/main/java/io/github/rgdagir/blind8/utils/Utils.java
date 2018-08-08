package io.github.rgdagir.blind8.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.DatePicker;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import io.github.rgdagir.blind8.DatePickerFragment;

public final class Utils extends Fragment {

    public Utils() {
        // never called
    }

    private String dateFromPicker;

    /*
    Function: getDateFromPicker
    Functionality: this function displauys a DatePicker (old style, without the Calendar), gets the Date set by the user and returns it in a String object (MM/DD/YYYY format)
    Warning: to use this function, define the DatePickerFragment, which will contain the picker.
     */
    public String getDateFromPicker(Context context) {
        DatePickerFragment dateFragment = new DatePickerFragment();
        // Set up current date Into dialog
        Calendar cal = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", cal.get(Calendar.YEAR));
        args.putInt("month", cal.get(Calendar.MONTH));
        args.putInt("day", cal.get(Calendar.DAY_OF_MONTH));
        dateFragment.setArguments(args);
        // Set up callback to retrieve date info
        dateFragment.setCallBack(new DatePickerDialog.OnDateSetListener(){
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                dateFromPicker = String.valueOf(monthOfYear) + "/" + String.valueOf(dayOfMonth) + "/" + String.valueOf(year);
            }
        });
        assert getFragmentManager() != null;
        dateFragment.show(getFragmentManager(), "Date Picker");
        return dateFromPicker;
    }

    public static byte[] getbytearray(Bitmap bm){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}
