package io.github.rgdagir.mpr.models;

public class Milestone {

    private static final int NAME_SCORE = 3;
    private static final int AGE_SCORE = 6;
    private static final int DISTANCE_AWAY_SCORE = 9;

    public Milestone() {
    }

    public static boolean canSeeName(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        if (currentPoints >= NAME_SCORE) {
            return true;
        }
        return false;
    }

    public static boolean canSeeAge(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        if (currentPoints >= AGE_SCORE) {
            return true;
        }
        return false;
    }

    public static boolean canSeeDistanceAway(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        if (currentPoints >= DISTANCE_AWAY_SCORE) {
            return true;
        }
        return false;
    }
}
