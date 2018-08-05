package io.github.rgdagir.blind8.sign_up;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.rgdagir.blind8.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
    private ImageView refresh;

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
        int random = rng(fakeNames.size());
        etAlias.setText(fakeNames.get(random));
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
        alias = view.findViewById(R.id.alias);
        aliasNote = view.findViewById(R.id.aliasNote);
        etAlias = view.findViewById(R.id.etAlias);
        refresh = view.findViewById(R.id.refresh);
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


    private int rng(int size) {
        Random rand = new Random();
        return rand.nextInt(size);
    }
}
