package io.github.rgdagir.blind8;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.rgdagir.blind8.models.Conversation;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class SearchFragment extends Fragment {
    private SearchFragment.OnFragmentInteractionListener mListener;
    private Button searchButton;
    private static final int LOCATION_PERMISSION_REQUEST = 1;
    private Context context;
    private ParseGeoPoint myLoc;
    private ParseUser currentUser;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private SeekBar rangeMatchBar;
    private int range;
    private Circle circle;
    private TextView mTvRange;
    private View mCustomMarkerView;
    private ImageView mMarkerImageView;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = ParseUser.getCurrentUser();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        mMapView = rootView.findViewById(R.id.mapView);
        mTvRange = rootView.findViewById(R.id.tvRange);
        searchButton = rootView.findViewById(R.id.btnSearch);
        context = getActivity();
        rangeMatchBar = rootView.findViewById(R.id.matchRangeBar);
        mCustomMarkerView = inflater.inflate(R.layout.map_marker, null);
        mMarkerImageView = mCustomMarkerView.findViewById(R.id.profile_image);
        setupMap(savedInstanceState);
        getLocationPermissionsIfNeeded(rootView, savedInstanceState);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setupListeners();
    }

    public void setupListeners(){
        // Setup any handles to view objects here
        rangeMatchBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                range = progress; // in miles
                mTvRange.setText(String.valueOf(range) + " mi.");
                circle.setRadius(range * 1609.4); // in meters
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchForOpenConversations();
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

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public interface OnFragmentInteractionListener {
        // Placeholder, to be inserted when clicking is introduced
        void onFragmentInteraction(Uri uri);
    }

    /* Search for convos that have only one user, and
       match current user to other user if they're not already matched.
       Filters for age and gender.
       If all convos are full, create a new convo with only the current user.
       A new convo must only have User1; User2 should be null
       Users cannot open more than one new convo */

    public void searchForOpenConversations() {
        final Conversation.Query openConversationsQuery = new Conversation.Query();
        openConversationsQuery.whereDoesNotExist("user2");
        filterForAgeAndGender(openConversationsQuery);

        //ensures that currentUser is not filtered out if they have a conversation open
        final ParseQuery<Conversation> currentUserQuery = new Conversation.Query();
        currentUserQuery.whereDoesNotExist("user2");
        currentUserQuery.whereEqualTo("user1", currentUser);

        List<ParseQuery<Conversation>> queries = new ArrayList<>();
        queries.add(openConversationsQuery);
        queries.add(currentUserQuery);

        ParseQuery<Conversation> query = ParseQuery.or(queries).include("user1");
        query.findInBackground(new FindCallback<Conversation>() {
            @Override
            public void done(final List<Conversation> objects, ParseException e) {
                if (e == null) {
                    // objects has list of open conversations
                    if (hasOpenConvo(currentUser, objects)) {
                        Toast.makeText(context, "Still searching...", Toast.LENGTH_LONG).show();
                        return;
                    }
                    searchForMatches(objects, currentUser);
                } else {
                    Log.e("SearchFragment", "Error querying for open conversations");
                }
            }
        });
    }

    private void filterForAgeAndGender(final Conversation.Query query) {
        if (currentUser.getString("interestedIn").equals("Male")) {
            query.whereEqualTo("user1Gender", "Male");
        } else if (currentUser.getString("interestedIn").equals("Female")) {
            query.whereEqualTo("user1Gender", "Female");
        }
        query.whereGreaterThan("user1MaxAge", (Integer) currentUser.getNumber("age") - 1);
        query.whereLessThan("user1MinAge", (Integer) currentUser.getNumber("age") + 1);
    }

    private void searchForMatches(final List<Conversation> openConversations, final ParseUser currentUser) {
        final ParseQuery<Conversation> fullConversationsQuery1 = new Conversation.Query();
        fullConversationsQuery1.whereEqualTo("user1", currentUser);

        final ParseQuery<Conversation> fullConversationsQuery2 = new Conversation.Query();
        fullConversationsQuery2.whereEqualTo("user2", currentUser);

        List<ParseQuery<Conversation>> queries = new ArrayList<>();
        queries.add(fullConversationsQuery1);
        queries.add(fullConversationsQuery2);

        ParseQuery<Conversation> mainQuery = ParseQuery.or(queries).include("user1").include("user2");
        mainQuery.findInBackground(new FindCallback<Conversation>() {
            public void done(final List<Conversation> results, ParseException e) {
                // results has the list of full conversations in which the current user is participating in
                for (int i = 0; i < openConversations.size(); i++) {
                    final Conversation conversation = openConversations.get(i);
                    if (checkNotAlreadyMatched(conversation.getUser1(), listAlreadyMatched(currentUser, results))
                            && checkIfInRange(conversation, currentUser)
                            ) {
                        Toast.makeText(getActivity(), "Match found! Say hello to "
                                + conversation.getUser1().getString("fakeName") + "!", Toast.LENGTH_LONG).show();
                        addCurrentUserToConversation(conversation);
                        return;
                    }
                }
                // create new convo if there does not already exist open convo with only current user
                Toast.makeText(getContext(), "Searching...", Toast.LENGTH_LONG).show();
                createConversation(currentUser);
            }
        });
    }

    private void addCurrentUserToConversation(final Conversation conversation) {
        conversation.setUser2(currentUser);
        conversation.setReadUser2(false);
        conversation.setReadUser1(false);
        conversation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("SearchFragment", "You have joined the conversation!");
                    // start chat activity between currentUser and objects.get(i).getUser1()
                    sendConversationPushNotification(conversation);
                } else {
                    Log.e("SearchFragment", "Error when joining conversation");
                }
            }
        });
        //goToChatList();
    }

    private void createConversation(final ParseUser currentUser) {
        final Conversation newConvo = new Conversation();

        currentUser.fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser currentUser, ParseException e) {
                newConvo.setUser1MinAge((Integer) currentUser.getNumber("minAge"));
                newConvo.setUser1MaxAge((Integer) currentUser.getNumber("maxAge"));
                newConvo.setUser1(currentUser);
                newConvo.setExchanges(0);
                newConvo.setUser1Gender(currentUser.getString("gender"));
                if (myLoc != null) {
                    newConvo.setMatchLocation(myLoc);
                }
                newConvo.setMatchRange(range);

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
        });
    }

    // returns true if current user has open convo, otherwise returns false
    private boolean hasOpenConvo(ParseUser currentUser, List<Conversation> openConversations) {
        for (int i = 0; i < openConversations.size(); i++) {
            if (openConversations.get(i).getUser1().getObjectId().equals(currentUser.getObjectId())) {
                return true;
            }
        }
        return false;
    }

    // goes through full conversations containing current user
    // returns a list of users already matched with the current user
    private List<ParseUser> listAlreadyMatched(ParseUser currentUser, List<Conversation> fullConversations) {
        List<ParseUser> currentMatches = new ArrayList<>();

        for (int i = 0; i < fullConversations.size(); i++) {
            if (fullConversations.get(i).getUser1().getObjectId().equals(currentUser.getObjectId())) {
                currentMatches.add(fullConversations.get(i).getUser2());
            } else if (fullConversations.get(i).getUser2().getObjectId().equals(currentUser.getObjectId())) {
                currentMatches.add(fullConversations.get(i).getUser1());
            }
        }
        return currentMatches;
    }

    // returns true if the users are not already matched, otherwise returns false
    private boolean checkNotAlreadyMatched(ParseUser otherUser, List<ParseUser> currentMatches) {
        for (int i = 0; i < currentMatches.size(); i++) {
            if (otherUser.getObjectId().equals(currentMatches.get(i).getObjectId())) {
                return false;
            }
        }
        return true;
    }

    private void sendConversationPushNotification(Conversation conversation) {
        // other user gets notification of new match
        HashMap<String, String> payload = new HashMap<>();
        ParseUser recipient = conversation.getUser1();
        payload.put("receiver", recipient.getObjectId());
        payload.put("newData", getString(R.string.new_conversation_notification));
        ParseCloud.callFunctionInBackground("pushNotificationGeneral", payload);
    }

    public boolean checkIfInRange(Conversation conversation, ParseUser user) {
        // get distance (in miles) between user who started the conversation and the one trying to match
        double distanceFromMatch = calcDistance(conversation.getMatchLocation().getLatitude(), user.getParseGeoPoint("lastLocation").getLatitude(),
                conversation.getMatchLocation().getLongitude(),
                user.getParseGeoPoint("lastLocation").getLongitude(), 0, 0);
        return (distanceFromMatch <= conversation.getMatchRange());
    }

    @SuppressLint("MissingPermission")
    public void getLastLoc() {
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(context);
        locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // GPS location can be null if GPS is switched off
                if (location != null) {
                    myLoc = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                    displayMap(myLoc);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("MapDemoActivity", "Error trying to get last GPS location");
                e.printStackTrace();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getLocationPermissionsIfNeeded(View rootView, Bundle savedInstance) {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If the statement above is true, then no permission was granted
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // if the statement above is false, it means that the user has previously denied the request for that permission
                // TODO - give user an explanation of how location will be used before the actual request is made
            }
            // Request permission
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            getLastLoc();
        }
    }

    // Callback function for location request
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Location permission granted", Toast.LENGTH_SHORT).show();
                getLastLoc();
            } else {
                boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
                if (showRationale) {
                    // TODO - handle this situation when the user has denied permission
                } else {
                    Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // Returns distance in miles between two coordinates
    public static double calcDistance(double lat1, double lat2, double lon1,
                                      double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return (Math.sqrt(distance)) / 1609.34;
    }

    protected void setupMap(Bundle savedInstanceState){
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize((context.getApplicationContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // will be called inside the callback in getLastLoc
    public void displayMap(final ParseGeoPoint location) {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;

                // For showing a move to my location button
                mGoogleMap.setMyLocationEnabled(true);

                // For dropping a marker at a point on the Map
                LatLng pin;
                pin = new LatLng(location.getLatitude(), location.getLongitude());
//                addCustomMarkerFromDatabase(pin);
                mGoogleMap.setMyLocationEnabled(false);
                drawRangeAndSetZoom(mGoogleMap, pin);
            }
        });
    }

    public void drawRangeAndSetZoom(GoogleMap gMap, LatLng pin) {
        range = Integer.parseInt(currentUser.get("matchRange").toString()); // in miles
        // Instantiates a new CircleOptions object and defines the center and radius
        CircleOptions circleOptions = new CircleOptions()
                .center(pin)
                .radius(range * 1609.34) // In meters
                .strokeWidth(0)
                .fillColor(R.color.mediumBlue);
        // Get back the mutable Circle
        circle = gMap.addCircle(circleOptions);

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(pin).zoom(10).build();
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

//    TODO - set up handlers for location request denials below
//    // Annotate a method which explains why the permission/s is/are needed.
//    // It passes in a `PermissionRequest` object which can continue or abort the current permission
//    @OnShowRationale(Manifest.permission.ACCESS_FINE_LOCATION)
//    void showRationaleForPhoneCall(PermissionRequest request) {
//        new AlertDialog.Builder(this)
//                .setMessage(R.string.permission_phone_rationale)
//                .setPositiveButton(R.string.button_allow, (dialog, button) -> request.proceed())
//                .setNegativeButton(R.string.button_deny, (dialog, button) -> request.cancel())
//                .show();
//    }
//
//    // Annotate a method which is invoked if the user doesn't grant the permissions
//    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
//    void showDeniedForPhoneCall() {
//        Toast.makeText(this, R.string.permission_call_denied, Toast.LENGTH_SHORT).show();
//    }
//
//    // Annotates a method which is invoked if the user
//    // chose to have the device "never ask again" about a permission
//    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
//    void showNeverAskForPhoneCall() {
//        Toast.makeText(this, R.string.permission_call_neverask, Toast.LENGTH_SHORT).show();
//    }

//    private Bitmap getMarkerBitmapFromView(View view, Bitmap bitmap) {
//
//        mMarkerImageView.setImageBitmap(bitmap);
//        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
//                Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(returnedBitmap);
//        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
//        Drawable drawable = view.getBackground();
//        if (drawable != null)
//            drawable.draw(canvas);
//        view.draw(canvas);
//        return returnedBitmap;
//    }
//
//    private void addCustomMarkerFromDatabase(final LatLng latLng) {
//
//        if (mGoogleMap == null) {
//            return;
//        }
//        // adding a marker with image from URL using glide image loading library
//        Glide.with(context)
//                .load(currentUser.getParseFile("profilePic").getUrl()).asBitmap().fitCenter()
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
//                        mMarkerImageView.setImageBitmap(bitmap);
//                        mGoogleMap.addMarker(new MarkerOptions().position(latLng)
//                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, bitmap))));
//
//                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
//
//                    }
//                });
//    }
}