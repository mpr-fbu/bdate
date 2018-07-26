package io.github.rgdagir.mpr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

import io.github.rgdagir.mpr.models.Conversation;
import io.github.rgdagir.mpr.models.Message;

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
            if (!conversation.getReadUser1()) {
                holder.tvText.setTypeface(null, Typeface.BOLD);
                holder.tvText.setTextColor(Color.WHITE);
            }
        } else {
            setConversationDetails(conversation.getUser1(), holder.tvUsername, holder.ivProfilePic);
            if (!conversation.getReadUser2()) {
                holder.tvText.setTypeface(null, Typeface.BOLD);
                holder.tvText.setTextColor(Color.WHITE);
            }
        }

        if (conversation.getLastMessage() == null) {
            holder.tvText.setText("No messages yet! Start talking...");
            holder.tvTimestamp.setText("");
        } else {
            Message lastMessage = conversation.getLastMessage();
            try {
                Message message = lastMessage.fetchIfNeeded();
                /* if (message.getSender().getObjectId().equals(currentUser.getObjectId())) {
                    holder.tvText.setText("You: " + lastMessage.getText());
                } else {
                    holder.tvText.setText(lastMessage.getText());
                } */
                holder.tvText.setText(lastMessage.getText());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.tvTimestamp.setText(conversation.getTimestamp());
        }
    }

    @Override
    public int getItemCount() { return mConversations.size(); }

    // Clean all elements of the recycler
    public void clear() {
        mConversations.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Conversation> list) {
        mConversations.addAll(list);
        notifyDataSetChanged();
    }

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

    private void setConversationDetails(ParseUser user, TextView tvUsername, final ParseImageView ivProfilePic) {
        tvUsername.setText(user.getUsername());
        if (user.getParseFile("profilePic") != null) {
            Glide.with(context).load(user.getParseFile("profilePic").getUrl())
                    .asBitmap().centerCrop().dontAnimate()
                    .placeholder(R.drawable.ic_action_name)
                    .error(R.drawable.ic_action_name)
                    .into(new BitmapImageViewTarget(ivProfilePic) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            ivProfilePic.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }
    }
}
