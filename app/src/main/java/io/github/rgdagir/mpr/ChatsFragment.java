package io.github.rgdagir.mpr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import io.github.rgdagir.mpr.models.Conversation;


public class ChatsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ParseImageView ivProfilePic;
    private Context context;
    private TextView tvUsername;
    private TextView tvNumConvos;
    private ConversationAdapter conversationAdapter;
    RecyclerView rvConversations;
    ArrayList<Conversation> conversations;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        context = getActivity();
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvNumConvos = view.findViewById(R.id.tvNumMsgs);

        Glide.with(this)
                .load(ParseUser.getCurrentUser().getParseFile("profilePic").getUrl())
                .centerCrop()
                .into(ivProfilePic);

        conversations = new ArrayList<>();
        conversationAdapter = new ConversationAdapter(conversations);
        rvConversations.setLayoutManager(new LinearLayoutManager(context));
        rvConversations.setAdapter(conversationAdapter);
        populateConversations();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // Placeholder, to be inserted when clicking is introduced
        void onFragmentInteraction(Uri uri);
    }

    private void populateConversations() {
        final Conversation.Query convosQuery = new Conversation.Query();
        ParseUser currUser = ParseUser.getCurrentUser();
        convosQuery.whereEqualTo("user1", currUser).orderByDescending("updatedAt");
        convosQuery.withUser().whereEqualTo("user2", currUser).orderByDescending("updatedAt");
        convosQuery.findInBackground(
        new FindCallback<Conversation>() {
            @Override
            public void done(List<Conversation> objects, ParseException e) {
                if (e == null) {
                for (int i = 0; i < objects.size(); ++i) {
                    Conversation convo = objects.get(i);
                    conversations.add(convo);
                    conversationAdapter.notifyItemInserted(conversations.size() - 1);
                    Log.d("Conversations", "a conversation has been loaded!");
                }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
