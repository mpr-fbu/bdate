package io.github.rgdagir.mpr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends Fragment {
    private ProfileFragment.OnFragmentInteractionListener mListener;
    private TextView profileName;
    private TextView profileAge;
    private TextView profilePic;
    private TextView profileBio;
    private TextView profileWebpage;
    private Button editProfileBtn;
    private Button logoutBtn;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        Context context = getActivity();
        ParseUser currentuser = ParseUser.getCurrentUser();


    }

    private boolean fetchUserProfileData (ParseUser user){
        ParseQuery<ParseObject> profileDataQuery = ParseQuery.getQuery("User");
        profileDataQuery.whereEqualTo("username", user.getUsername());
        profileDataQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseObject> userData, ParseException e) {
                if (e == null && userData.size() == 1){
                    Log.d("ProfileQuerySuccess", userData.size());
                    ParseObject name = userData.get(0).

                } else {
                    Log.d("ProfileQueryFailed", e.printStackTrace());
                }
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileFragment.OnFragmentInteractionListener) {
            mListener = (ProfileFragment.OnFragmentInteractionListener) context;
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

    public interface OnFragmentInteractionListener {
        // Placeholder, to be inserted when clicking is introduced
        void onFragmentInteraction(Uri uri);
    }
}
