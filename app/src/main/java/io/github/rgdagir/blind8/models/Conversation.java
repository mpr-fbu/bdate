package io.github.rgdagir.mpr.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@ParseClassName("Conversation")
public class Conversation extends ParseObject implements Serializable {
    private static final String KEY_USER1 = "user1";
    private static final String KEY_USER2 = "user2";
    private static final String KEY_LAST_MSG = "lastMessage";
    private static final String KEY_LAST_TIME = "lastMessageTime";
    private static final String KEY_EXCHANGES = "exchanges";
    private static final String KEY_READ1 = "readUser1";
    private static final String KEY_READ2 = "readUser2";
    private static final String KEY_USER1_MIN_AGE = "user1MinAge";
    private static final String KEY_USER1_MAX_AGE = "user1MaxAge";
    private static final String KEY_USER1GENDER = "user1Gender";

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

    public Date getLastMessageTime() {
        return getDate(KEY_LAST_TIME);
    }

    public void setLastMessageTime(Date date) {
        put(KEY_LAST_TIME, date);
    }

    public void setExchanges(Integer exchanges) {
        put(KEY_EXCHANGES, exchanges);
    }

    public Integer getExchanges() {
        return getInt("exchanges");
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

    public void setMatchLocation(ParseGeoPoint location) {
        put("matchLocation", location);
    }

    public ParseGeoPoint getMatchLocation() {
        return getParseGeoPoint("matchLocation");
    }

    public void setMatchRange(int rangeInMiles) {
        put("matchRange", rangeInMiles);
    }

    public int getMatchRange() {
        return getInt("matchRange");
    }

    public boolean getReadUser1() {
        return getBoolean(KEY_READ1);
    }

    public void setReadUser1(boolean value) {
        put(KEY_READ1, value);
    }

    public boolean getReadUser2() {
        return getBoolean(KEY_READ2);
    }

    public void setReadUser2(boolean value) {
        put(KEY_READ2, value);
    }

    public void setUser1MinAge(Integer age) {
        put("user1MinAge", age);
    }

    public Integer getUser1MinAge() {
        return getInt(KEY_USER1_MIN_AGE);
    }

    public void setUser1MaxAge(Integer age) {
        put("user1MaxAge", age);
    }

    public Integer getUser1MaxAge() {
        return getInt(KEY_USER1_MAX_AGE);
    }

    public void setUser1Gender(String gender) {
        put("user1Gender", gender);
    }

    public String getUser1Gender() {
        return getString(KEY_USER1GENDER);
    }
}