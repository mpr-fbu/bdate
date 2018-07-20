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
    private static final String KEY_USER1_SENT = "sentByUser1";
    private static final String KEY_USER2_SENT = "sentByUser2";

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

    public Message getLastMessage() {
        return (Message) getParseObject(KEY_LAST_MSG);
    }

    public void setLastMessage(Message msg) {
        put(KEY_LAST_MSG, msg);
    }

    public Integer getSentByUser1() {
        return (Integer) getNumber(KEY_USER1_SENT);
    }

    public void setSentByUser1(Integer sentByuser1) {
        put(KEY_USER1_SENT, sentByuser1);
    }

    public Integer getSentByUser2() {
        return (Integer) getNumber(KEY_USER2_SENT);
    }

    public void setSentByUser2(Integer sentByuser2) {
        put(KEY_USER1_SENT, sentByuser2);
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
