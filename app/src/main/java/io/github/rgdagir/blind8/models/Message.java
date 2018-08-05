package io.github.rgdagir.blind8.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@ParseClassName("Message")
public class Message extends ParseObject {
    private static final String KEY_SENDER = "sender";
    private static final String KEY_CONVERSATION = "conversation";
    private static final String KEY_TEXT = "text";

    public ParseUser getSender() {
        return getParseUser(KEY_SENDER);
    }

    public void setSender(ParseUser user) {
        put(KEY_SENDER, user);
    }

    public Conversation getConversation() {
        return (Conversation) getParseObject(KEY_CONVERSATION);
    }

    public void setConversation(Conversation conversation) {
        put(KEY_CONVERSATION, conversation);
    }

    public String getText() {
        return getString(KEY_TEXT);
    }

    public void setText(String text) {
        put(KEY_TEXT, text);
    }

    public String getApproxTimestamp() {
        Date date = getCreatedAt();
        Date now = new Date();
        DateFormat dayFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        long daysDiff = Math.round((now.getTime() - date.getTime()) / (double) 86400000);
        if (dayFormat.format(date).equals(dayFormat.format(now))) {
            DateFormat df = new SimpleDateFormat("h:mm aaa", Locale.getDefault());
            return df.format(date);
        } else if (daysDiff < 7) {
            DateFormat df = new SimpleDateFormat("EEE", Locale.getDefault());
            return df.format(date);
        } else {
            DateFormat df = new SimpleDateFormat("MMM d", Locale.getDefault());
            return df.format(date);
        }
    }

    public String getExactTimestamp() {
        Date date = getCreatedAt();
        Date now = new Date();
        DateFormat dayFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        if (dayFormat.format(date).equals(dayFormat.format(now))) {
            DateFormat df = new SimpleDateFormat("h:mm aaa", Locale.getDefault());
            return "Today at " + df.format(date);
        } else {
            DateFormat df = new SimpleDateFormat("MMM d", Locale.getDefault());
            DateFormat df2 = new SimpleDateFormat("h:mm aaa", Locale.getDefault());
            return df.format(date) + " at " + df2.format(date);
        }
    }

    public static class Query extends ParseQuery<Message> {
        public Query() {
            super(Message.class);
        }

        public Message.Query withConversationSender() {
            include(KEY_CONVERSATION);
            include(KEY_SENDER);
            return this;
        }
    }
}
