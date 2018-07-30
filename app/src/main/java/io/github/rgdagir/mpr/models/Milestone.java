package io.github.rgdagir.mpr.models;

import io.github.rgdagir.mpr.ChatActivity;

public class Milestone {

    private static final int NAME_AND_GENDER_SCORE = 3;
    private static final int AGE_SCORE = 5;
    private static final int DISTANCE_AWAY_SCORE = 7;
    private static final int OCCUPATION_SCORE = 8;
    private static final int PROFILE_PICTURE_SCORE = 9;
    private static final int GALLERY_SCORE = 11;

    public Milestone() {
    }

    public static boolean canSeeName(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        return (currentPoints >= NAME_AND_GENDER_SCORE);
    }

    public static boolean canSeeGender(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        return (currentPoints >= NAME_AND_GENDER_SCORE);
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

    public static void showNotification(Conversation conversation) {
        int currentPoints = conversation.getExchanges();
        if (currentPoints == NAME_AND_GENDER_SCORE) {
            ChatActivity.showTextViewNotification("name and gender");
        } else if (currentPoints == AGE_SCORE) {
            ChatActivity.showTextViewNotification("age");
        } else if (currentPoints == DISTANCE_AWAY_SCORE) {
            ChatActivity.showTextViewNotification("distance away");
        } else if (currentPoints == OCCUPATION_SCORE) {
            ChatActivity.showTextViewNotification("occupation");
        } else if (currentPoints == PROFILE_PICTURE_SCORE){
            ChatActivity.showTextViewNotification("profile picture");
        } else if (currentPoints == GALLERY_SCORE){
            ChatActivity.showTextViewNotification("gallery");
        }
    }
}
