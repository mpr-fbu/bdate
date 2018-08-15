package io.github.rgdagir.blind8;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.plattysoft.leonids.ParticleSystem;

import org.parceler.Parcels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.github.rgdagir.blind8.models.Conversation;
import io.github.rgdagir.blind8.models.Message;
import io.github.rgdagir.blind8.models.Milestone;

public class ChatActivity extends AppCompatActivity {

    Conversation conversation;
    private EditText etMessage;
    private TextView tvUsername;
    private ParseImageView ivProfilePic;
    private ImageView defaultProfilePic;
    private ImageView heartLogo;
    private static TextView notification;
    private static ImageButton btnSend;
    private RecyclerView rvMessages;
    private MessageAdapter mMessageAdapter;
    private ArrayList<Message> mMessages;
    ParseUser currUser;
    ParseUser otherUser;
    public ParseLiveQueryClient parseLiveQueryClient;
    Milestone milestone;
    private boolean sent;
    private boolean isUpdated;
    private boolean animation;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.shadowBlue));
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
        //showAnimation(animation);
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
    public void onBackPressed() {
        final Intent intent = new Intent(ChatActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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
        heartLogo = findViewById(R.id.heartLogo);
        defaultProfilePic = findViewById(R.id.defaultImageView);
        btnSend = findViewById(R.id.btnSend);
        notification = findViewById(R.id.notification);
        rvMessages = findViewById(R.id.rvMessages);
    }

    private void setUpInstanceVariables() {
        conversation = Parcels.unwrap(getIntent().getParcelableExtra("conversation"));
        //animation = Parcels.unwrap(getIntent().getParcelableExtra("animation"));
        currUser = ParseUser.getCurrentUser();
        mMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(mMessages, conversation);
        milestone = new Milestone();
        rvMessages.setAdapter(mMessageAdapter);
        notification.setVisibility(View.INVISIBLE);
        notification.setGravity(Gravity.CENTER);
        isUpdated = true;
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
        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUpdated) {
                    Intent intent = new Intent(ChatActivity.this, HolderActivity.class);
                    intent.putExtra("conversation", (Serializable) conversation);
                    startActivity(intent);
                }
            }
        });
        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUpdated) {
                    Intent intent = new Intent(ChatActivity.this, HolderActivity.class);
                    intent.putExtra("conversation", (Serializable) conversation);
                    startActivity(intent);
                }
            }
        });
        defaultProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUpdated) {
                    Intent intent = new Intent(ChatActivity.this, HolderActivity.class);
                    intent.putExtra("conversation", (Serializable) conversation);
                    startActivity(intent);
                }
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, HolderActivity.class);
                intent.putExtra("conversation", (Serializable) conversation);
                startActivity(intent);
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

//    private void showAnimation(boolean animation) {
//        if (animation) {
//            heartLogo.setVisibility(View.VISIBLE);
//            pulseAnimation(heartLogo);
//        }
//    }

//    public void pulseAnimation(View heartLogo) {
//        ObjectAnimator objAnim= ObjectAnimator.ofPropertyValuesHolder(heartLogo, PropertyValuesHolder.ofFloat("scaleX", 1.5f), PropertyValuesHolder.ofFloat("scaleY", 1.5f));
//        objAnim.setDuration(300);
//        objAnim.setRepeatMode(ObjectAnimator.REVERSE);
//        objAnim.setRepeatCount(3);
//        objAnim.start();
//        heartLogo.setVisibility(View.INVISIBLE);
//    }

    /* Helpers for other methods */

    private void checkNewUnlockedMilestones(final Conversation conversation) {
        if (Milestone.canGoOnDate(conversation)) {
            milestone.showNotification(conversation, this);
        } else if (Milestone.canSeeGallery(conversation)) {
            milestone.showNotification(conversation, this);
        } else if (Milestone.canSeeAge(conversation)) {
            milestone.showNotification(conversation, this);
        } else if (Milestone.canSeeOccupation(conversation)) {
            milestone.showNotification(conversation, this);
        } else if (Milestone.canSeeDistanceAway(conversation)) {
            milestone.showNotification(conversation, this);
        } else if (Milestone.canSeeProfilePicture(conversation)) {
            milestone.showNotification(conversation, this);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayActualProfilePicture();
                    mMessageAdapter.setConversation(conversation);
                    mMessageAdapter.notifyDataSetChanged();
                }
            });
        } else if (Milestone.canSeeName(conversation)) {
            milestone.showNotification(conversation, this);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvUsername.setText(otherUser.getString("firstName"));
                }
            });
        } else if (Milestone.canSeeInterests(conversation)) {
            milestone.showNotification(conversation, this);
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
                    .placeholder(R.mipmap.ic_picture)
                    .error(R.mipmap.ic_picture)
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

    public void showTextViewNotification(String milestone, Context context) {
        switch (milestone) {
            case "interests":
                animateTextView(R.string.notification_interests);
                playUnlockSound(context);
                return;
            case "name":
                animateTextView(R.string.notification_name);
                playUnlockSound(context);
                return;
            case "age":
                animateTextView(R.string.notification_age);
                playUnlockSound(context);
                return;
            case "distance away":
                animateTextView(R.string.notification_distance_away);
                playUnlockSound(context);
                return;
            case "occupation":
                animateTextView(R.string.notification_occupation);
                playUnlockSound(context);
                return;
            case "profile picture":
                animateTextView(R.string.notification_profile_pic);
                playUnlockSound(context);
                return;
            case "gallery":
                animateTextView(R.string.notification_gallery);
                playUnlockSound(context);
                return;
            case "date":
                animateTextView(R.string.notification_date);
                playDateSoundAnimation(context);
                return;
        }
    }

    private void animateTextView(final int unlockMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notification.setText(unlockMessage);
                notification.setBackgroundResource(R.color.oceanBlue);
                if (unlockMessage == R.string.notification_date) {
                    notification.setBackgroundResource(R.color.pastelPink);
                }
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

    public void playUnlockSound(final Context context) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mediaPlayer = MediaPlayer.create(context, R.raw.quite_impressed);
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(onCompletionListener);
            }
        });
    }

    public void playDateSoundAnimation(final Context context) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mediaPlayer = MediaPlayer.create(context, R.raw.cw_final_cut);
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(onCompletionListener);
                new ParticleSystem((Activity) context, 20, R.drawable.small_heart, 1000)
                        .setSpeedByComponentsRange(0f, 0f, 0.05f, 0.1f)
                        .setAcceleration(0.00005f, 90)
                        .emitWithGravity(((Activity) context).findViewById(R.id.notification), Gravity.BOTTOM,
                                4, 3000);
            }
        });
    }

    public MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    };

    private void unmatch() {
        ParseQuery<Message> messageQuery = ParseQuery.getQuery("Message");
        messageQuery.whereEqualTo("conversation", conversation);
        messageQuery.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                for (Message message : objects) {
                    message.deleteInBackground();
                }
            }
        });
        conversation.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
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
        conversation.setLastMessageTime(new Date());
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
                isUpdated = false;
                conversation.setExchanges(exchanges + 1);
                conversation.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("ChatActivity", "exchange updated");
                            isUpdated = true;
                        } else {
                            Log.e("ChatActivity", "exchange failed to update");
                        }
                    }
                });
            }
        }
    }

}
