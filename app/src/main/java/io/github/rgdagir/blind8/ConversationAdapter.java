package io.github.rgdagir.blind8;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

import io.github.rgdagir.blind8.models.Conversation;
import io.github.rgdagir.blind8.models.Message;
import io.github.rgdagir.blind8.models.Milestone;

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
            setConversationDetails(conversation.getUser2(), holder.tvUsername, holder.ivProfilePic,
                    holder.ivDefaultPic, conversation);
            if (!conversation.getReadUser1()) {
                Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                holder.tvText.setTypeface(boldTypeface);
                holder.tvText.setTextColor(context.getResources().getColor(R.color.offBlack));
            }
        } else {
            setConversationDetails(conversation.getUser1(), holder.tvUsername, holder.ivProfilePic,
                    holder.ivDefaultPic, conversation);
            if (!conversation.getReadUser2()) {
                Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                holder.tvText.setTypeface(boldTypeface);
                holder.tvText.setTextColor(context.getResources().getColor(R.color.offBlack));
            }
        }

        if (conversation.getLastMessage() == null) {
            holder.tvText.setText("No messages yet! Start talking...");
            holder.tvTimestamp.setText("");
        } else {
            Message lastMessage = conversation.getLastMessage();
            final ParseUser cUser = currentUser;
            final ConversationAdapter.ViewHolder h = holder;
            final Message lMsg = lastMessage;
            lastMessage.fetchInBackground(new GetCallback<Message>() {
                @Override
                public void done(Message object, ParseException e) {
                    if (object.getSender().getObjectId().equals(cUser.getObjectId())) {
                        h.tvText.setText("You: " + lMsg.getText());
                    } else {
                        h.tvText.setText(lMsg.getText());
                        Log.d("IMPORTANT ACTIVITY", "new message displayed - " + lMsg.getText());
                    }
                }
            });
            holder.tvTimestamp.setText(conversation.getLastMessage().getApproxTimestamp());
        }
        updateLevel(conversation, holder);
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

    private void updateLevel(Conversation conversation, ConversationAdapter.ViewHolder holder) {
        holder.heart.setVisibility(View.INVISIBLE);
        if (Milestone.canGoOnDate(conversation)) {
            holder.level.setText("");
            holder.heart.setVisibility(View.VISIBLE);
            holder.levelCircle.setColorFilter(ContextCompat.getColor(context,
                    R.color.heartRed));
        } else if (Milestone.canSeeGallery(conversation)) {
            holder.level.setText("7");
            holder.level.setTextColor(context.getResources().getColor(R.color.pastelRed));
            holder.levelCircle.setColorFilter(ContextCompat.getColor(context,
                    R.color.pastelRed));
        } else if (Milestone.canSeeAge(conversation)) {
            holder.level.setText("6");
            holder.level.setTextColor(context.getResources().getColor(R.color.pastelOrange));
            holder.levelCircle.setColorFilter(ContextCompat.getColor(context,
                    R.color.pastelOrange));
        } else if (Milestone.canSeeOccupation(conversation)) {
            holder.level.setText("5");
            holder.level.setTextColor(context.getResources().getColor(R.color.pastelYellow));
            holder.levelCircle.setColorFilter(ContextCompat.getColor(context,
                    R.color.pastelYellow));
        } else if (Milestone.canSeeDistanceAway(conversation)) {
            holder.level.setText("4");
            holder.level.setTextColor(context.getResources().getColor(R.color.pastelGreen));
            holder.levelCircle.setColorFilter(ContextCompat.getColor(context,
                    R.color.pastelGreen));
        } else if (Milestone.canSeeProfilePicture(conversation)) {
            holder.level.setText("3");
            holder.level.setTextColor(context.getResources().getColor(R.color.pastelBlue2));
            holder.levelCircle.setColorFilter(ContextCompat.getColor(context,
                    R.color.pastelBlue2));
        } else if (Milestone.canSeeName(conversation)) {
            holder.level.setText("2");
            holder.level.setTextColor(context.getResources().getColor(R.color.pastelPurple2));
            holder.levelCircle.setColorFilter(ContextCompat.getColor(context,
                    R.color.pastelPurple2));
        } else if (Milestone.canSeeInterests(conversation)) {
            holder.level.setText("1");
            holder.level.setTextColor(context.getResources().getColor(R.color.pastelPink2));
            holder.levelCircle.setColorFilter(ContextCompat.getColor(context,
                    R.color.pastelPink2));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ParseImageView ivProfilePic;
        public ImageView ivDefaultPic;
        public ImageView levelCircle;
        public ImageView heart;
        public TextView tvUsername;
        public TextView tvTimestamp;
        public TextView tvText;
        public TextView level;

        public ViewHolder(final View itemView) {
            super(itemView);

            // do all them findViewByIds
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            ivDefaultPic = itemView.findViewById(R.id.defaultProfilePic);
            levelCircle = itemView.findViewById(R.id.levelCircle);
            heart = itemView.findViewById(R.id.heart);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvText = itemView.findViewById(R.id.tvConversation);
            level = itemView.findViewById(R.id.level);

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

    private void setConversationDetails(ParseUser user, TextView tvUsername, final ParseImageView ivProfilePic,
                                        ImageView ivDefaultPic, Conversation conversation) {
        if (Milestone.canSeeProfilePicture(conversation)) {
            tvUsername.setText(user.getString("firstName"));
            ivDefaultPic.setVisibility(View.INVISIBLE);
            ivProfilePic.setVisibility(View.VISIBLE);
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
        } else if (Milestone.canSeeName(conversation)) {
            tvUsername.setText(user.getString("firstName"));
            ivDefaultPic.setVisibility(View.VISIBLE);
            ivProfilePic.setVisibility(View.INVISIBLE);
        } else {
            tvUsername.setText(user.getString("fakeName"));
            ivDefaultPic.setVisibility(View.VISIBLE);
            ivProfilePic.setVisibility(View.INVISIBLE);
        }
    }
}
