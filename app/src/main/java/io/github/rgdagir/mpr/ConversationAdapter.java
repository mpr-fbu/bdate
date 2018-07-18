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
        if (currentUser.getObjectId().equals(convo.getUser1().getObjectId())) {
            holder.tvUsername.setText(convo.getUser2().getUsername());
            Glide.with(context)
                    .load(convo.getUser2().getParseFile("profilePic").getUrl())
                    .centerCrop()
                    .into(holder.ivProfilePic);
        } else {
            holder.tvUsername.setText(convo.getUser1().getUsername());
            Glide.with(context)
                    .load(convo.getUser1().getParseFile("profilePic").getUrl())
                    .centerCrop()
                    .into(holder.ivProfilePic);
        }
        holder.tvTimestamp.setText(convo.getTimestamp());
        holder.tvText.setText("Some placeholder message for now");
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
