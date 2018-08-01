package io.github.rgdagir.mpr.sign_up;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import io.github.rgdagir.mpr.R;

public class SignUpActivity extends AppCompatActivity
        implements LoginInfoFragment.OnFragmentInteractionListener, BasicInfoFragment.OnFragmentInteractionListener,
        InterestsFragment.OnFragmentInteractionListener, PicturesFragment.OnFragmentInteractionListener {

    LoginInfoFragment initialFragment = new LoginInfoFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // set chats fragment as initial
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
        fragmentTransaction.replace(R.id.flContainer, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        onBackPressed();
        goToBasicInfoFragment();
        goToInterestsFragment();
        goToPicturesFragment();
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

    public void goToBasicInfoFragment() {
        switchFragment(fragmentManager.beginTransaction(), new BasicInfoFragment());
    }

    public void goToInterestsFragment() {
        switchFragment(fragmentManager.beginTransaction(), new InterestsFragment());
    }

    public void goToPicturesFragment() {
        switchFragment(fragmentManager.beginTransaction(), new PicturesFragment());
    }

//    private static String ACTIVITY_TAG = "SIGN UP";
//    private EditText etFirstNameSignUp;
//    private EditText etFakeNameSignUp;
//    private EditText etEmailSignUp;
//    private EditText etPasswordSignUp;
//    private Button signupBtn;
//    private Button refresh;
//    private List<String> fakeNames = new ArrayList<>(Arrays.asList("Anonymous Anon", "Mysterious Stranger", "?????"));

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign_up);
//        findViews();
//        int random = rng(fakeNames.size());
//        etFakeNameSignUp.setText(fakeNames.get(random));
//        setOnClickListeners();
//    }
//
//    private void findViews() {
//        etFirstNameSignUp = findViewById(R.id.firstNameSignUp);
//        etFakeNameSignUp = findViewById(R.id.fakeNameSignUp);
//        etEmailSignUp = findViewById(R.id.emailSignUp);
//        etPasswordSignUp = findViewById(R.id.passwordSignUp);
//        signupBtn = findViewById(R.id.signUpBtn);
//        refresh = findViewById(R.id.refresh);
//    }
//
//    private void setOnClickListeners() {
//        // Invoke signUpInBackground when user clicks the button
//        signupBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Create the ParseUser
//                ParseUser user = new ParseUser();
//                createNewUser(user);
//            }
//        });
//        //refresh fake name when clicked
//        refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int random = rng(fakeNames.size());
//                etFakeNameSignUp.setText(fakeNames.get(random));
//            }
//        });
//    }
//
//    private void createNewUser(ParseUser user) {
//        user.setUsername(etEmailSignUp.getText().toString());
//        user.setPassword(etPasswordSignUp.getText().toString());
//        user.setEmail(etEmailSignUp.getText().toString());
//        user.put("firstName", etFirstNameSignUp.getText().toString());
//        user.put("minAge", 18);
//        user.put("maxAge", 500);
//        user.put("fakeName", etFakeNameSignUp.getText().toString());
//        user.signUpInBackground(new SignUpCallback() {
//            public void done(ParseException e) {
//                if (e == null) {
//                    // Hooray! Let them use the app now.
//                    Log.d(ACTIVITY_TAG, "Success!");
//                    ParseUser.logInInBackground(etEmailSignUp.getText().toString(),
//                            etPasswordSignUp.getText().toString(), new LogInCallback() {
//                                public void done(ParseUser user, ParseException e) {
//                                    if (user != null) {
//                                        launchMainActivity();
//                                    }
//                                }
//                            });
//                } else {
//                    // Sign up didn't succeed.
//                    // Look at the ParseException to figure out what went wrong
//                    Log.d(ACTIVITY_TAG, "Failed :(");
//                }
//            }
//        });
//    }
//
//    private int rng(int size) {
//        Random rand = new Random();
//        return rand.nextInt(size);
//    }
//
//    private void launchMainActivity() {
//        // default ACLs for User object
//        ParseACL parseACL = new ParseACL(ParseUser.getCurrentUser());
//        parseACL.setPublicReadAccess(true);
//        ParseUser.getCurrentUser().setACL(parseACL);
//        Log.d(ACTIVITY_TAG, "Login success!");
//        // set current user for installation
//        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
//        installation.put("currentUserId", ParseUser.getCurrentUser().getObjectId());
//        installation.saveInBackground();
//        // redirect to main activity
//        final Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
//    }
}
