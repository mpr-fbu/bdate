package io.github.rgdagir.mpr.sign_up;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.github.rgdagir.mpr.R;

public class BasicInfoFragment extends Fragment {

    private BasicInfoFragment.OnFragmentInteractionListener mListener;

    private TextView title;
    private TextView explanation;
    private TextView gender;
    private TextView birthday;
    private TextView name;
    private TextView alias;
    private TextView aliasNote;
    private EditText placeholderGender;
    private EditText placeholderBirthday;
    private EditText etName;
    private EditText etAlias;
    private Button back;
    private Button btnContinue;


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
        void goToInterestsFragment(String gender, Integer age, String name, String alias);
    }

    private void setupFragmentVariables(View view) {
        title = view.findViewById(R.id.title);
        explanation = view.findViewById(R.id.explanation);
        gender = view.findViewById(R.id.gender);
        birthday = view.findViewById(R.id.birthday);
        name = view.findViewById(R.id.name);
        alias = view.findViewById(R.id.alias);
        aliasNote = view.findViewById(R.id.aliasNote);
        placeholderGender = view.findViewById(R.id.placeholderGender);
        placeholderBirthday = view.findViewById(R.id.placeholderBirthday);
        etName = view.findViewById(R.id.etName);
        etAlias = view.findViewById(R.id.etAlias);
        back = view.findViewById(R.id.back);
        btnContinue = view.findViewById(R.id.btnContinue);
    }

    private void setupButtonListeners() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBackPressed();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToInterestsFragment(placeholderGender.getText().toString(), 18,
                        etName.getText().toString(), etAlias.getText().toString());
            }
        });
    }
}
