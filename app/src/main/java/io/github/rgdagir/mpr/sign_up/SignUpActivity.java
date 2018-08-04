package io.github.rgdagir.mpr.sign_up;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.HashMap;

import io.github.rgdagir.mpr.MainActivity;
import io.github.rgdagir.mpr.R;
import io.github.rgdagir.mpr.models.Interest;
import io.github.rgdagir.mpr.models.UserInterest;

public class SignUpActivity extends AppCompatActivity
        implements LoginInfoFragment.OnFragmentInteractionListener, BasicInfoFragment.OnFragmentInteractionListener,
        InterestsFragment.OnFragmentInteractionListener, PicturesFragment.OnFragmentInteractionListener {

    String newUserUsername;
    String newUserPassword;
    ParseUser newUser = new ParseUser();
    LoginInfoFragment initialFragment = new LoginInfoFragment();
    BasicInfoFragment basicInfoFragment = new BasicInfoFragment();
    InterestsFragment interestsFragment = new InterestsFragment();
    PicturesFragment picturesFragment = new PicturesFragment();
    HashMap<Interest, Boolean> checkedInterests = new HashMap();
    Boolean interestsSkipped = false;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, initialFragment)
                .commit();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public static void switchFragment(FragmentTransaction fragmentTransaction, Fragment fragment) {
        fragmentTransaction.replace(R.id.flContainer, fragment)
                //.addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    public void goToBasicInfoFragment(String email, String password) {
        newUser.setEmail(email);
        newUser.setUsername(email);
        newUser.setPassword(password);
        newUserUsername = email;
        newUserPassword = password;
        switchFragment(fragmentManager.beginTransaction(), new BasicInfoFragment());
    }

    public void goToInterestsFragment(String gender, String interestedIn, long age, String name, String alias) {
        newUser.put("gender", gender);
        newUser.put("interestedIn", interestedIn);
        newUser.put("age", age);
        newUser.put("firstName", name);
        newUser.put("fakeName", alias);
        newUser.put("minAge", 18);
        newUser.put("maxAge", 30);
        switchFragment(fragmentManager.beginTransaction(), new InterestsFragment());
    }

    public void goToPicturesFragment(HashMap<Interest, Boolean> checked, Boolean skipped) {
        interestsSkipped = skipped;
        checkedInterests = checked;
        switchFragment(fragmentManager.beginTransaction(), new PicturesFragment());
    }

    public void createNewUser() {
        newUser.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    if (!interestsSkipped) {
                        createUserInterests(checkedInterests, newUser);
                    }
                    // Hooray! Let them use the app now.
                    Log.d("SignUpActivity", "New user created successfully!");
                    ParseUser.logInInBackground(newUserUsername, newUserPassword, new LogInCallback() {
                                public void done(ParseUser user, ParseException e) {
                                    if (user != null) {
                                        launchMainActivity();
                                    }
                                }
                            });
                } else {
                    Log.d("SignUpActivity", "Creating new user failed :(");
                }
            }
        });
    }

    private void createUserInterests(HashMap<Interest, Boolean> checkedInterests, ParseUser user) {
        for (Interest i : checkedInterests.keySet()) {
            if (checkedInterests.get(i)) {
                UserInterest userInterest = new UserInterest();
                userInterest.put("user", user);
                userInterest.put("interest", i);
                userInterest.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.e("ChatActivity", "Creating user interest success! :)");
                        } else {
                            Log.e("ChatActivity", "Creating user interest failed :(");
                        }
                    }
                });
            }
        }
    }

    private void launchMainActivity() {
        // default ACLs for User object
        ParseACL parseACL = new ParseACL(ParseUser.getCurrentUser());
        parseACL.setPublicReadAccess(true);
        ParseUser.getCurrentUser().setACL(parseACL);
        Log.d("SignUpActivity", "Login success!");
        // set current user for installation
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("currentUserId", ParseUser.getCurrentUser().getObjectId());
        installation.saveInBackground();
        // redirect to main activity
        final Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onRadioButtonClicked(View view) {
    }

    public void onCheckboxClicked(View view) {
    }
}
