package io.github.rgdagir.mpr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.parse.ParseUser;

import org.parceler.Parcels;

import io.github.rgdagir.mpr.models.Conversation;

public class ChatActivity extends AppCompatActivity {

    Conversation conversation;
    private TextView tvUsername;
    ParseUser currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        conversation = Parcels.unwrap(getIntent().getParcelableExtra("conversation"));
        tvUsername = findViewById(R.id.tvUsername);
        currUser = ParseUser.getCurrentUser();
        if (currUser.getObjectId().equals(conversation.getUser1().getObjectId())) {
            tvUsername.setText(conversation.getUser2().getUsername());
        } else {
            tvUsername.setText(conversation.getUser1().getUsername());
        }

        // Then do querying and stuff and actually get the messages

    }
}
