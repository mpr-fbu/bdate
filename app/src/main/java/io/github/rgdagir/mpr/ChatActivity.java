package io.github.rgdagir.mpr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LiveQueryException;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SubscriptionHandling;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.rgdagir.mpr.models.Conversation;
import io.github.rgdagir.mpr.models.Message;
import io.github.rgdagir.mpr.models.Milestone;

public class ChatActivity extends AppCompatActivity {

    Conversation conversation;
    private EditText etMessage;
    private TextView tvUsername;
    private ParseImageView ivProfilePic;
    private static Button btnSend;
    private RecyclerView rvMessages;
    private MessageAdapter mMessageAdapter;
    private ArrayList<Message> mMessages;
    ParseUser currUser;
    ParseUser otherUser;
    ParseLiveQueryClient parseLiveQueryClient;
    private boolean profileNotificationShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setUpToolbar();
        findViews();
        setUpInstanceVariables();
        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        subscribeToMessages();
        subscribeToConversations();
        setUpRecyclerView();
        displayUsernameAtTop();
        displayProfilePicture();
        setOnClickListeners();
        populateMessages();
        rvMessages.scrollToPosition(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Store our shared preference
        SharedPreferences sp = getSharedPreferences("ACTIVEINFO", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", true);
        ed.commit();
    }

    private void setUpToolbar() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.chat_option_one:
                Toast.makeText(this, "This doesn't do anything lmao", Toast.LENGTH_LONG).show();
                return true;
            case R.id.chat_option_two:
                Toast.makeText(this, "You unmatched </3", Toast.LENGTH_LONG).show();
                unmatch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpRecyclerView() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
        rvMessages.setLayoutManager(linearLayoutManager);
        rvMessages.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
            {
                rvMessages.scrollToPosition(0);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Store our shared preference
        SharedPreferences sp = getSharedPreferences("ACTIVEINFO", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", false);
        ed.commit();
    }

    private void findViews() {
        etMessage = findViewById(R.id.etMessage);
        tvUsername = findViewById(R.id.toolbar_title);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        btnSend = findViewById(R.id.btnSend);
        rvMessages = findViewById(R.id.rvMessages);
    }

    private void setUpInstanceVariables() {
        conversation = Parcels.unwrap(getIntent().getParcelableExtra("conversation"));
        currUser = ParseUser.getCurrentUser();
        mMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(mMessages);
        rvMessages.setAdapter(mMessageAdapter);
    }

    private void subscribeToMessages() {
        // Make sure the Parse server is setup to configured for live queries
        // URL for server is determined by Parse.initialize() call.

        ParseQuery<Message> messagesQuery = ParseQuery.getQuery(Message.class);
        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(messagesQuery);
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new
                SubscriptionHandling.HandleEventCallback<Message>() {
                    @Override
                    public void onEvent(ParseQuery<Message> query, Message object) {
                        mMessages.add(0, object);
                        // RecyclerView updates need to be run on the UI thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMessageAdapter.notifyDataSetChanged();
                                rvMessages.scrollToPosition(0);
                            }
                        });
                    }
                });
        subscriptionHandling.handleError(new SubscriptionHandling.HandleErrorCallback<Message>() {
            @Override
            public void onError(ParseQuery<Message> query, LiveQueryException exception) {
                Log.d("Live Query", "Callback failed");
            }
        });
    }

    private void subscribeToConversations() {
        ParseQuery<Conversation> conversationQuery = ParseQuery.getQuery(Conversation.class);
        conversationQuery.whereEqualTo("objectId", conversation.getObjectId());
        SubscriptionHandling<Conversation> subscriptionHandlingConversations = parseLiveQueryClient.subscribe(conversationQuery);
        subscriptionHandlingConversations.handleEvent(SubscriptionHandling.Event.UPDATE, new
                SubscriptionHandling.HandleEventCallback<Conversation>() {
                    @Override
                    public void onEvent(ParseQuery<Conversation> query, Conversation conv) {
                        conversation = conv;
                        checkNewUnlockedMilestones(conversation);
                    }
                });
        subscriptionHandlingConversations.handleError(new SubscriptionHandling.HandleErrorCallback<Conversation>() {
            @Override
            public void onError(ParseQuery<Conversation> query, LiveQueryException exception) {
                Log.d("Conversation Live Query", "Callback failed");
            }
        });
    }

    private void checkNewUnlockedMilestones(Conversation conversation) {
        if (Milestone.canSeeProfilePicture(conversation)) {
            //also need to update chatlist adapter to show pro pics properly
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayActualProfilePicture();
                }
            });
        } else if (Milestone.canSeeDistanceAway(conversation)) {
            //show distance away in both user profiles
        } else if (Milestone.canSeeAge(conversation)) {
            //show age in both user profiles
        } else if (Milestone.canSeeName(conversation)) {
            //also need to update chatlist adapter to show name properly
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvUsername.setText(otherUser.getString("firstName") + " " + otherUser.getString("lastName"));
                }
            });
        }
    }

    private void displayUsernameAtTop() {
        if (currUser.getObjectId().equals(conversation.getUser1().getObjectId())) {
            otherUser = conversation.getUser2();
            try {
                tvUsername.setText(otherUser.fetchIfNeeded().getUsername());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            otherUser = conversation.getUser1();
            tvUsername.setText(otherUser.getUsername());
        }
    }

    private void displayProfilePicture() {
        if (Milestone.canSeeProfilePicture(conversation)) {
            displayActualProfilePicture();
        } else {
            displayDefaultProfilePicture();
        }
    }

    private void displayDefaultProfilePicture() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", "Bhg8VXqMbu");
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser defaultUser, ParseException e) {
                Glide.with(getApplicationContext()).load(defaultUser.getParseFile("profilePic").getUrl())
                        .asBitmap().centerCrop().dontAnimate()
                        .placeholder(R.drawable.ic_action_name)
                        .error(R.drawable.ic_action_name)
                        .into(new BitmapImageViewTarget(ivProfilePic) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                ivProfilePic.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            }
        });
    }

    private void displayActualProfilePicture() {
        if (currUser.getObjectId().equals(conversation.getUser1().getObjectId())) {
            otherUser = conversation.getUser2();
        } else {
            otherUser = conversation.getUser1();
        }
        try {
            Glide.with(getApplicationContext()).load(otherUser.fetchIfNeeded().getParseFile("profilePic").getUrl())
                    .asBitmap().centerCrop().dontAnimate()
                    .placeholder(R.drawable.ic_action_name)
                    .error(R.drawable.ic_action_name)
                    .into(new BitmapImageViewTarget(ivProfilePic) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            ivProfilePic.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        } catch (ParseException e) {
            Log.e("ChatActivity", "Error displaying actual profile picture");
            e.printStackTrace();
        }
    }

    private void setOnClickListeners() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMessage.getText().toString().length() > 0) {
                    sendMessage();
                }
            }
        });
    }

    private void sendMessage() {
        updateExchanges();
        final Message newMessage = new Message();
        String messageText = etMessage.getText().toString();

        newMessage.setSender(currUser);
        newMessage.setConversation(conversation);
        newMessage.setText(messageText);
        conversation.setLastMessage(newMessage);

        newMessage.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("ChatActivity", "Sending message success!");
                    // send push notification to other user
                    HashMap<String, String> payload = new HashMap<>();
                    ParseUser recipient;
                    if (conversation.getUser1().getObjectId().equals(currUser.getObjectId())) {
                        recipient = conversation.getUser2();
                    } else {
                        recipient = conversation.getUser1();
                    }
                    payload.put("receiver", recipient.getObjectId());
                    payload.put("newData", getString(R.string.new_message_notification));
                    ParseCloud.callFunctionInBackground("pushNotificationGeneral", payload);
                } else {
                    Log.e("ChatActivity", "Sending message failed :(");
                    e.printStackTrace();
                }
            }
        });
        etMessage.setText(null);
    }

    private void updateExchanges() {
        Integer exchanges = conversation.getExchanges();
        if (!mMessages.isEmpty()) {
            if (!currUser.getObjectId().equals(mMessages.get(0).getSender().getObjectId())) {
                conversation.setExchanges(exchanges + 1);
                conversation.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("ChatActivity", "exchange updated");
                        } else {
                            Log.e("ChatActivity", "exchange failed to update");
                        }
                    }
                });
            }
        }
    }

    private void populateMessages() {
        ParseQuery<Message> messagesQuery = new Message.Query();
        messagesQuery.include("sender").whereEqualTo("conversation", conversation);
        messagesQuery.addDescendingOrder("createdAt");
        messagesQuery.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); ++i) {
                        Message message = objects.get(i);
                        mMessages.add(message);
                        mMessageAdapter.notifyItemInserted(mMessages.size() - 1);
                        Log.d("Messages", "a message has been loaded!");
                    }
                } else {
                    Log.d("ChatActivity", "Error querying for messages" + e);
                }
            }
        });
    }

    public static void showSnackbar(String milestone) {
        switch (milestone) {
            case "name":
                Snackbar.make(btnSend, R.string.snackbar_name, Snackbar.LENGTH_LONG)
                        .show();
                return;
            case "age":
                Snackbar.make(btnSend, R.string.snackbar_age, Snackbar.LENGTH_LONG)
                        .show();
                return;
            case "distance away":
                Snackbar.make(btnSend, R.string.snackbar_distance_away, Snackbar.LENGTH_LONG)
                        .show();
                return;
            case "profile picture":
                Snackbar.make(btnSend, R.string.snackbar_profile_pic, Snackbar.LENGTH_LONG)
                        .show();
                return;
        }
    }

    private void unmatch() {
        conversation.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                Intent intent =new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
