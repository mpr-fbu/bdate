package io.github.rgdagir.mpr;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.parse.FindCallback;
import com.parse.LiveQueryException;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.rgdagir.mpr.models.Conversation;


public class ChatsListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout swipeContainer;

    private Button btnSendNotification;
    private ParseImageView ivProfilePic;
    private TextView tvUsername;
    private TextView tvNumConversations;
    private ConversationAdapter conversationAdapter;
    RecyclerView rvConversations;
    ArrayList<Conversation> mConversations;
    Context context;
    ParseUser currUser;

    public ChatsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        context = getActivity();
        currUser = ParseUser.getCurrentUser();
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvNumConversations = view.findViewById(R.id.tvNumMessages);
        rvConversations = view.findViewById(R.id.rvConversations);
        btnSendNotification = view.findViewById(R.id.btnSendNotification);

        // Lookup the swipe container view
        swipeContainer = view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshConversations();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Make sure the Parse server is setup to configured for live queries
        // URL for server is determined by Parse.initialize() call.
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();

        ParseQuery<Conversation> conversationsQuery = ParseQuery.getQuery(Conversation.class);
        conversationsQuery.include("lastMessage");
        SubscriptionHandling<Conversation> subscriptionHandling = parseLiveQueryClient.subscribe(conversationsQuery);
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new
                SubscriptionHandling.HandleEventCallback<Conversation>() {
                    @Override
                    public void onEvent(ParseQuery<Conversation> query, Conversation object) {
                        refreshConversations();
                    }
                });
        subscriptionHandling.handleError(new SubscriptionHandling.HandleErrorCallback<Conversation>() {
            @Override
            public void onError(ParseQuery<Conversation> query, LiveQueryException exception) {
                Log.d("Live Query", "Callback failed");
            }
        });

        // set up and populate views
        if (currUser.getParseFile("profilePic") != null) {
            Glide.with(context).load(currUser.getParseFile("profilePic").getUrl())
                    .asBitmap().centerCrop().dontAnimate()
                    .placeholder(R.drawable.ic_action_name)
                    .error(R.drawable.ic_action_name)
                    .into(new BitmapImageViewTarget(ivProfilePic) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            ivProfilePic.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }
        tvUsername.setText(currUser.getString("firstName") + " " + currUser.getString("lastName"));

        mConversations = new ArrayList<>();
        conversationAdapter = new ConversationAdapter(mConversations);
        rvConversations.setLayoutManager(new LinearLayoutManager(context));
        rvConversations.setAdapter(conversationAdapter);
        populateConversations();

        btnSendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> payload = new HashMap<>();
                payload.put("newData", "You got a match, what a miracle! :O");
                ParseCloud.callFunctionInBackground("pushNotificationGeneral", payload);
            }
        });

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
        final ParseQuery<Conversation> conversationsQuery = makeConversationQuery();
        conversationsQuery.findInBackground(new FindCallback<Conversation>() {
            @Override
            public void done(List<Conversation> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); ++i) {
                        Conversation conversation = objects.get(i);
                        mConversations.add(conversation);
                        conversationAdapter.notifyItemInserted(mConversations.size() - 1);
                        Log.d("Conversations", "a conversation has been loaded!" + conversation.getUser1().getUsername());
                    }
                    tvNumConversations.setText(Integer.toString(mConversations.size()));
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void refreshConversations() {
        final ParseQuery<Conversation> conversationsQuery = makeConversationQuery();
        conversationsQuery.findInBackground(new FindCallback<Conversation>() {
            @Override
            public void done(List<Conversation> objects, ParseException e) {
                if (e == null) {
                    // Remember to CLEAR OUT old items before appending in the new ones
                    conversationAdapter.clear();
                    mConversations.clear();
                    // add in new items
                    mConversations.addAll(objects);
                    tvNumConversations.setText(Integer.toString(mConversations.size()));
                    // signal refresh has finished
                    swipeContainer.setRefreshing(false);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private ParseQuery<Conversation> makeConversationQuery() {
        ParseUser currUser = ParseUser.getCurrentUser();
        final ParseQuery<Conversation> conversationsQuery1 = new Conversation.Query();
        conversationsQuery1.whereEqualTo("user1", currUser);
        final ParseQuery<Conversation> conversationsQuery2 = new Conversation.Query();
        conversationsQuery2.whereEqualTo("user2", currUser);

        List<ParseQuery<Conversation>> queries = new ArrayList<>();
        queries.add(conversationsQuery1);
        queries.add(conversationsQuery2);

        final ParseQuery<Conversation> conversationsQuery = ParseQuery.or(queries).whereExists("user2").whereExists("user1");
        conversationsQuery.include("user1").include("user2").include("lastMessage").addDescendingOrder("updatedAt");
        return conversationsQuery;
    }
}
