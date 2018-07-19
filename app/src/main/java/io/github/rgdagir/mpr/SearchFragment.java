package io.github.rgdagir.mpr;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import io.github.rgdagir.mpr.models.Conversation;

public class SearchFragment extends Fragment {
    private SearchFragment.OnFragmentInteractionListener mListener;
    private Button searchButton;

    public SearchFragment() {
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        searchButton = view.findViewById(R.id.btnSearch);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchForOpenConvos();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchFragment.OnFragmentInteractionListener) {
            mListener = (SearchFragment.OnFragmentInteractionListener) context;
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

    //searches for convos that have only one user and matches current user to other user if they're not already matched
    //if all convos are full creates a new convo with only the current user
    //a new convo must only have User1; User2 should be null
    //users cannot open more than one new convo; somehow convo must delete itself after a time period or immediately before log out
    public void searchForOpenConvos() {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            final Conversation.Query openConvosQuery = new Conversation.Query();
            openConvosQuery.whereDoesNotExist("user2").include("user1");
            openConvosQuery.findInBackground(new FindCallback<Conversation>() {
                @Override
                public void done(final List<Conversation> objects, ParseException e) {
                    if (e == null) {
                        //objects has list of open conversations
                        boolean check = hasOpenConvo(currentUser, objects);
                        if (hasOpenConvo(currentUser, objects)) {
                            Toast.makeText(getActivity(), "Already searching...", Toast.LENGTH_LONG).show();
                            return;
                        }
                        searchForMatches(objects, currentUser);
                    } else {
                        Log.e("SearchFragment", "Error querying for open conversations");
                    }
                }
            });
        } else {
            Log.e("SearchFragment", "Current user is somehow null");
        }
    }

    private void searchForMatches(final List<Conversation> openConvos, final ParseUser currentUser) {
        final ParseQuery<Conversation> fullConvosQuery1 = new Conversation.Query();
        fullConvosQuery1.whereEqualTo("user1", currentUser);

        final ParseQuery<Conversation> fullConvosQuery2 = new Conversation.Query();
        fullConvosQuery2.whereEqualTo("user2", currentUser);

        List<ParseQuery<Conversation>> queries = new ArrayList<ParseQuery<Conversation>>();
        queries.add(fullConvosQuery1);
        queries.add(fullConvosQuery2);

        ParseQuery<Conversation> mainQuery = ParseQuery.or(queries).include("user1").include("user2");
        mainQuery.findInBackground(new FindCallback<Conversation>() {
            public void done(List<Conversation> results, ParseException e) {
                // results has the list of full conversations in which the current user is participating in
                for (int i = 0; i < openConvos.size(); i++) {
                    if (checkNotAlreadyMatched(openConvos.get(i).getUser1(), listAlreadyMatched(currentUser, results))) {
                        //possible to get first/last name?
                        ParseUser check = openConvos.get(i).getUser1();
                        Toast.makeText(getActivity(), "Match found! " + openConvos.get(i).getUser1().getUsername(), Toast.LENGTH_LONG).show();
                        openConvos.get(i).setUser2(currentUser);
                        openConvos.get(i).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("SearchFragment", "You have joined the conversation!");
                                    //start chat activity between currentUser and objects.get(i).getUser1()
                                    //other user gets notif
                                } else {
                                    Log.e("SearchFragment", "Error when joining conversation");
                                }
                            }
                        });
                        return;
                    }
                }
                //create new convo if there does not already exist open convo with only current user
                Toast.makeText(getActivity(), "Match not found... starting new conversation...", Toast.LENGTH_LONG).show();
                createConversation(currentUser);
            }
        });
    }

    private void createConversation(ParseUser currentUser) {
        final Conversation newConvo = new Conversation();
        newConvo.setUser1(currentUser);

        newConvo.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("SearchFragment", "Create conversation success!");
                } else {
                    Log.e("SearchFragment", "Creating conversation failed :(");
                }
            }
        });
    }

    //returns true if current user has open convo, otherwise returns false
    public boolean hasOpenConvo(ParseUser currentUser, List<Conversation> openConvos) {
        for (int i = 0; i < openConvos.size(); i++) {
            if (openConvos.get(i).getUser1().getObjectId().equals(currentUser.getObjectId())) {
                return true;
            }
        }
        return false;
    }

    //goes through full conversations containing current user and returns a list of users already matched with the current user
    public List<ParseUser> listAlreadyMatched(ParseUser currentUser, List<Conversation> fullConvos) {
        List<ParseUser> currentMatches = new ArrayList<>();

        for (int i = 0; i < fullConvos.size(); i++) {
            if (fullConvos.get(i).getUser1().getObjectId().equals(currentUser.getObjectId())) {
                currentMatches.add(fullConvos.get(i).getUser2());
            } else if (fullConvos.get(i).getUser2().getObjectId().equals(currentUser.getObjectId())) {
                currentMatches.add(fullConvos.get(i).getUser1());
            }
        }
        return currentMatches;
    }

    //returns true if the users are not already matched, otherwise returns false
    public boolean checkNotAlreadyMatched(ParseUser otherUser, List<ParseUser> currentMatches) {
        for (int i = 0; i < currentMatches.size(); i++) {
            if (otherUser.getObjectId().equals(currentMatches.get(i).getObjectId())) {
                return false;
            }
        }
        return true;
    }
}
