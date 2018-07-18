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
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import io.github.rgdagir.mpr.models.Conversation;


public class ChatsListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ParseImageView ivProfilePic;
    private Context context;
    private TextView tvUsername;
    private TextView tvNumConvos;
    private ConversationAdapter conversationAdapter;
    RecyclerView rvConversations;
    ArrayList<Conversation> conversations;

    public ChatsListFragment() {
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
        ParseUser currUser = ParseUser.getCurrentUser();
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvNumConvos = view.findViewById(R.id.tvNumMsgs);
        rvConversations = view.findViewById(R.id.rvConversations);

        if (currUser.getParseFile("profilePic") != null) {
            Glide.with(this)
                    .load(currUser.getParseFile("profilePic").getUrl())
                    .centerCrop()
                    .into(ivProfilePic);
        }
        tvUsername.setText(currUser.getString("firstName") + " " + currUser.getString("lastName"));

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
        ParseUser currUser = ParseUser.getCurrentUser();
        final ParseQuery<Conversation> convosQuery1 = new Conversation.Query();
        convosQuery1.whereEqualTo("user1", currUser);
        final ParseQuery<Conversation> convosQuery2 = new Conversation.Query();
        convosQuery2.whereEqualTo("user2", currUser);

        List<ParseQuery<Conversation>> queries = new ArrayList<>();
        queries.add(convosQuery1);
        queries.add(convosQuery2);

        final ParseQuery<Conversation> convosQuery = ParseQuery.or(queries).whereExists("user2").whereExists("user1");
        convosQuery.include("user1").include("user2").include("lastMessage").addDescendingOrder("updatedAt");
        convosQuery.findInBackground(new FindCallback<Conversation>() {
            @Override
            public void done(List<Conversation> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); ++i) {
                        Conversation convo = objects.get(i);
                        conversations.add(convo);
                        conversationAdapter.notifyItemInserted(conversations.size() - 1);
                        Log.d("Conversations", "a conversation has been loaded!" + convo.getUser1().getUsername());
                    }
                    tvNumConvos.setText(Integer.toString(conversations.size()));
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
