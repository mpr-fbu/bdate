package io.github.rgdagir.mpr.models;

public class Milestone {

    private static Conversation conversation;
    private static final int NAME_SCORE = 5;
    private static final int AGE_SCORE = 5;
    private static final int DISTANCE_AWAY_SCORE = 5;

    public Milestone(Conversation convo) {
        conversation = convo;
    }

    public static boolean canSeeName() {
        int currentPoints = conversation.getExchanges();
        if (currentPoints >= NAME_SCORE) {
            return true;
        }
        return false;
    }

    public static boolean canSeeAge() {
        int currentPoints = conversation.getExchanges();
        if (currentPoints >= AGE_SCORE) {
            return true;
        }
        return false;
    }

    public static boolean canSeeDistanceAway() {
        int currentPoints = conversation.getExchanges();
        if (currentPoints >= DISTANCE_AWAY_SCORE) {
            return true;
        }
        return false;
    }
}
