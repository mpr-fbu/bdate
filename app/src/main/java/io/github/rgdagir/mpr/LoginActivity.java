package io.github.rgdagir.mpr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private static String ACTIVITY_TAG = "LOGIN";
    private EditText etLoginEmail;
    private EditText etLoginPassword;
    private Button loginBtn;
    private Button toSignUpBtn;
    private Context context;
    private ProgressBar progressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        progressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
        etLoginEmail = findViewById(R.id.loginEmail);
        etLoginPassword = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginButton);
        toSignUpBtn = findViewById(R.id.toSignUpButton);

        final ParseUser currentUser = ParseUser.getCurrentUser();

        toSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(toSignUp);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                ParseUser.logInInBackground(etLoginEmail.getText().toString(), etLoginPassword.getText().toString(), new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            progressBar.setVisibility(View.INVISIBLE);
                            launchMainActivity();
                        } else {
                            // Sign in failed. Look at the ParseException to see what happened.
                            Log.d(ACTIVITY_TAG, "Login failed :(");
                            Toast.makeText(context, "Login failed, please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        if (currentUser != null) {
            // persistence if the user is already signed in
            launchMainActivity();
        }

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
        final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
