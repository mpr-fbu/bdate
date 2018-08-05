package io.github.rgdagir.blind8.sign_up;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.github.rgdagir.blind8.DatePickerFragment;
import io.github.rgdagir.blind8.R;

public class BasicInfoFragment extends Fragment {

    private BasicInfoFragment.OnFragmentInteractionListener mListener;
    private List<String> fakeNames = new ArrayList<>(Arrays.asList("Anonymous Anon", "Mysterious Stranger", "?????"));

    private TextView title;
    private TextView explanation;
    private TextView tvGender;
    private TextView tvInterestedIn;
    private TextView birthday;
    private TextView name;
    private TextView alias;
    private TextView aliasNote;
    private EditText placeholderBirthday;
    private EditText etName;
    private EditText etAlias;
    private RadioGroup genderOptions;
    private RadioGroup preferenceOptions;
    private Button refresh;
    private Button btnContinue;
    private String gender;
    private String interestedIn;
    private long age;
    private String dateFromPicker;

    public BasicInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic_info, container, false);
        setupFragmentVariables(view);
        setupButtonListeners();
        setupRadioGroupListeners();
        int random = rng(fakeNames.size());
        etAlias.setText(fakeNames.get(random));
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BasicInfoFragment.OnFragmentInteractionListener) {
            mListener = (BasicInfoFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public interface OnFragmentInteractionListener {
        void onBackPressed();
        void goToInterestsFragment(String gender, String interestedIn, long age, String name, String alias);
    }

    private void setupFragmentVariables(View view) {
        title = view.findViewById(R.id.title);
        explanation = view.findViewById(R.id.explanation);
        tvGender = view.findViewById(R.id.gender);
        tvInterestedIn = view.findViewById(R.id.interestedIn);
        birthday = view.findViewById(R.id.birthday);
        name = view.findViewById(R.id.name);
        alias = view.findViewById(R.id.alias);
        aliasNote = view.findViewById(R.id.aliasNote);
        placeholderBirthday = view.findViewById(R.id.placeholderBirthday);
        etName = view.findViewById(R.id.etName);
        etAlias = view.findViewById(R.id.etAlias);
        genderOptions = view.findViewById(R.id.genderOptions);
        preferenceOptions = view.findViewById(R.id.preferenceOptions);
        refresh = view.findViewById(R.id.refresh);
        btnContinue = view.findViewById(R.id.btnContinue);
    }

    private void setupButtonListeners() {
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int random = rng(fakeNames.size());
                etAlias.setText(fakeNames.get(random));
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToInterestsFragment(gender, interestedIn, age,
                        etName.getText().toString(), etAlias.getText().toString());
            }
        });
        placeholderBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDateFromPicker();
            }
        });
    }

    private void getDateFromPicker() {
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
                dateFromPicker = String.valueOf(monthOfYear) + "/" + String.valueOf(dayOfMonth + 1) + "/" + String.valueOf(year);
                placeholderBirthday.setText(dateFromPicker);
                try {
                    DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                    Date birthday = formatter.parse(dateFromPicker);
                    Date today = new Date();
                    long diffInMillies = Math.abs(today.getTime() - birthday.getTime());
                    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                    age = diff;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        if (getActivity().getSupportFragmentManager() == null){
            Log.e("Wut", "it's null :(");
        }
        dateFragment.show(getActivity().getSupportFragmentManager(), "Date Picker");
    }

    private void setupRadioGroupListeners() {
        genderOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.gender_male:
                        gender = "Male";
                        break;
                    case R.id.gender_female:
                        gender = "Female";
                        break;
                    case R.id.gender_other:
                        gender = "Other";
                        break;
                }
            }
        });

        preferenceOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.prefer_men:
                        interestedIn = "Male";
                        break;
                    case R.id.prefer_women:
                        interestedIn = "Female";
                        break;
                    case R.id.no_preference:
                        interestedIn = "No preference";
                        break;
                }
            }
        });
    }

    private int rng(int size) {
        Random rand = new Random();
        return rand.nextInt(size);
    }
}
