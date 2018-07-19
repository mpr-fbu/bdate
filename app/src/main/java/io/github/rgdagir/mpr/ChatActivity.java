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
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
        rvMessages.setLayoutManager(linearLayoutManager);

        if (currUser.getObjectId().equals(conversation.getUser1().getObjectId())) {
            tvUsername.setText(conversation.getUser2().getUsername());
            otherUser = conversation.getUser2();
        } else {
            tvUsername.setText(conversation.getUser1().getUsername());
            otherUser = conversation.getUser1();
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
                    messages.add(newMessage);
                    messageAdapter.notifyItemInserted(0);
                } else {
                    Log.e("ChatActivity", "Sending message failed :(");
                }
            }
        });
        etMessage.setText(null);
    }

    private void populateMessages() {
        final ParseQuery<Message> messagesQuery = new Message.Query();
        messagesQuery.whereEqualTo("conversation", conversation);
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
    }


    private int getNumberOfMessagesSentBy(ParseUser sender) {
        final int numberOfMessages;
        ParseQuery<Message> fetchNumberOfMessages = new Message.Query();
        fetchNumberOfMessages.whereEqualTo("sender", sender.getUsername())
                .whereEqualTo("conversation", conversation);
        fetchNumberOfMessages.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                numberOfMessages = objects.size();
            }
        });
        return numberOfMessages;
    }

    

}
