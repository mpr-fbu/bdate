package io.github.rgdagir.mpr;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseImageView;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

import io.github.rgdagir.mpr.models.Conversation;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    List<Conversation> mConversations;
    Context context;

    public ConversationAdapter(List<Conversation> conversations) {
        mConversations = conversations;
    }

    // for each row, inflate layout and cache refs into ViewHolder
    @NonNull
    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View conversationView = inflater.inflate(R.layout.item_conversation, parent, false);
        ConversationAdapter.ViewHolder viewHolder = new ConversationAdapter.ViewHolder(conversationView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationAdapter.ViewHolder holder, int position) {
        // get data according to position
        final Conversation conversation = mConversations.get(position);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.getObjectId().equals(conversation.getUser1().getObjectId())) {
            setConversationDetails(conversation.getUser2(), holder.tvUsername, holder.ivProfilePic);
        } else {
            setConversationDetails(conversation.getUser1(), holder.tvUsername, holder.ivProfilePic);
        }

        if (conversation.getLastMessage() == null) {
            holder.tvText.setText("No messages yet! Start talking...");
            holder.tvTimestamp.setText("");
        } else {
            holder.tvText.setText(conversation.getLastMessage().getText());
            holder.tvTimestamp.setText(conversation.getTimestamp());
        }
    }

    @Override
    public int getItemCount() { return mConversations.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ParseImageView ivProfilePic;
        public TextView tvUsername;
        public TextView tvTimestamp;
        public TextView tvText;

        public ViewHolder(final View itemView) {
            super(itemView);

            // do all them findViewByIds
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvText = itemView.findViewById(R.id.tvConversation);

            // set onclick listener for each conversation
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Conversation conversation = mConversations.get(pos);
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("conversation", Parcels.wrap(conversation));
                context.startActivity(intent);
            }
        }
    }

    private void setConversationDetails(ParseUser user, TextView tvUsername, ParseImageView ivProfilePic) {
        tvUsername.setText(user.getUsername());
        if (user.getParseFile("profilePic") != null) {
            Glide.with(context)
                    .load(user.getParseFile("profilePic").getUrl())
                    .placeholder(R.drawable.ic_action_name)
                    .centerCrop()
                    .into(ivProfilePic);
        }
    }
}