package io.github.rgdagir.mpr.sign_up;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.github.rgdagir.mpr.R;

public class InterestsFragment extends Fragment {

    private InterestsFragment.OnFragmentInteractionListener mListener;

    private TextView title;
    private TextView explanation;
    private Button skip;
    private Button back;
    private Button btnContinue;


    public InterestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interests, container, false);
        setupFragmentVariables(view);
        setupButtonListeners();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InterestsFragment.OnFragmentInteractionListener) {
            mListener = (InterestsFragment.OnFragmentInteractionListener) context;
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
        // Placeholder, to be inserted when clicking is introduced
        void onBackPressed();
        void goToPicturesFragment();
    }

    private void setupFragmentVariables(View view) {
        title = view.findViewById(R.id.title);
        explanation = view.findViewById(R.id.explanation);
        skip = view.findViewById(R.id.skip);
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
                Log.d("BasicInfoFragment", "continue button clicked");
                mListener.goToPicturesFragment();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToPicturesFragment();
            }
        });
    }
}