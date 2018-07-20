package io.github.rgdagir.mpr.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Milestone")
public class Milestone extends ParseObject {
    private static final String KEY_CONVERSATION = "conversation";
    private static final String KEY_ACHIEVEMENT = "achievement";
    private static final String KEY_USER = "user";
    private static final String KEY_POINTS_NEEDED = "pointsNeeded";
    private static final String KEY_UNLOCKED = "unlocked";

    public Conversation getConversation() {
        return (Conversation) getParseObject(KEY_CONVERSATION);
    }

    public void setConversation(Conversation conversation) {
        put(KEY_CONVERSATION, conversation);
    }

    public String getAchievement() {
        return getString(KEY_CONVERSATION);
    }

    public void setAchievement(String achievement) {
        put(KEY_ACHIEVEMENT, achievement);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public Integer getPointsNeeded() {
        return (Integer) getNumber(KEY_POINTS_NEEDED);
    }

    public void setPointsNeeded(Integer pointsNeeded) {
        put(KEY_POINTS_NEEDED, pointsNeeded);
    }

    public Boolean getUnlocked() {
        return getBoolean(KEY_UNLOCKED);
    }

    public void setUnlocked(Boolean unlocked) {
        put(KEY_UNLOCKED, unlocked);
    }

    public static class Query extends ParseQuery<Milestone> {
        public Query() {
            super(Milestone.class);
        }

        public Milestone.Query withConversation() {
            include(KEY_CONVERSATION);
            return this;
        }
    }
}
