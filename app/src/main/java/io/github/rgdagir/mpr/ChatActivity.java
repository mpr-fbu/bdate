package io.github.rgdagir.mpr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class ChatActivity extends AppCompatActivity {

    Conversation conversation;
    private EditText etMessage;
    private TextView tvUsername;
    private Button btnReturn;
    private Button btnSend;
    private RecyclerView rvMessages;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messages;
    ParseUser currUser;
    ParseUser otherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        conversation = Parcels.unwrap(getIntent().getParcelableExtra("conversation"));
        etMessage = findViewById(R.id.etMessage);
        tvUsername = findViewById(R.id.tvUsername);
        btnReturn = findViewById(R.id.btnReturn);
        btnSend = findViewById(R.id.btnSend);
        rvMessages = findViewById(R.id.rvMessages);
        currUser = ParseUser.getCurrentUser();
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages);
        rvMessages.setAdapter(messageAdapter);

        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        ParseQuery<Message> parseQuery = new Message.Query();
        //parseQuery.whereEqualTo("conversation", conversation);
        SubscriptionHandling<Message> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
        Log.e("Live Query", "Query");
        // Listen for CREATE events
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, new
                SubscriptionHandling.HandleEventCallback<Message>() {
                    @Override
                    public void onEvent(ParseQuery<Message> query, Message object) {
                        Log.d("TestLive", "Callback");
                        try {
                            object.fetchInBackground();
                            messages.add(0, object);
                            // RecyclerView updates need to be run on the UI thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("TestLive", "Notifying Adapter...");
                                    messageAdapter.notifyItemInserted(0);
                                    Log.d("TestLive", "Scrolling to position...");
                                    rvMessages.scrollToPosition(0);
                                    Log.d("TestLive", "Scrolled");
                                }});
                        } catch (Error e){
                            e.printStackTrace();
                        }
                    }
                });
        subscriptionHandling.handleError(new
                                                 SubscriptionHandling.HandleErrorCallback<Message>() {
                                                     @Override
                                                     public void onError(ParseQuery<Message> query, LiveQueryException exception) {
                                                         Log.d("Live Query", "Callback failed");
                                                     }
                                                 });


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
        rvMessages.setLayoutManager(linearLayoutManager);

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

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // Then do querying and stuff and actually get the messages
        populateMessages();
    }

    private void sendMessage() {
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
                    messages.add(0, newMessage);
                    // RecyclerView updates need to be run on the UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageAdapter.notifyDataSetChanged();
                            rvMessages.scrollToPosition(0);
                        }
                    });
                } else {
                    Log.e("ChatActivity", "Sending message failed :(");
                    e.printStackTrace();
                }
            }
        });
        etMessage.setText(null);
    }

    private void populateMessages() {
        final ParseQuery<Message> messagesQuery = new Message.Query();
        messagesQuery.include("sender").whereEqualTo("conversation", conversation);
        messagesQuery.addDescendingOrder("createdAt");

        messagesQuery.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); ++i) {
                        Message message = objects.get(i);
                        messages.add(message);
                        messageAdapter.notifyItemInserted(messages.size() - 1);
                        Log.d("Messages", "a message has been loaded!");
                    }
                } else {
                    Log.d("ChatActivity", "Error querying for messages");
                }
            }
        });
        rvMessages.scrollToPosition(0);
    }


//    private int getNumberOfMessagesSentBy(ParseUser sender) {
//        final int numberOfMessages;
//        ParseQuery<Message> fetchNumberOfMessages = new Message.Query();
//        fetchNumberOfMessages.whereEqualTo("sender", sender.getUsername())
//                .whereEqualTo("conversation", conversation);
//        fetchNumberOfMessages.findInBackground(new FindCallback<Message>() {
//            @Override
//            public void done(List<Message> objects, ParseException e) {
//                numberOfMessages = objects.size();
//            }
//        });
//        return numberOfMessages;
//    }
}
