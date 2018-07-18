package io.github.rgdagir.mpr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.parse.ParseUser;

import org.parceler.Parcels;

import io.github.rgdagir.mpr.models.Conversation;

public class ChatActivity extends AppCompatActivity {

    Conversation convo;
    private TextView tvUsername;
    ParseUser currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        convo = Parcels.unwrap(getIntent().getParcelableExtra("conversation"));
        tvUsername = findViewById(R.id.tvUsername);
        currUser = ParseUser.getCurrentUser();
        if (currUser.getObjectId().equals(convo.getUser1().getObjectId())) {
            tvUsername.setText(convo.getUser2().getUsername());
        } else {
            tvUsername.setText(convo.getUser1().getUsername());
        }

        // Then do querying and stuff and actually get the messages

    }
}
