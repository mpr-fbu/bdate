package io.github.rgdagir.mpr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    private static String ACTIVITY_TAG = "SIGN UP";
    private EditText etFirstNameSignUp;
    private EditText etLastNameSignUp;
    private EditText etEmailSignUp;
    private EditText etPasswordSignUp;
    private Button signupBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etFirstNameSignUp = findViewById(R.id.firstNameSignUp);
        etLastNameSignUp = findViewById(R.id.lastNameSignUp);
        etEmailSignUp = findViewById(R.id.emailSignUp);
        etPasswordSignUp = findViewById(R.id.passwordSignUp);
        signupBtn = findViewById(R.id.signUpBtn);

        // Create the ParseUser
        final ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(etEmailSignUp.toString());
        user.setPassword(etPasswordSignUp.toString());
        user.setEmail(etEmailSignUp.toString());
        // Set custom properties
        user.put("firstName", etFirstNameSignUp.toString());
        user.put("lastName", etLastNameSignUp.toString());
        // Invoke signUpInBackground when user clicks the button
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Hooray! Let them use the app now.
                            Log.d(ACTIVITY_TAG, "Success!");
                        } else {
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
                            Log.d(ACTIVITY_TAG, "Failed :(");
                        }
                    }
                });
            }
        });
    }
}
