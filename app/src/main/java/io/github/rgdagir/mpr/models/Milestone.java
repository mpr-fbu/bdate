package io.github.rgdagir.mpr.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Milestone")
public class Milestone extends ParseObject {
    private static final String KEY_CONVERSATION = "conversation";
    private static final String KEY_ACHIEVEMENT = "achievement";
    private static final String KEY_POINTS_NEEDED = "pointsNeeded";

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

    public Integer getPointsNeeded() {
        return (Integer) getNumber(KEY_POINTS_NEEDED);
    }

    public void setPointsNeeded(Integer pointsNeeded) {
        put(KEY_POINTS_NEEDED, pointsNeeded);
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
