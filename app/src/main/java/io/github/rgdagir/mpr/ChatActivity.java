package io.github.rgdagir.mpr;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.LiveQueryException;
import com.parse.ParseException;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SubscriptionHandling;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import io.github.rgdagir.mpr.models.Conversation;
import io.github.rgdagir.mpr.models.Message;
import io.github.rgdagir.mpr.models.Milestone;

public class ChatActivity extends AppCompatActivity {

    Conversation conversation;
    private EditText etMessage;
    private TextView tvUsername;
    private Button btnReturn;
    private static Button btnSend;
    private RecyclerView rvMessages;
    private MessageAdapter mMessageAdapter;
    private ArrayList<Message> mMessages;
    ParseUser currUser;
    ParseUser otherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setUpToolbar();

        findViews();
        setUpInstanceVariables();
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        subscribeToMessages(parseLiveQueryClient);
        subscribeToConversations(parseLiveQueryClient);

        // set up recycler view for messages
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
        rvMessages.setLayoutManager(linearLayoutManager);

        displayUsernameAtTop();
        setOnClickListeners();

        // initially populate chat with past messages
        populateMessages();
        rvMessages.scrollToPosition(0);
    }

    private void setUpToolbar() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void findViews() {
        etMessage = findViewById(R.id.etMessage);
        tvUsername = findViewById(R.id.toolbar_title);
        btnReturn = findViewById(R.id.btnReturn);
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

    private void subscribeToMessages(ParseLiveQueryClient parseLiveQueryClient) {
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

    private void subscribeToConversations(ParseLiveQueryClient parseLiveQueryClient) {
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
        if (Milestone.canSeeDistanceAway(conversation)) {
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

    private void setOnClickListeners() {
        // back button to return to chat list
//        btnReturn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });

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
                } else {
                    Log.e("ChatActivity", "Sending message failed :(");
                    e.printStackTrace();
                }
            }
        });
        etMessage.setText(null);
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
        }
    }
}
