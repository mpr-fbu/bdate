package io.github.rgdagir.mpr.models;

import io.github.rgdagir.mpr.ChatActivity;

public class Milestone {

    private static final int INTERESTS_SCORE = 2;
    private static final int NAME_SCORE = 6;
    private static final int AGE_SCORE = 8;
    private static final int DISTANCE_AWAY_SCORE = 10;
    private static final int OCCUPATION_SCORE = 12;
    private static final int PROFILE_PICTURE_SCORE = 16;
    private static final int GALLERY_SCORE = 20;
    private static final int DATE_SCORE = 24;
    ChatActivity activity;

    public Milestone() {
        activity = new ChatActivity();
    }

    public static boolean canSeeInterests(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        return (currentPoints >= INTERESTS_SCORE);
    }

    public static boolean canSeeName(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        return (currentPoints >= NAME_SCORE);
    }

    public static boolean canSeeGender(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        return (currentPoints >= NAME_SCORE);
    }

    public static boolean canSeeAge(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        return (currentPoints >= AGE_SCORE);
    }

    public static boolean canSeeDistanceAway(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        return (currentPoints >= DISTANCE_AWAY_SCORE);
    }

    public static boolean canSeeOccupation(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        return (currentPoints >= OCCUPATION_SCORE);
    }

    public static boolean canSeeProfilePicture(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        return (currentPoints >= PROFILE_PICTURE_SCORE);
    }

    public static boolean canSeeGallery(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        return (currentPoints >= GALLERY_SCORE);
    }

    public static boolean canGoOnDate(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        return (currentPoints >= DATE_SCORE);
    }

    public void showNotification(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        if (currentPoints == INTERESTS_SCORE) {
            activity.showTextViewNotification("interests");
        } else if (currentPoints == NAME_SCORE) {
            activity.showTextViewNotification("name");
        } else if (currentPoints == AGE_SCORE) {
            activity.showTextViewNotification("age");
        } else if (currentPoints == DISTANCE_AWAY_SCORE) {
            activity.showTextViewNotification("distance away");
        } else if (currentPoints == OCCUPATION_SCORE) {
            activity.showTextViewNotification("occupation");
        } else if (currentPoints == PROFILE_PICTURE_SCORE){
            activity.showTextViewNotification("profile picture");
        } else if (currentPoints == GALLERY_SCORE){
            activity.showTextViewNotification("gallery");
        }
    }
}
