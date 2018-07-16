package io.github.rgdagir.mpr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private static String ACTIVITY_TAG = "LOGIN";
    private EditText etLoginEmail;
    private EditText etLoginPassword;
    private Button loginBtn;
    private Button toSignUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLoginEmail = findViewById(R.id.loginEmail);
        etLoginPassword = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginButton);
        toSignUpBtn = findViewById(R.id.toSignUpButton);

        final ParseUser currentUser = ParseUser.getCurrentUser();

        toSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    Intent toSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(toSignUp);
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logInInBackground(etLoginEmail.getText().toString(), etLoginPassword.getText().toString(), new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            // Launch app
                            Log.d(ACTIVITY_TAG, "Login success!");
                        } else {
                            // Signup failed. Look at the ParseException to see what happened.
                            Log.d(ACTIVITY_TAG, "Login failed :(");
                        }
                    }
                });
            }
        });


    }


}
