package io.github.rgdagir.blind8.sign_up;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.github.rgdagir.blind8.R;

public class LoginInfoFragment extends Fragment {

    private LoginInfoFragment.OnFragmentInteractionListener mListener;
    private List<String> fakeNames = new ArrayList<>(Arrays.asList("Anonymous Anon", "Mysterious Stranger", "An old chair", "Roonil Wazlib", "Ash Ketchum"));

    private TextView title;
    private TextView explanation;
    private TextView email;
    private TextView password;
    private TextView alias;
    private TextView aliasNote;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etAlias;
    private Button btnContinue;
    private Button fakeContinue;
    private ImageView refresh;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;

    public LoginInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_info, container, false);
        setupFragmentVariables(view);
        setupButtonListeners();
        setupTextChangeListeners();
        int random = rng(fakeNames.size());
        etAlias.setText(fakeNames.get(random));
        btnContinue.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginInfoFragment.OnFragmentInteractionListener) {
            mListener = (LoginInfoFragment.OnFragmentInteractionListener) context;
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
        void goToBasicInfoFragment(String email, String password, String fakeName);
    }

    private void setupFragmentVariables(View view) {
        title = view.findViewById(R.id.title);
        explanation = view.findViewById(R.id.explanation);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnContinue = view.findViewById(R.id.btnContinue);
        fakeContinue = view.findViewById(R.id.btnFakeContinue);
        alias = view.findViewById(R.id.alias);
        aliasNote = view.findViewById(R.id.aliasNote);
        etAlias = view.findViewById(R.id.etAlias);
        refresh = view.findViewById(R.id.refresh);
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
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
                mListener.goToBasicInfoFragment(etEmail.getText().toString(),
                        etPassword.getText().toString(), etAlias.getText().toString());
            }
        });
    }

    private void setupTextChangeListeners() {
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() < 6) {
                    tilPassword.setError("Password has to be at least 6 characters long");
                    btnContinue.setVisibility(View.INVISIBLE);
                } else {
                    tilPassword.setError(null);
                    if (etEmail.getText().length() != 0 && etAlias.getText().length() != 0) {
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
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isValidEmail(s.toString())){
                    tilEmail.setError("Invalid email");
                    btnContinue.setVisibility(View.INVISIBLE);

                } else {
                    tilEmail.setError(null);
                    if (etPassword.getError() == null && etAlias.getText().length() != 0) {
                        btnContinue.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
        etAlias.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    btnContinue.setVisibility(View.INVISIBLE);
                } else {
                    if (etEmail.getError() == null && etPassword.getError() == null) {
                        btnContinue.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private int rng(int size) {
        Random rand = new Random();
        return rand.nextInt(size);
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
