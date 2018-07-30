package io.github.rgdagir.mpr.models;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("UserInterest")
public class UserInterest extends ParseObject {
    private static final String KEY_USER = "user";
    private static final String KEY_INTEREST = "interest";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public Interest getInterest() {
        return (Interest) getParseObject(KEY_INTEREST);
    }

    public void setInterest(ParseUser user) {
        put(KEY_INTEREST, user);
    }

    public static class Query extends ParseQuery<UserInterest> {
        public Query() {
            super(UserInterest.class);
        }

        public UserInterest.Query withUserInterest() {
            include(KEY_USER);
            include(KEY_INTEREST);
            return this;
        }
    }
}
