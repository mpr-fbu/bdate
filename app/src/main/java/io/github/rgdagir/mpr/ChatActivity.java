package io.github.rgdagir.mpr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
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
    private ImageView defaultProfilePic;
    private static TextView notification;
    private static Button btnSend;
    private RecyclerView rvMessages;
    private MessageAdapter mMessageAdapter;
    private ArrayList<Message> mMessages;
    ParseUser currUser;
    ParseUser otherUser;
    public ParseLiveQueryClient parseLiveQueryClient;
    Milestone milestone;
    private boolean sent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setUpToolbar();
        findViews();
        setUpInstanceVariables();
        setUpRecyclerView();
        parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        subscribeToMessages();
        subscribeToConversations();
        displayUsernameAtTop();
        displayProfilePicture();
        setOnClickListeners();
        populateMessages();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Store our shared preference for push notification
        SharedPreferences sp = getSharedPreferences("ACTIVEINFO", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("active", true);
        ed.putString("conversationId", conversation.getObjectId());
        ed.commit();
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

    /* Methods for onCreate */

    private void setUpToolbar() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void findViews() {
        etMessage = findViewById(R.id.etMessage);
        tvUsername = findViewById(R.id.toolbar_title);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        defaultProfilePic = findViewById(R.id.defaultImageView);
        btnSend = findViewById(R.id.btnSend);
        notification = findViewById(R.id.notification);
        rvMessages = findViewById(R.id.rvMessages);
    }

    private void setUpInstanceVariables() {
        conversation = Parcels.unwrap(getIntent().getParcelableExtra("conversation"));
        currUser = ParseUser.getCurrentUser();
        mMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(mMessages, conversation);
        milestone = new Milestone();
        rvMessages.setAdapter(mMessageAdapter);
        notification.setVisibility(View.INVISIBLE);
        notification.setGravity(Gravity.CENTER);
    }

    private void setUpRecyclerView() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(linearLayoutManager);
        rvMessages.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom != oldBottom) {
                    rvMessages.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rvMessages.smoothScrollToPosition(0);
                        }
                    }, 50);
                }
            }
        });
    }

    private void subscribeToMessages() {
        // Make sure the Parse server is setup to configured for live queries
        // URL for server is determined by Parse.initialize() call.
        ParseQuery<Message> messagesQuery = ParseQuery.getQuery(Message.class);
        messagesQuery.whereEqualTo("conversation", conversation);
        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(messagesQuery);
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.ENTER, new
                SubscriptionHandling.HandleEventCallback<Message>() {
                    @Override
                    public void onEvent(ParseQuery<Message> query, Message message) {
                        processNewMessage(message);
                    }
                });
        subscriptionHandling.handleError(new SubscriptionHandling.HandleErrorCallback<Message>() {
            @Override
            public void onError(ParseQuery<Message> query, LiveQueryException exception) {
                Log.d("Live Query", "Callback failed");
            }
        });
    }

    private void processNewMessage(Message message) {
        mMessages.add(0, message);
        Log.e("Message received", message.getText().toString());
        // RecyclerView updates need to be run on the UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageAdapter.notifyDataSetChanged();
                rvMessages.scrollToPosition(0);
            }
        });
        SharedPreferences sp = getSharedPreferences("ACTIVEINFO", MODE_PRIVATE);
        boolean chatActive = sp.getBoolean("active", false);
        String conversationId = sp.getString("conversationId", "");
        if (chatActive && conversationId.equals(conversation.getObjectId())) {
            if (conversation.getUser1().getObjectId().equals(currUser.getObjectId())) {
                conversation.setReadUser1(true);
            } else {
                conversation.setReadUser2(true);
            }
            conversation.saveInBackground();
        }
    }

    private void subscribeToConversations() {
        ParseQuery<Conversation> conversationQuery = ParseQuery.getQuery(Conversation.class);
        conversationQuery.whereEqualTo("objectId", conversation.getObjectId());
        SubscriptionHandling<Conversation> subscriptionHandlingConversations = parseLiveQueryClient.subscribe(conversationQuery);
        subscriptionHandlingConversations.handleEvent(SubscriptionHandling.Event.UPDATE, new
                SubscriptionHandling.HandleEventCallback<Conversation>() {
                    @Override
                    public void onEvent(ParseQuery<Conversation> query, Conversation conv) {
                        int oldExchanges = conversation.getExchanges();
                        conversation = conv;
                        if(conversation.getExchanges() > oldExchanges) {
                            checkNewUnlockedMilestones(conversation);
                        }
                    }
                });
        subscriptionHandlingConversations.handleError(new SubscriptionHandling.HandleErrorCallback<Conversation>() {
            @Override
            public void onError(ParseQuery<Conversation> query, LiveQueryException exception) {
                Log.d("Conversation Live Query", "Callback failed");
            }
        });
    }

    private void displayUsernameAtTop() {
        if (currUser.getObjectId().equals(conversation.getUser1().getObjectId())) {
            otherUser = conversation.getUser2();
            if (Milestone.canSeeName(conversation)) {
                tvUsername.setText(otherUser.getString("firstName"));
            } else {
                try {
                    tvUsername.setText(otherUser.fetchIfNeeded().getString("fakeName"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            otherUser = conversation.getUser1();
            if (Milestone.canSeeName(conversation)) {
                tvUsername.setText(otherUser.getString("firstName") );
            } else {
                tvUsername.setText(otherUser.getString("fakeName"));
            }
        }
    }

    private void displayProfilePicture() {
        if (Milestone.canSeeProfilePicture(conversation)) {
            displayActualProfilePicture();
            ivProfilePic.setVisibility(View.VISIBLE);
            defaultProfilePic.setVisibility(View.INVISIBLE);
        } else {
            ivProfilePic.setVisibility(View.INVISIBLE);
            defaultProfilePic.setVisibility(View.VISIBLE);
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

    private void populateMessages() {
        final ParseQuery<Message> messagesQuery = new Message.Query();
        messagesQuery.include("sender").whereEqualTo("conversation", conversation);
        messagesQuery.addDescendingOrder("createdAt");
        if (conversation.getUser1().getObjectId().equals(currUser.getObjectId())) {
            conversation.setReadUser1(true);
        } else {
            conversation.setReadUser2(true);
        }
        conversation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                messagesQuery.findInBackground(new FindCallback<Message>() {
                    @Override
                    public void done(List<Message> objects, ParseException e) {
                        if (e == null) {
                            for (int i = 0; i < objects.size(); ++i) {
                                Message message = objects.get(i);
                                mMessages.add(message);
                                mMessageAdapter.notifyItemInserted(mMessages.size() - 1);
                                Log.d("Messages", "a message has been loaded!");
                                rvMessages.scrollToPosition(0);
                            }
                        } else {
                            Log.d("ChatActivity", "Error querying for messages" + e);
                        }
                    }
                });
            }
        });
    }

    /* Helpers for other methods */

    private void checkNewUnlockedMilestones(final Conversation conversation) {
        if (Milestone.canSeeGallery(conversation)) {
            milestone.showNotification(conversation);
            // show distance away in both user profiles
        } else if (Milestone.canSeeProfilePicture(conversation)) {
            milestone.showNotification(conversation);
            // also need to update chatList adapter to show pro pics properly
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayActualProfilePicture();
                    mMessageAdapter.setConversation(conversation);
                    mMessageAdapter.notifyDataSetChanged();
                }
            });
        } else if (Milestone.canSeeOccupation(conversation)) {
            milestone.showNotification(conversation);
            // show distance away in both user profiles
        } else if (Milestone.canSeeDistanceAway(conversation)) {
            milestone.showNotification(conversation);
            // show distance away in both user profiles
        } else if (Milestone.canSeeAge(conversation)) {
            milestone.showNotification(conversation);
            // show age in both user profiles
        } else if (Milestone.canSeeName(conversation) && Milestone.canSeeGender(conversation)) {
            milestone.showNotification(conversation);
            // also need to update chatList adapter to show name properly
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvUsername.setText(otherUser.getString("firstName"));
                }
            });
        }
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
            ivProfilePic.setVisibility(View.VISIBLE);
            defaultProfilePic.setVisibility(View.INVISIBLE);
        } catch (ParseException e) {
            Log.e("ChatActivity", "Error displaying actual profile picture");
            e.printStackTrace();
        }
    }

    public void showTextViewNotification(String milestone) {
        switch (milestone) {
            case "name and gender":
                animateTextView(R.string.notification_name_and_gender);
                return;
            case "age":
                animateTextView(R.string.notification_age);
                return;
            case "distance away":
                animateTextView(R.string.notification_distance_away);
                return;
            case "occupation":
                animateTextView(R.string.notification_occupation);
                return;
            case "profile picture":
                animateTextView(R.string.notification_profile_pic);
                return;
            case "gallery":
                animateTextView(R.string.notification_gallery);
                return;
        }
    }

    private void animateTextView(final int unlockMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notification.setText(unlockMessage);
                notification.setBackgroundResource(R.color.oceanBlue);
                notification.setVisibility(View.VISIBLE);
                notification.postDelayed(new Runnable() {
                    public void run() {
                        AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f );
                        fadeOut.setDuration(1200);
                        notification.startAnimation(fadeOut);
                        notification.setVisibility(View.INVISIBLE);
                    }
                }, 3000);
            }
        });
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

    private void sendMessage() {
        updateExchanges();
        if (mMessages.size() != 0) {
            if (!mMessages.get(0).getSender().getObjectId().equals(currUser.getObjectId())) {
                checkNewUnlockedMilestones(conversation);
            }
        }
        final Message newMessage = new Message();
        String messageText = etMessage.getText().toString();

        newMessage.setSender(currUser);
        newMessage.setConversation(conversation);
        newMessage.setText(messageText);
        conversation.setLastMessage(newMessage);
        if (conversation.getUser1().getObjectId().equals(currUser.getObjectId())) {
            conversation.setReadUser1(true);
            conversation.setReadUser2(false);
        } else {
            conversation.setReadUser1(false);
            conversation.setReadUser2(true);
        }
        newMessage.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("ChatActivity", "Sending message success!");
                    // send push notification to other user
                    sendMessagePushNotification();
                } else {
                    Log.e("ChatActivity", "Sending message failed :(");
                    e.printStackTrace();
                    // retry sending message
                    sent = false;
                    int retries = 0;
                    while (retrySending(newMessage) | retries == 5){
                        retries++;
                    }
                }
            }
        });
        etMessage.setText(null);
    }

    public boolean retrySending(Message message){
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    sent = true;
                    Log.d("ChatActivity", "Retrial was successful!");
                    // send push notification to other user
                    sendMessagePushNotification();
                } else {
                    sent = false;
                    Log.e("ChatActivity", "Retrial failed :(");
                    e.printStackTrace();
                }
            }
        });
        return sent;
    }

    private void sendMessagePushNotification() {
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

}
