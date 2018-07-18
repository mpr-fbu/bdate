package io.github.rgdagir.mpr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseImageView;
import com.parse.ParseUser;

import java.util.List;

import io.github.rgdagir.mpr.models.Conversation;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    List<Conversation> mConvos;
    Context context;

    public ConversationAdapter(List<Conversation> convos) {
        mConvos = convos;
    }

    // for each row, inflate layout and cache refs into ViewHolder
    @NonNull
    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View convoView = inflater.inflate(R.layout.item_conversation, parent, false);
        ConversationAdapter.ViewHolder viewHolder = new ConversationAdapter.ViewHolder(convoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationAdapter.ViewHolder holder, int position) {
        // get data according to position
        final Conversation convo = mConvos.get(position);
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (convo.getUser2() != null) {
            if (currentUser.getObjectId().equals(convo.getUser1().getObjectId())) {
                holder.tvUsername.setText(convo.getUser2().getUsername());
                if (convo.getUser2().getParseFile("profilePic") != null) {
                    Glide.with(context)
                            .load(convo.getUser2().getParseFile("profilePic").getUrl())
                            .placeholder(R.drawable.ic_action_name)
                            .centerCrop()
                            .into(holder.ivProfilePic);
                }
            } else {
                holder.tvUsername.setText(convo.getUser1().getUsername());
                if (convo.getUser1().getParseFile("profilePic") != null) {
                    Glide.with(context)
                            .load(convo.getUser1().getParseFile("profilePic").getUrl())
                            .placeholder(R.drawable.ic_action_name)
                            .centerCrop()
                            .into(holder.ivProfilePic);
                }
            }
        }
        if (convo.getLastMessage() == null) {
            holder.tvText.setText("No messages yet! Start talking...");
            holder.tvTimestamp.setText("");
        } else {
            holder.tvText.setText(convo.getLastMessage().getText());
            holder.tvTimestamp.setText(convo.getTimestamp());
        }
    }

    @Override
    public int getItemCount() { return mConvos.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
        }
    }
}
