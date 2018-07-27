package io.github.rgdagir.mpr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SignUpActivity extends AppCompatActivity {

    private static String ACTIVITY_TAG = "SIGN UP";
    private EditText etFirstNameSignUp;
    private EditText etLastNameSignUp;
    private EditText etEmailSignUp;
    private EditText etPasswordSignUp;
    private Button signupBtn;
    private List<String> fakeNames = new ArrayList<>(Arrays.asList("Anonymous Anon", "Mysterious Stranger", "?????"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etFirstNameSignUp = findViewById(R.id.firstNameSignUp);
        etLastNameSignUp = findViewById(R.id.lastNameSignUp);
        etEmailSignUp = findViewById(R.id.emailSignUp);
        etPasswordSignUp = findViewById(R.id.passwordSignUp);
        signupBtn = findViewById(R.id.signUpBtn);

        // Invoke signUpInBackground when user clicks the button
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create the ParseUser
                ParseUser user = new ParseUser();
                // Set core properties
                user.setUsername(etEmailSignUp.getText().toString());
                user.setPassword(etPasswordSignUp.getText().toString());
                user.setEmail(etEmailSignUp.getText().toString());
                // Set custom properties
                int random = rng(fakeNames.size());
                user.put("firstName", etFirstNameSignUp.getText().toString());
                user.put("lastName", etLastNameSignUp.getText().toString());
                user.put("fakeName", fakeNames.get(random));
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Hooray! Let them use the app now.
                            Log.d(ACTIVITY_TAG, "Success!");
                            ParseUser.logInInBackground(etEmailSignUp.getText().toString(),
                                    etPasswordSignUp.getText().toString(), new LogInCallback() {
                                public void done(ParseUser user, ParseException e) {
                                    if (user != null) {
                                        launchMainActivity();
                                    }
                                }
                            });
                        } else {
                            // Sign up didn't succeed.
                            // Look at the ParseException to figure out what went wrong
                            Log.d(ACTIVITY_TAG, "Failed :(");
                        }
                    }
                });
            }
        });
    }

    private int rng(int size) {
        Random rand = new Random();
        return rand.nextInt(size);
    }

    private void launchMainActivity() {
        // default ACLs for User object
        ParseACL parseACL = new ParseACL(ParseUser.getCurrentUser());
        parseACL.setPublicReadAccess(true);
        ParseUser.getCurrentUser().setACL(parseACL);
        Log.d(ACTIVITY_TAG, "Login success!");
        // set current user for installation
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("currentUserId", ParseUser.getCurrentUser().getObjectId());
        installation.saveInBackground();
        // redirect to main activity
        final Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
