package io.github.rgdagir.blind8.sign_up;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.github.rgdagir.blind8.DatePickerFragment;
import io.github.rgdagir.blind8.R;

public class BasicInfoFragment extends Fragment {

    private BasicInfoFragment.OnFragmentInteractionListener mListener;

    private TextView title;
    private TextView explanation;
    private TextView tvGender;
    private TextView tvInterestedIn;
    private TextView birthday;
    private TextView name;
    private EditText placeholderBirthday;
    private EditText etName;
    private RadioGroup genderOptions;
    private RadioGroup preferenceOptions;
    private Button btnContinue;
    private String gender;
    private String interestedIn;
    private int age;
    private String dateFromPicker;
    private Date dob;
    private TextInputLayout tilBirthday;
    private boolean genderPicked;
    private boolean preferencePicked;


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
        setupTextChangeListeners();
        btnContinue.setVisibility(View.INVISIBLE);
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
        void goToInterestsFragment(String gender, String interestedIn, int age, String name, Date dob);
    }

    private void setupFragmentVariables(View view) {
        title = view.findViewById(R.id.title);
        explanation = view.findViewById(R.id.explanation);
        tvGender = view.findViewById(R.id.gender);
        tvInterestedIn = view.findViewById(R.id.interestedIn);
        birthday = view.findViewById(R.id.birthday);
        name = view.findViewById(R.id.name);
        placeholderBirthday = view.findViewById(R.id.placeholderBirthday);
        etName = view.findViewById(R.id.etName);
        genderOptions = view.findViewById(R.id.genderOptions);
        preferenceOptions = view.findViewById(R.id.preferenceOptions);
        btnContinue = view.findViewById(R.id.btnContinue);
        tilBirthday = view.findViewById(R.id.tilBirthday);
    }

    private void setupButtonListeners() {
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToInterestsFragment(gender, interestedIn, age,
                        etName.getText().toString(), dob);
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
        Calendar cal /* go stanford! */ = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", cal.get(Calendar.YEAR));
        args.putInt("month", cal.get(Calendar.MONTH));
        args.putInt("day", cal.get(Calendar.DAY_OF_MONTH));
        dateFragment.setArguments(args);
        // Set up callback to retrieve date info
        dateFragment.setCallBack(new DatePickerDialog.OnDateSetListener(){
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                dateFromPicker = String.valueOf(monthOfYear + 1) + "/" + String.valueOf(dayOfMonth) + "/" + String.valueOf(year);
                placeholderBirthday.setText(dateFromPicker);
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                dob = null;
                try {
                    dob = formatter.parse(dateFromPicker);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                age = calculateAge(year, monthOfYear + 1, dayOfMonth);
                if (age < 18){
                    tilBirthday.setError("You have to be 18 or older to use this app");
                    btnContinue.setVisibility(View.INVISIBLE);
                } else {
                    tilBirthday.setError(null);
                    if (genderPicked && preferencePicked && etName.getText().length() > 0) {
                        btnContinue.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        if (getActivity().getSupportFragmentManager() == null){
            Log.e("Wut", "it's null :(");
        }
        dateFragment.show(getActivity().getSupportFragmentManager(), "Date Picker");
    }

    private int calculateAge(int year, int monthOfYear, int dayOfMonth){
        int returnAge;
        Calendar calendar = Calendar.getInstance();
        int presentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int presentMonthOfYear = calendar.get(Calendar.MONTH) + 1;
        int presentYear = calendar.get(Calendar.YEAR);
        returnAge = presentYear - year;
        if ((presentMonthOfYear < monthOfYear) || (presentMonthOfYear == monthOfYear && presentDayOfMonth < dayOfMonth)){
            returnAge--;
        }
        return returnAge;
    }

    private void setupRadioGroupListeners() {
        genderOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                genderPicked = true;
                switch(checkedId) {
                    case R.id.gender_male:
                        gender = "Male";
                        checkCanContinue();
                        break;
                    case R.id.gender_female:
                        gender = "Female";
                        checkCanContinue();
                        break;
                    case R.id.gender_other:
                        gender = "Other";
                        checkCanContinue();
                        break;
                }
            }
        });

        preferenceOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                preferencePicked = true;
                switch(checkedId) {
                    case R.id.prefer_men:
                        interestedIn = "Male";
                        checkCanContinue();
                        break;
                    case R.id.prefer_women:
                        interestedIn = "Female";
                        checkCanContinue();
                        break;
                    case R.id.no_preference:
                        interestedIn = "No preference";
                        checkCanContinue();
                        break;
                }
            }
        });
    }

    private void checkCanContinue() {
        if (genderPicked && preferencePicked && tilBirthday.getError() == null && etName.getText().length() != 0) {
            btnContinue.setVisibility(View.VISIBLE);
        }
    }

    private void setupTextChangeListeners() {
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() < 1) {
                    btnContinue.setVisibility(View.INVISIBLE);
                } else {
                    if (genderPicked && preferencePicked && tilBirthday.getError() == null) {
                        btnContinue.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


}
