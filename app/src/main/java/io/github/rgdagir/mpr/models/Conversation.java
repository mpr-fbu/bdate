package io.github.rgdagir.mpr.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@ParseClassName("Conversation")
public class Conversation extends ParseObject {
    private static final String KEY_USER1 = "user1";
    private static final String KEY_USER2 = "user2";
    private static final String KEY_FULL = "full";
    private static final String KEY_LAST_MSG = "lastMessage";

    public ParseUser getUser1() {
        return getParseUser(KEY_USER1);
    }

    public void setUser1(ParseUser user) {
        put(KEY_USER1, user);
    }

    public ParseUser getUser2() {
        return getParseUser(KEY_USER2);
    }

    public void setUser2(ParseUser user) {
        put(KEY_USER2, user);
    }

    public Date getLastUpdated() {
        return getUpdatedAt();
    }

    public void setLastUpdated(Date date) {
        setLastUpdated(date);
    }

    public String getTimestamp() {
        Date date = getLastMessage().getCreatedAt();
        DateFormat df = new SimpleDateFormat("MMM d", Locale.getDefault());
        DateFormat df2 = new SimpleDateFormat("hh:mm aaa", Locale.getDefault());
        return df.format(date) + " at " + df2.format(date);
    }

    public Boolean getFull() {return getBoolean(KEY_FULL);}

    public void setFull(Boolean full) {put(KEY_FULL, full);}

    public Message getLastMessage() {
        return (Message) getParseObject(KEY_LAST_MSG);
    }

    public void setLastMessage(Message msg) {
        put(KEY_LAST_MSG, msg);
    }

    public static class Query extends ParseQuery<Conversation> {
        public Query() {
            super(Conversation.class);
        }

        public Conversation.Query withUser() {
            include(KEY_USER1);
            include(KEY_USER2);
            return this;
        }
    }
}
