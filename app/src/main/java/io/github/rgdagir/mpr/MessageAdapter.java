package io.github.rgdagir.mpr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseUser;

import java.util.List;

import io.github.rgdagir.mpr.models.Conversation;
import io.github.rgdagir.mpr.models.Message;
import io.github.rgdagir.mpr.models.Milestone;

import static com.parse.Parse.getApplicationContext;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> mMessages;
    private Conversation mConversation;
    private ParseUser currUser;
    private ParseUser otherUser;

    public MessageAdapter(List<Message> messages, Conversation conversation) {
        mMessages = messages;
        mConversation = conversation;
        currUser = ParseUser.getCurrentUser();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case 0:
                View messageViewMe = inflater.inflate(R.layout.item_message_me, parent, false);
                return new ViewHolderMe(messageViewMe);
            case 1:
                View messageViewOther = inflater.inflate(R.layout.item_message_other, parent, false);
                return new ViewHolderOther(messageViewOther);
        }
        View messageViewMe = inflater.inflate(R.layout.item_message_me, parent, false);
        return new ViewHolderMe(messageViewMe);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        Message message = mMessages.get(position);

        switch (holder.getItemViewType()) {
            case 0:
                final ViewHolderMe viewHolderMe = (ViewHolderMe) holder;
                viewHolderMe.mInfoMe.setText(message.getExactTimestamp());
                viewHolderMe.mMessageMe.setText(message.getText());
                break;
            case 1:
                final ViewHolderOther viewHolderOther = (ViewHolderOther) holder;
                viewHolderOther.mInfoOther.setText(message.getExactTimestamp());
                viewHolderOther.mMessageOther.setText(message.getText());
                if (Milestone.canSeeProfilePicture(mConversation)) {
                    displayActualProfilePicture(viewHolderOther.mProfilePic);
                    viewHolderOther.mDefaultProPic.setVisibility(View.INVISIBLE);
                    viewHolderOther.mProfilePic.setVisibility(View.VISIBLE);
                } else {
                    viewHolderOther.mDefaultProPic.setVisibility(View.VISIBLE);
                    viewHolderOther.mProfilePic.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    public void setConversation(Conversation conversation) {
        mConversation = conversation;
    }

    private void displayActualProfilePicture(final ParseImageView otherProfilePic) {
        if (currUser.getObjectId().equals(mConversation.getUser1().getObjectId())) {
            otherUser = mConversation.getUser2();
        } else {
            otherUser = mConversation.getUser1();
        }
        try {
            Glide.with(getApplicationContext()).load(otherUser.fetchIfNeeded().getParseFile("profilePic").getUrl())
                    .asBitmap().centerCrop().dontAnimate()
                    .placeholder(R.drawable.ic_action_name)
                    .error(R.drawable.ic_action_name)
                    .into(new BitmapImageViewTarget(otherProfilePic) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            otherProfilePic.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        } catch (ParseException e) {
            Log.e("ChatActivity", "Error displaying actual profile picture");
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);
        ParseUser sender = message.getSender();
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (sender.getObjectId().equals(currentUser.getObjectId())) {
            return 0;
        } else {
            return 1;
        }
    }

    public class ViewHolderMe extends RecyclerView.ViewHolder {
        TextView mMessageMe;
        TextView mInfoMe;

        public ViewHolderMe(View itemView) {
            super(itemView);
            mMessageMe = itemView.findViewById(R.id.tvMessageMe);
            mInfoMe = itemView.findViewById(R.id.tvInfoMe);
            mInfoMe.setVisibility(View.GONE);

            mInfoMe.animate().alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mInfoMe.clearAnimation();
                        }
                    });

            mMessageMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mInfoMe.isShown()) {
                        mInfoMe.animate().alpha(0.0f).setDuration(300)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        mInfoMe.clearAnimation();
                                        mInfoMe.setVisibility(View.GONE);
                                    }
                                });
                    }
                    else {
                        mInfoMe.setVisibility(View.VISIBLE);
                        mInfoMe.animate().alpha(1.0f).setDuration(400)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        mInfoMe.clearAnimation();
                                    }
                                });
                    }
                }
            });
        }
    }

    public class ViewHolderOther extends RecyclerView.ViewHolder {
        TextView mMessageOther;
        TextView mInfoOther;
        ParseImageView mProfilePic;
        ImageView mDefaultProPic;
        LinearLayout chatBubble;

        public ViewHolderOther(View itemView) {
            super(itemView);
            mMessageOther = itemView.findViewById(R.id.tvMessageOther);
            mInfoOther = itemView.findViewById(R.id.tvInfoOther);
            mProfilePic = itemView.findViewById(R.id.ivProfilePic);
            mDefaultProPic = itemView.findViewById(R.id.defaultImageView);
            chatBubble = itemView.findViewById(R.id.contentWithBackgroundOther);
            mInfoOther.setVisibility(View.GONE);

            mInfoOther.animate().alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mInfoOther.clearAnimation();
                        }
                    });

            mMessageOther.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mInfoOther.isShown()) {
                        mInfoOther.animate().alpha(0.0f).setDuration(300)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        mInfoOther.clearAnimation();
                                        mInfoOther.setVisibility(View.GONE);
                                    }
                                });
                    }
                    else {
                        mInfoOther.setVisibility(View.VISIBLE);
                        mInfoOther.animate().alpha(1.0f).setDuration(400)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        mInfoOther.clearAnimation();
                                    }
                                });
                    }
                }
            });
        }
    }
}
