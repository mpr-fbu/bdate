package io.github.rgdagir.mpr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

import bolts.Task;
import io.github.rgdagir.mpr.models.Message;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> mMessages;
    private String senderObjId;
    private boolean isItYou;

    public MessageAdapter(List<Message> messages) {
        mMessages = messages;
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
                viewHolderMe.mInfoMe.setText(message.getTimestamp());
                message.fetchInBackground(new GetCallback<Message>() {
                    @Override
                    public void done(Message object, ParseException e) {
                        if (e == null) {
                            viewHolderMe.mMessageMe.setText(object.getText());
                        } else {
                            Log.e("MessageAdapter", e.toString());
                        }
                    }
                });
                break;
            case 1:
                final ViewHolderOther viewHolderOther = (ViewHolderOther) holder;
                viewHolderOther.mInfoOther.setText(message.getTimestamp());
                message.fetchInBackground(new GetCallback<Message>() {
                    @Override
                    public void done(Message object, ParseException e) {
                        if (e == null) {
                            viewHolderOther.mMessageOther.setText(object.getText());
                        } else {
                            Log.e("MessageAdapter", e.toString());
                        }
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);
        message.getSender().fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser senderUser, ParseException e) {
                if (e == null) {
                    senderObjId = senderUser.getObjectId();
                    Log.d("Fetch Sender", "success!");
                    String currentUserObjId = ParseUser.getCurrentUser().getObjectId();
                    if (senderObjId.equals(currentUserObjId)) {
                        isItYou = false;
                    } else {
                        isItYou = true;
                    }
                } else {
                    Log.e("Fetch Sender", "wtf is going on");
                    e.printStackTrace();
                }
            }
        });
        return isItYou ? 1 : 0;
    }

    public class ViewHolderMe extends RecyclerView.ViewHolder {
        TextView mMessageMe;
        TextView mInfoMe;

        public ViewHolderMe(View itemView) {
            super(itemView);
            mMessageMe = itemView.findViewById(R.id.tvMessageMe);
            mInfoMe = itemView.findViewById(R.id.tvInfoMe);
        }
    }

    public class ViewHolderOther extends RecyclerView.ViewHolder {
        TextView mMessageOther;
        TextView mInfoOther;

        public ViewHolderOther(View itemView) {
            super(itemView);
            mMessageOther = itemView.findViewById(R.id.tvMessageOther);
            mInfoOther = itemView.findViewById(R.id.tvInfoOther);
        }
    }
}
