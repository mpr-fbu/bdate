package io.github.rgdagir.mpr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import io.github.rgdagir.mpr.models.Conversation;
import io.github.rgdagir.mpr.models.Milestone;
import me.relex.circleindicator.CircleIndicator;

public class ProfileFragment extends Fragment {
    private ProfileFragment.OnFragmentInteractionListener mListener;
    private ParseImageView profilePic;
    private ImageView defaultProfilePic;
    private TextView profileName;
    private TextView profileAge;
    private TextView profileDistance;
    private TextView profileStatus;
    private TextView profileOccupation;
    private TextView profileEducation;

    private ViewPager mPager;
    private static int currentPage = 0;
    private ArrayList<String> mGalleryImages;
    private CircleIndicator indicator;
    private RecyclerView rvInterests;
    private ImageButton editProfileBtn;
    private Button logoutBtn;

    private Context context;
    private ParseUser otherUser;
    private boolean isMyProfile;
    private ParseUser currentUser;
    private Conversation conversation;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = ParseUser.getCurrentUser();
        if (getArguments() == null) {
            isMyProfile = true;
        } else {
            conversation = (Conversation) getArguments().getSerializable("conversation");
            isMyProfile = false;
            if (conversation.getUser1().getObjectId().equals(currentUser.getObjectId())) {
                otherUser = conversation.getUser2();
            } else {
                otherUser = conversation.getUser1();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity();
        mGalleryImages = new ArrayList<>();

        profilePic = view.findViewById(R.id.ivProfilePic);
        defaultProfilePic = view.findViewById(R.id.defaultProfilePic);
        profileName = view.findViewById(R.id.tvProfileName);
        profileAge = view.findViewById(R.id.tvProfileAge);
        profileDistance = view.findViewById(R.id.tvDistance);
        profileStatus = view.findViewById(R.id.tvStatus);
        profileOccupation = view.findViewById(R.id.tvOccupation);
        profileEducation = view.findViewById(R.id.tvEducation);
        editProfileBtn = view.findViewById(R.id.editProfileBtn);
        logoutBtn = view.findViewById(R.id.logoutBtn);
        mPager = view.findViewById(R.id.pager);
        indicator = view.findViewById(R.id.indicator);

        if (isMyProfile) {
            fetchProfileData(currentUser);
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
        } else {
            fetchProfileData(otherUser);
            editProfileBtn.setVisibility(View.INVISIBLE);
            logoutBtn.setVisibility(View.INVISIBLE);
        }
        return view;
    }

    public static ProfileFragment newInstance(Conversation conversation) {
        ProfileFragment myFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("conversation", conversation);
        myFragment.setArguments(args);
        return myFragment;
    }

    private void fetchProfileData(ParseUser user) {
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
                    if (isMyProfile) {
                        setMyUserDetails(userData);
                    } else {
                        setOtherUserDetails(userData);
                    }
                } else {
                    Log.e("ProfileQuery", "Failed");
                    e.printStackTrace();
                }
            }
        });
    }

    private void setMyUserDetails(ParseUser user) {
        // simply display your personal information
        String name = user.get("firstName").toString();
        String age = user.get("age").toString();
        String status = user.get("bio").toString();
        Object occupation = user.get("occupation");
        Object education = user.get("education");

        profileName.setText(name);
        profileAge.setText("Age: " + age);
        profileDistance.setText("0 miles away");
        defaultProfilePic.setVisibility(View.INVISIBLE);
        setProfilePicture(user);
        profileStatus.setText(status);
        if (occupation == null) {
            profileOccupation.setText("---");
        } else {
            profileOccupation.setText(occupation.toString());
        }
        if (education == null) {
            profileEducation.setText("---");
        } else {
            profileEducation.setText(education.toString());
        }
        populateGallery(user);
    }

    private void setOtherUserDetails(ParseUser user) {
        // check if they are revealed before displaying
        String status = user.get("bio").toString();
        profileStatus.setText(status);
        if (Milestone.canSeeName(conversation)) {
            String name = user.get("firstName").toString();
            profileName.setText(name);
        } else {
            String fakeName = user.get("fakeName").toString();
            profileName.setText(fakeName);
        }
        if (Milestone.canSeeAge(conversation)) {
            String age = user.get("age").toString();
            profileAge.setText("Age: " + age);
        } else {
            profileAge.setText("Age: --");
        }
        if (Milestone.canSeeDistanceAway(conversation)) {
            // get distance (in miles) between user who started the conversation and the one trying to match
            double distanceFromMatch = SearchFragment.calcDistance(currentUser.getParseGeoPoint("lastLocation").getLatitude(),
                    user.getParseGeoPoint("lastLocation").getLatitude(),
                    currentUser.getParseGeoPoint("lastLocation").getLongitude(),
                    user.getParseGeoPoint("lastLocation").getLongitude(), 0, 0);
            int roundedDistance10 = (int) (distanceFromMatch * 10);
            double distance = roundedDistance10 / 10.0;
            profileDistance.setText(Double.toString(distance) + " miles away");
        } else {
            profileDistance.setText("--- miles away");
        }
        if (Milestone.canSeeOccupation(conversation)) {
            Object occupation = user.get("occupation");
            Object education = user.get("education");
            if (occupation == null) {
                profileOccupation.setText("---");
            } else {
                profileOccupation.setText(occupation.toString());
            }
            if (education == null) {
                profileEducation.setText("---");
            } else {
                profileEducation.setText(education.toString());
            }
        } else {
            profileOccupation.setText("---");
            profileEducation.setText("---");
        }
        if (Milestone.canSeeProfilePicture(conversation)) {
            setProfilePicture(user);
            defaultProfilePic.setVisibility(View.INVISIBLE);
        }
        if (Milestone.canSeeGallery(conversation)) {
            populateGallery(user);
        }
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

    private void populateGallery(ParseUser user) {
        if (user.getParseFile("coverPhoto1") != null) {
            mGalleryImages.add(user.getParseFile("coverPhoto1").getUrl());
        }
        if (user.getParseFile("coverPhoto2") != null) {
            mGalleryImages.add(user.getParseFile("coverPhoto2").getUrl());
        }
        if (user.getParseFile("coverPhoto3") != null) {
            mGalleryImages.add(user.getParseFile("coverPhoto3").getUrl());
        }
        mPager.setAdapter(new GalleryAdapter(context, mGalleryImages));
        indicator.setViewPager(mPager);
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
