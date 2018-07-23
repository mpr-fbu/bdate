package io.github.rgdagir.mpr.models;

import com.parse.ParseUser;

public class Milestone {

    public Conversation conversation;
    public Boolean nameUnlocked;
    public Boolean ageUnlocked;
    public Boolean distanceAwayUnlocked;

    public Milestone(Conversation conversation) {
        this.conversation = conversation;
        nameUnlocked = false;
        ageUnlocked = false;
        distanceAwayUnlocked = false;
    }

    public void canSeeName() {
        int currentPoints = conversation.getExchanges();
        if (currentPoints >= 3) {
            nameUnlocked = true;
        }
    }

    public void canSeeAge() {
        int currentPoints = conversation.getExchanges();
        if (currentPoints >= 6) {
            ageUnlocked = true;
        }
    }

    public void canSeeDistanceAway() {
        int currentPoints = conversation.getExchanges();
        if (currentPoints >= 9) {
            distanceAwayUnlocked = true;
        }
    }
}
