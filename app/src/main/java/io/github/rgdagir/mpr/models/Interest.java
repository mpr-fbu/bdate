package io.github.rgdagir.mpr.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Interest")
public class Interest extends ParseObject{
    private static final String KEY_NAME = "name";
    private boolean isSelected;

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String text) {
        put(KEY_NAME, text);
    }

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public static class Query extends ParseQuery<Interest> {
        public Query() {
            super(Interest.class);
        }

        public Interest.Query withName() {
            include(KEY_NAME);
            return this;
        }
    }
}
