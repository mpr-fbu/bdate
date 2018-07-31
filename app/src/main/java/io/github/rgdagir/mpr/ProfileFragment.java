package io.github.rgdagir.mpr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends Fragment {
    private ProfileFragment.OnFragmentInteractionListener mListener;
    private ParseImageView profilePic;
    private TextView profileName;
    private TextView profileAge;
    private TextView profileDistance;
    private TextView profileStatus;
    private TextView profileOccupation;
    private TextView profileEducation;
    private RecyclerView rvInterests;
    private ImageButton editProfileBtn;
    private Button logoutBtn;
    private Context context;

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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity();
        final ParseUser currentUser = ParseUser.getCurrentUser();

        profilePic = view.findViewById(R.id.ivProfilePic);
        profileName = view.findViewById(R.id.tvProfileName);
        profileAge = view.findViewById(R.id.tvProfileAge);
        profileDistance = view.findViewById(R.id.tvDistance);
        profileStatus = view.findViewById(R.id.tvStatus);
        profileOccupation = view.findViewById(R.id.tvOccupation);
        profileEducation = view.findViewById(R.id.tvEducation);
        editProfileBtn = view.findViewById(R.id.editProfileBtn);
        logoutBtn = view.findViewById(R.id.logoutBtn);

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goToEditProfile();
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(currentUser);
            }
        });

        fetchUserProfileData(currentUser);
        return view;
    }

    private void fetchUserProfileData (ParseUser user){
        ParseQuery<ParseUser> profileDataQuery = ParseUser.getQuery();
        profileDataQuery.whereEqualTo("username", user.getUsername());
        Log.e("ProfileQuery", user.getUsername());
        profileDataQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> userDataList, ParseException e) {
                if (e == null){
                    Log.e("ProfileQuerySuccess", Integer.toString(userDataList.size()));
                    // the list should ideally have only one element, given users are unique
                    ParseUser userData = userDataList.get(0);
                    setUserDetails(userData);
                } else {
                    Log.e("ProfileQuery", "Failed");
                    e.printStackTrace();
                }
            }
        });
    }

    private void setUserDetails(ParseUser user) {
        String name = user.get("firstName").toString();
        String age = user.get("age").toString();
        String status = user.get("bio").toString();

        profileName.setText(name);
        profileAge.setText("Age: " + age);
        setProfilePicture(user);
        profileStatus.setText(status);
    }

    private void setProfilePicture(ParseUser user) {
        Glide.with(context).load(user.getParseFile("profilePic").getUrl())
                .asBitmap().centerCrop().dontAnimate()
                .placeholder(R.drawable.ic_action_name)
                .error(R.drawable.ic_action_name)
                .into(new BitmapImageViewTarget(profilePic) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        profilePic.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    public void logout(ParseUser user){
        user.logOutInBackground();
        Intent goToLogin = new Intent(context, LoginActivity.class);
        goToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // set current user on installation to null
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("currentUserId", "");
        installation.saveInBackground();
        startActivity(goToLogin);
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
        void goToEditProfile();
    }
}
