package io.github.rgdagir.mpr.models;

import io.github.rgdagir.mpr.ChatActivity;

public class Milestone {

    private static final int NAME_SCORE = 3;
    private static final int AGE_SCORE = 5;
    private static final int DISTANCE_AWAY_SCORE = 7;

    public Milestone() {
    }

    public static boolean canSeeName(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        if (currentPoints == NAME_SCORE) {
            ChatActivity.showSnackbar("name");
            return true;
        }
        return (currentPoints > NAME_SCORE);
    }

    public static boolean canSeeAge(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        if (currentPoints == AGE_SCORE) {
            ChatActivity.showSnackbar("age");
            return true;
        }
        return (currentPoints > AGE_SCORE);
    }

    public static boolean canSeeDistanceAway(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        if (currentPoints == DISTANCE_AWAY_SCORE) {
            ChatActivity.showSnackbar("distance away");
            return true;
        }
        return (currentPoints > DISTANCE_AWAY_SCORE);
    }
}
