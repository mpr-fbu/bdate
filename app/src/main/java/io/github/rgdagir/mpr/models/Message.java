package io.github.rgdagir.mpr.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject {
    private static final String KEY_SENDER = "sender";
    private static final String KEY_CONVERSATION = "conversation";
    private static final String KEY_TEXT = "text";

    public ParseUser getSender() {
        return getParseUser(KEY_SENDER);
    }

    public void setSender(ParseUser user) {
        put(KEY_SENDER, user);
    }

    public Conversation getConversation() {
        return (Conversation) getParseObject(KEY_CONVERSATION);
    }

    public void setConversation(Conversation conversation) {
        put(KEY_CONVERSATION, conversation);
    }

    public String getText() {
        return getString(KEY_TEXT);
    }

    public void setText(String text) {
        put(KEY_TEXT, text);
    }

    public static class Query extends ParseQuery<Message> {
        public Query() {
            super(Message.class);
        }

        public Message.Query withConversationSender() {
            include(KEY_CONVERSATION);
            include(KEY_SENDER);
            return this;
        }
    }
}
