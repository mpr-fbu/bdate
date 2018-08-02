package io.github.rgdagir.mpr.sign_up;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.github.rgdagir.mpr.R;

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
    private Button back;
    private Button refresh;
    private Button btnContinue;
    String gender;
    String interestedIn;

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
        void goToInterestsFragment(String gender, String interestedIn, Integer age, String name, String alias);
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
        back = view.findViewById(R.id.back);
        refresh = view.findViewById(R.id.refresh);
        btnContinue = view.findViewById(R.id.btnContinue);
    }

    private void setupButtonListeners() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBackPressed();
            }
        });

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
                mListener.goToInterestsFragment(gender, interestedIn, 18,
                        etName.getText().toString(), etAlias.getText().toString());
            }
        });
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
                        interestedIn = "Men";
                        break;
                    case R.id.prefer_women:
                        interestedIn = "Women";
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
