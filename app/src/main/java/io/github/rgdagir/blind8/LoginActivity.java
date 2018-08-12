package io.github.rgdagir.blind8;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import io.github.rgdagir.blind8.models.Conversation;
import io.github.rgdagir.blind8.models.Message;
import io.github.rgdagir.blind8.sign_up.SignUpActivity;

public class LoginActivity extends AppCompatActivity {

    private static String ACTIVITY_TAG = "LOGIN";
    private ImageView appName;
    private ImageView logo;
    private TextView termsAndPrivacy;
    private TextView slogan;
    private EditText etLoginEmail;
    private EditText etLoginPassword;
    private Button loginBtn;
    private Button toSignUpBtn;
    private Button autoFill;
    private Context context;
    private ProgressBar progressBar = null;
    private Conversation dateConvo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        final ParseUser currentUser = ParseUser.getCurrentUser();
        findViews();
        setOnClickListeners();

        if (currentUser != null) {
            // persistence if the user is already signed in
            launchMainActivity();
        }

    }

    private void findViews() {
        progressBar = findViewById(R.id.loginProgressBar);
        termsAndPrivacy = findViewById(R.id.termsAndPrivacy);
        slogan = findViewById(R.id.slogan);
        appName = findViewById(R.id.appName);
        logo = findViewById(R.id.logo);
        etLoginEmail = findViewById(R.id.loginEmail);
        etLoginPassword = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginButton);
        toSignUpBtn = findViewById(R.id.toSignUpButton);
        autoFill = findViewById(R.id.autoFill);
        logo = findViewById(R.id.logo);
    }

    private void setOnClickListeners() {
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
                            e.printStackTrace();
                            Toast.makeText(context, "Login failed, please try again.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        autoFill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etLoginEmail.setText("test@gmail.com");
                etLoginPassword.setText("asdf");
            }
        });

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDateConversation();
            }
        });
    }

    private void resetDateConversation() {
        // reset last message and exchange count in conversation to go on a date
        ParseQuery<Conversation> query2 = ParseQuery.getQuery("Conversation");
        query2.whereEqualTo("objectId", "jvpmHiCipP");
        query2.whereGreaterThan("exchanges", 23);
        query2.findInBackground(new FindCallback<Conversation>() {
            public void done(List<Conversation> conversations, ParseException e) {
                if (e == null) {
                    if (conversations.size() > 0) {
                        dateConvo = conversations.get(0);
                        Message lastMessage = dateConvo.getLastMessage();
                        lastMessage.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                Toast.makeText(context, "Raul's last message deleted successfully!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        ParseQuery<Message> messageQuery = ParseQuery.getQuery("Message");
                        messageQuery.whereEqualTo("objectId", "okeGyHAGrw");
                        messageQuery.findInBackground(new FindCallback<Message>() {
                            @Override
                            public void done(List<Message> objects, ParseException e) {
                                Message message = objects.get(0);
                                dateConvo.setLastMessage(message);
                                dateConvo.setExchanges(23);
                                dateConvo.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Toast.makeText(context, "Sarah's last message reset successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
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
        final Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }
}
