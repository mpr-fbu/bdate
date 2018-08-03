package io.github.rgdagir.mpr.sign_up;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.rgdagir.mpr.R;
import io.github.rgdagir.mpr.models.Interest;

public class InterestsFragment extends Fragment {

    private InterestsFragment.OnFragmentInteractionListener mListener;
    private ScrollView interestView;
    private List<Interest> interests;

    private TextView title;
    private TextView explanation;
    private Button skip;
    private Button back;
    private Button btnContinue;
    private Boolean skipped;
    private SignUpInterestAdapter signUpInterestAdapter;
    RecyclerView rvInterests;
    ArrayList<Interest> mInterests;
    Context context;
    ArrayList<Interest> mCheckedInterests;
    HashMap<Interest, Boolean> checked = new HashMap();

    CheckBox art, music, science, sports_playing, video_games, reading, crafts, traveling, food, movies, tv_shows, animals, memes,
            outdoors, fitness, photography, cooking, puzzles, partying, sports_watching, technology, musical_instruments, dance;
    List<CheckBox> checkBoxes = new ArrayList<>();


    public InterestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        skipped = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interests, container, false);
        setupFragmentVariables(view);
        setupButtonListeners();
        populateInterests();
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
        void goToPicturesFragment(HashMap<Interest, Boolean> checked);
    }

    private void setupFragmentVariables(View view) {
        context = getContext();
        //interestView = view.findViewById(R.id.interests);
        title = view.findViewById(R.id.title);
        explanation = view.findViewById(R.id.explanation);
        skip = view.findViewById(R.id.skip);
        back = view.findViewById(R.id.back);
        btnContinue = view.findViewById(R.id.btnContinue);
        rvInterests = view.findViewById(R.id.rvInterests);
        mInterests = new ArrayList<>();
        mCheckedInterests = new ArrayList<>();
        signUpInterestAdapter = new SignUpInterestAdapter(mInterests);
        rvInterests.setLayoutManager(new LinearLayoutManager(context));
        rvInterests.setAdapter(signUpInterestAdapter);
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
                //getAllCheckedInterests(interestView);
                checked = signUpInterestAdapter.checked;
                mListener.goToPicturesFragment(checked);
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipped = true;
                checked = signUpInterestAdapter.checked;
                mListener.goToPicturesFragment(checked);
            }
        });
    }

    private void populateInterests() {
        final ParseQuery<Interest> interestQuery = new Interest.Query();
        interestQuery.findInBackground(new FindCallback<Interest>() {
            @Override
            public void done(List<Interest> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); ++i) {
                        Interest interest = objects.get(i);
                        mInterests.add(interest);
                        signUpInterestAdapter.notifyItemInserted(mInterests.size() - 1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}