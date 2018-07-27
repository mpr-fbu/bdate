package io.github.rgdagir.mpr;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
//    private DatePickerFragment.OnDateReceiveCallback mListener;
    private Context context;

//    private DatePickerFragment.OnDateReceiveCallback {
//        public void onDateReceive(int dd, int mm, int yy);
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        this.context = context;
//
//        try {
//            mListener = (DatePickerFragment.onDateReceiveCallback) context;
//        } catch (ClassCastException e){
//            throw  new ClassCastException(context.toString() + "must implement OnDateSetListener");
//        }
//    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Toast.makeText(getActivity(), String.valueOf(dayOfMonth) + " / " + String.valueOf(month) + " / " + String.valueOf(year), Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar cal /* Go Stanford! */ = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
}
