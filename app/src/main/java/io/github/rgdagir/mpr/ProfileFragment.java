package io.github.rgdagir.mpr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends Fragment {
    private ProfileFragment.OnFragmentInteractionListener mListener;
    private EditText profileName;
    private TextView profileAge;
    private ImageView profilePic;
    private TextView profileBio;
    private TextView profileWebpage;
    private Button editProfileBtn;
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
        profileName = view.findViewById(R.id.tvProfileName);
        profileAge = view.findViewById(R.id.tvProfileAge);
        profilePic = view.findViewById(R.id.ivProfilePic);
        profileBio = view.findViewById(R.id.tvProfileBio);
        profileWebpage = view.findViewById(R.id.tvProfileWebpage);
        editProfileBtn = view.findViewById(R.id.editProfileButton);
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

    // Embeds the child fragment dynamically
    private void insertNestedEditProfileFragment() {
        Fragment editProfileFragment = new EditProfileFragment();
        FragmentTransaction transactionToChild = getChildFragmentManager().beginTransaction();
        FragmentTransaction killParent = getFragmentManager().beginTransaction();
        transactionToChild.replace(R.id.child_fragment_container, editProfileFragment).commit();
        killParent.replace(R.id.flContainer, editProfileFragment).commit();

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
                    ParseUser userData = userDataList.get(0); // the list should ideally have only one element, given users are unique

                    String name = userData.get("firstName").toString() + " " + userData.get("lastName").toString();
                    String age = userData.get("age").toString();
                    String bio = userData.get("bio").toString();
                    String webpage = userData.get("webpage").toString();

                    profileName.setText(name);
                    profileAge.setText(age);
//                    Glide.with(context)
//                            .load(userData.getParseFile("profilePic").getUrl())
//                            .centerCrop()
//                            .into(profilePic);
                    Glide.with(context).load(userData.getParseFile("profilePic").getUrl())
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
                    profileBio.setText(bio);
                    profileWebpage.setText(webpage);

                } else {
                    Log.e("ProfileQuery", "Failed");
                    e.printStackTrace();
                }
            }
        });
    }

    public void logout(ParseUser user){
        user.logOutInBackground();
        Intent goToLogin = new Intent(context, LoginActivity.class);
        goToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        // Placeholder, to be inserted when clicking is introduced
        void goToEditProfile();
    }
}
