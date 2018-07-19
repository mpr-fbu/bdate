package io.github.rgdagir.mpr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

import org.parceler.Parcels;

import io.github.rgdagir.mpr.models.Conversation;

public class ChatActivity extends AppCompatActivity {

    Conversation conversation;
    private TextView tvUsername;
    private Button btnReturn;
    private RecyclerView rvMessages;
    ParseUser currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        conversation = Parcels.unwrap(getIntent().getParcelableExtra("conversation"));
        tvUsername = findViewById(R.id.tvUsername);
        btnReturn = findViewById(R.id.btnReturn);

        currUser = ParseUser.getCurrentUser();
        if (currUser.getObjectId().equals(conversation.getUser1().getObjectId())) {
            tvUsername.setText(conversation.getUser2().getUsername());
        } else {
            tvUsername.setText(conversation.getUser1().getUsername());
        }

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Then do querying and stuff and actually get the messages

    }
}
