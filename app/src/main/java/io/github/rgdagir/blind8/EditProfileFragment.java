package io.github.rgdagir.blind8;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import io.github.rgdagir.blind8.models.Interest;
import io.github.rgdagir.blind8.models.UserInterest;
import io.github.rgdagir.blind8.utils.BitmapScaler;
import io.github.rgdagir.blind8.utils.Utils;

public class EditProfileFragment extends Fragment {
    private Context context;
    private ParseUser currUser;
    public final String APP_TAG = "Blind8";
    private EditProfileFragment.OnFragmentInteractionListener mListener;
    private ImageView submitChanges;
    private ImageView arrowBack;

    private TextView savingIndicator;
    private ImageView profilePic;
    private FloatingActionButton changeProfilePic;
    public final static int PICK_PHOTO_CODE = 1046;
    private ArrayList<String> images;
    private ArrayList<Interest> mInterests;
    private ArrayList<Boolean> mIsInCommon;
    private ArrayList<String> mStrInterests;
    private InterestAdapter interestAdapter;
    PickGalleryAdapter galleryAdapter;
    RecyclerView rvGalleryPicker;
    private ImageButton addInterest;

    private EditText editName;
    private EditText editEmail;
    private EditText editBio;
    private EditText editOccupation;
    private EditText editEducation;
    private EditText editAlias;

    private Spinner myGenderSpinner;
    private Spinner interestedInSpinner;
    private CrystalSeekbar rangeDistanceSeekBar;
    private TextView displayProgress;
    private HashMap changes;
    private RecyclerView rvEditInterests;
    private AutoCompleteTextView mAutoCompleteInterests;
    private static TextInputLayout autoCompleteTxtLayout;
    private static String[] interestsArray;
    private static HashMap<String, Interest> officialInterests;
    private Button deleteAccountBtn;

    public EditProfileFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        context = getActivity();
        currUser = ParseUser.getCurrentUser();
        changes = new HashMap<String, String>();
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        setupViews(v);
        fetchCurrentUserAndLoadPage();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void setupViews(View v){
        // associating views from xml file with the Java class
        submitChanges = v.findViewById(R.id.done);
        arrowBack = v.findViewById(R.id.goBackArrow);

        savingIndicator = v.findViewById(R.id.tvSaving);
        savingIndicator.setVisibility(View.INVISIBLE);
        profilePic = v.findViewById(R.id.profilePic);
        changeProfilePic = v.findViewById(R.id.changeProfilePicBtn);

        editName = v.findViewById(R.id.editName);
        editEmail = v.findViewById(R.id.editEmail);
        editBio = v.findViewById(R.id.editBio);
        editEducation = v.findViewById(R.id.editEducation);
        editOccupation = v.findViewById(R.id.editOccupation);
        editAlias = v.findViewById(R.id.editAlias);

        myGenderSpinner = v.findViewById(R.id.myGender);
        interestedInSpinner = v.findViewById(R.id.interestedInGender);
        rangeDistanceSeekBar = v.findViewById(R.id.rangeDistanceSeekBar);
        displayProgress = v.findViewById(R.id.distanceProgress);
        addInterest = v.findViewById(R.id.addInterestImageButton);
        deleteAccountBtn = v.findViewById(R.id.deleteAccountButton);

        rvEditInterests = v.findViewById(R.id.rvEditInterests);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(context);
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        mInterests = new ArrayList<>();
        mIsInCommon = new ArrayList<>();
        mStrInterests = new ArrayList<>();
        officialInterests = new HashMap<>();
        interestAdapter = new InterestAdapter(mInterests, mIsInCommon);
        rvEditInterests.setLayoutManager(layoutManager);
        rvEditInterests.setAdapter(interestAdapter);
        mAutoCompleteInterests = v.findViewById(R.id.autoCompleteInterests);

        autoCompleteTxtLayout = v.findViewById(R.id.autoCompleteTxtLayout);

        // query for all possible interests in the database
        final ParseQuery<Interest> interestQuery = new Interest.Query();
        interestQuery.findInBackground(new FindCallback<Interest>() {
            @Override
            public void done(List<Interest> objects, ParseException e) {
                if (e == null) {
                    interestsArray = new String[objects.size()];
                    for (int i = 0; i < objects.size(); ++i) {
                        Interest interest = objects.get(i);
                        interestsArray[i] = interest.getName();
                        officialInterests.put(interest.getName(), interest);
                    }
                    setupAutoCompleteInterests();
                } else {
                    e.printStackTrace();
                }
            }
        });

        setupButtonListeners();
        setupTextContainerListeners();
        setupGallery(v);
        setupSpinners(v);
        setupRangeBar();
        setupCrystalSeekBar(v);
        fetchUserInterests();
    }

    private void fetchUserInterests() {
        final UserInterest.Query interestsQuery = new UserInterest.Query();
        interestsQuery.whereEqualTo("user", currUser);
        interestsQuery.withUserInterest();
        interestsQuery.findInBackground(new FindCallback<UserInterest>() {
            @Override
            public void done(List<UserInterest> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); ++i) {
                        Interest interest = objects.get(i).getInterest();
                        mStrInterests.add(interest.getName());
                        mInterests.add(interest);
                        mIsInCommon.add(false);
                        interestAdapter.notifyItemInserted(mInterests.size() - 1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupAutoCompleteInterests(){
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, interestsArray);
        mAutoCompleteInterests.setThreshold(0);
        mAutoCompleteInterests.setAdapter(adapter);
    }

    public boolean isValid(String str){
        return (Arrays.asList(interestsArray).contains(str));
    }

    private void setupButtonListeners() {
        // adding listeners to buttons
        submitChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Done button", "pressed!");
                deleteUserInterestsThenCreateTheNewOnes();
            }
        });
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Arrow button", "pressed!");
                mListener.goBackToProfile();
            }
        });
        changeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });
        addInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strInterest = mAutoCompleteInterests.getText().toString();
                if (isValid(strInterest)){
                    if (!mStrInterests.contains(strInterest)) {
                        mStrInterests.add(strInterest);
                        autoCompleteTxtLayout.setError(null);
                        Interest interest = officialInterests.get(mAutoCompleteInterests.getText().toString());
                        mInterests.add(interest);
                        mIsInCommon.add(false);
                        interestAdapter.notifyItemInserted(mInterests.size() - 1);
                    } else {
                        autoCompleteTxtLayout.setError("You're already interested in that :)");
                    }
                    mAutoCompleteInterests.setText(null);
                } else {
                    autoCompleteTxtLayout.setError("Invalid interest. Please select one from the list");
                }
            }
        });

        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDemoUser();
            }
        });
    }

    private void createUserInterests() {
        for (int i = 0; i < mInterests.size(); i++) {
            UserInterest userInterest = new UserInterest();
            userInterest.put("user", currUser);
            userInterest.put("interest", mInterests.get(i));
            userInterest.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.e("ChatActivity", "Creating user interest success! :)");
                    } else {
                        Log.e("ChatActivity", "Creating user interest failed :(");
                    }
                }
            });
        }
        saveUpdatedUser();
        mListener.goBackToProfile();
    }

    public void deleteUserInterestsThenCreateTheNewOnes(){
        ParseQuery<UserInterest> query = new UserInterest.Query();
        query.whereEqualTo("user", currUser);
        query.findInBackground(new FindCallback<UserInterest>() {
            @Override
            public void done(List<UserInterest> objects, ParseException e) {
                for (UserInterest ui : objects){
                    ui.deleteInBackground();
                }
                createUserInterests();
            }
        });
    }

    public void setupGallery(View v){
        rvGalleryPicker = v.findViewById(R.id.rvGalleryPics);
        images = fetchCoverImages(ParseUser.getCurrentUser());
        galleryAdapter = new PickGalleryAdapter(images, this);
        rvGalleryPicker.setAdapter(galleryAdapter);
        rvGalleryPicker.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
    }

    public void setupCrystalSeekBar(View view){
        // get seekbar from view
        final CrystalRangeSeekbar rangeSeekbar = view.findViewById(R.id.crystalRangeAgeSeekBar);

        // get min and max text view
        final TextView tvMin = view.findViewById(R.id.rangeSeekBarMin);
        final TextView tvMax = view.findViewById(R.id.rangeSeekBarMax);
        rangeSeekbar.setMinStartValue(currUser.getNumber("minAge").floatValue());
        rangeSeekbar.setMaxStartValue(currUser.getNumber("maxAge").floatValue());
        rangeSeekbar.apply();

        // set listener
        rangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                tvMin.setText(String.valueOf(minValue));
                tvMax.setText(String.valueOf(maxValue));
            }
        });

        // set final value listener
        rangeSeekbar.setOnRangeSeekbarFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                Log.d("CRS=>", String.valueOf(minValue) + " : " + String.valueOf(maxValue));
                changes.put("minAge", String.valueOf(minValue));
                changes.put("maxAge", String.valueOf(maxValue));
            }
        });
    }

    public ArrayList<String> fetchCoverImages(ParseUser user){
        ArrayList<String> coverImages = new ArrayList<>();
        ParseFile photo0 = user.getParseFile("coverPhoto0");
        ParseFile photo1 = user.getParseFile("coverPhoto1");
        ParseFile photo2 = user.getParseFile("coverPhoto2");
        ParseFile photo3 = user.getParseFile("coverPhoto3");
        if (photo0 != null) {
            coverImages.add(photo0.getUrl());
        } else {
            coverImages.add("placeholder");
        }
        if (photo1 != null) {
            coverImages.add(photo1.getUrl());
        } else {
            coverImages.add("placeholder");
        }
        if (photo2 != null) {
            coverImages.add(photo2.getUrl());
        } else {
            coverImages.add("placeholder");
        }
        if (photo3 != null) {
            coverImages.add(photo3.getUrl());
        } else {
            coverImages.add("placeholder");
        }
        return coverImages;
    }

    private void setupTextContainerListeners() {
        // adding listeners to text containers
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                changes.put("firstName", s);
            }
        });
        editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                changes.put("email", s.toString());
                changes.put("username", s.toString());
            }
        });
        editBio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                changes.put("bio", s.toString());
            }
        });
        editOccupation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                changes.put("occupation", s.toString());
            }
        });
        editEducation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                changes.put("education", s.toString());
            }
        });
        editAlias.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                changes.put("fakeName", s.toString());
            }
        });
    }

    public void fetchCurrentUserAndLoadPage(){
        // populate screen
        editName.setText(currUser.getString("firstName"));
        editEmail.setText(currUser.getString("email"));
        editBio.setText(currUser.getString("bio"));
        editEducation.setText(currUser.getString("education"));
        editOccupation.setText(currUser.getString("occupation"));
        editAlias.setText(currUser.getString("fakeName"));
        Glide.with(context).load(currUser.getParseFile("profilePic").getUrl())
                .asBitmap().centerCrop().dontAnimate()
                .placeholder(R.mipmap.ic_picture)
                .error(R.mipmap.ic_picture)
                .into(new BitmapImageViewTarget(profilePic) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        profilePic.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    public void setupSpinners(View v){
        String userGender = currUser.getString("gender");
        String userInterest = currUser.getString("interestedIn");
        final String[] genders = getResources().getStringArray(R.array.genders);
        final String[] preferences = getResources().getStringArray(R.array.preferences);
        // create spinners
        myGenderSpinner = v.findViewById(R.id.myGender);
        interestedInSpinner = v.findViewById(R.id.interestedInGender);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(context,
                R.array.genders, android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> interestedAdapter = ArrayAdapter.createFromResource(context,
                R.array.preferences, android.R.layout.simple_spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        interestedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        myGenderSpinner.setAdapter(genderAdapter);
        myGenderSpinner.setSelection(genderAdapter.getPosition(userGender), true);
        // Apply the adapter to the spinner
        interestedInSpinner.setAdapter(interestedAdapter);
        interestedInSpinner.setSelection(interestedAdapter.getPosition(userInterest), true);
        myGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changes.put("gender", genders[position]);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        interestedInSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changes.put("interestedIn", preferences[position]);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void setupRangeBar(){
        displayProgress.setText(String.valueOf(currUser.getNumber("matchRange")) + " mi.");
        rangeDistanceSeekBar.setMinStartValue((Integer) currUser.getNumber("matchRange")).apply();
        rangeDistanceSeekBar.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number value) {
                displayProgress.setText(Integer.toString(value.intValue()) + " mi.");
                changes.put("matchRange", Integer.toString(value.intValue()));
            }
        });
    }

    public void saveUpdatedUser() {
        Iterator it = changes.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) it.next();
            if (entry.getKey().toString().equals("matchRange")) {
                int range = Integer.parseInt(entry.getValue().toString());
                currUser.put("matchRange", range);
                continue;
            }
            if (entry.getKey().toString().equals("minAge")) {
                int num = Integer.parseInt(entry.getValue().toString());
                currUser.put("minAge", num);
                continue;
            }
            if (entry.getKey().toString().equals("maxAge")) {
                int num = Integer.parseInt(entry.getValue().toString());
                currUser.put("maxAge", num);
                continue;
            }
            currUser.put(entry.getKey().toString(), entry.getValue().toString());
            System.out.println("key: " + entry.getKey().toString() + "value: " + entry.getValue().toString());
            it.remove(); // avoids a ConcurrentModificationException
        }
        currUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                Log.d("EditProfile", "Success!");
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EditProfileFragment.OnFragmentInteractionListener) {
            mListener = (EditProfileFragment.OnFragmentInteractionListener) context;
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
        void goBackToProfile();
    }

    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (i.resolveActivity(context.getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(i, PICK_PHOTO_CODE);
        }
    }

    // after user picks image to upload from their gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri photoUri = data.getData();
            // Do something with the photo based on Uri
            final Bitmap selectedImage;
            byte[] img;
            try {
                Bitmap rawSelectedImage = BitmapScaler.getCorrectlyOrientedImage(context, photoUri);
                if (requestCode == PICK_PHOTO_CODE){ // profile pic
                    selectedImage = resizeImage(rawSelectedImage, "profilePic");
                    img = Utils.getbytearray(selectedImage);
                    // Load the selected image into a preview
                    Glide.with(context).load(photoUri)
                            .asBitmap().centerCrop().dontAnimate()
                            .placeholder(R.drawable.ic_action_name)
                            .error(R.drawable.ic_action_name)
                            .into(new BitmapImageViewTarget(profilePic) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    profilePic.setImageDrawable(circularBitmapDrawable);
                                }
                            });
                    ParseFile imageFile = new ParseFile(currUser.getObjectId() + "profilePic_resized.jpg", img);
                    currUser.put("profilePic", imageFile);
                    savingIndicator.setVisibility(View.VISIBLE);
                    currUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            savingIndicator.setVisibility(View.INVISIBLE);
                        }
                    });
                } else if (requestCode == PICK_PHOTO_CODE + 1) { // first gallery pic
                    selectedImage = resizeImage(rawSelectedImage, "galleryPic");
                    img = Utils.getbytearray(selectedImage);
                    processNewCoverPhoto(0, photoUri.toString(), img);
                } else if (requestCode == PICK_PHOTO_CODE + 2) { // second gallery pic
                    selectedImage = resizeImage(rawSelectedImage, "galleryPic");
                    img = Utils.getbytearray(selectedImage);
                    processNewCoverPhoto(1, photoUri.toString(), img);
                } else if (requestCode == PICK_PHOTO_CODE + 3) { // third gallery pic
                    selectedImage = resizeImage(rawSelectedImage, "galleryPic");
                    img = Utils.getbytearray(selectedImage);
                    processNewCoverPhoto(2, photoUri.toString(), img);
                } else if (requestCode == PICK_PHOTO_CODE + 4) { // third gallery pic
                    selectedImage = resizeImage(rawSelectedImage, "galleryPic");
                    img = Utils.getbytearray(selectedImage);
                    processNewCoverPhoto(3, photoUri.toString(), img);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processNewCoverPhoto(int position, String fileUri, byte[] image) {
        images.remove(position);
        images.add(position, fileUri);
        galleryAdapter.notifyDataSetChanged();
        rvGalleryPicker.scrollToPosition(0);
        ParseFile imageFile = new ParseFile(currUser.getObjectId() + "galleryPic_resized.jpg", image);
        currUser.put("coverPhoto" + Integer.toString(position), imageFile);
        savingIndicator.setVisibility(View.VISIBLE);
        currUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                savingIndicator.setVisibility(View.INVISIBLE);
            }
        });
    }

    // RESIZE BITMAP
    private Bitmap resizeImage(Bitmap rawImage, String imagePrefix) {
        Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawImage, 800);
        // Configure byte output stream
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // Compress the image further
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
        final File resizedFile = getPhotoFileUri(currUser.getObjectId() + imagePrefix + "_resized.jpg");
        // Write the bytes of the bitmap to file
        try {
            FileOutputStream fos = new FileOutputStream(resizedFile);
            fos.write(bytes.toByteArray());
            fos.close();
            Log.d("SaveImage", "the image was compressed correctly");
        } catch (IOException e) {
            Log.d("SaveImage", "problem compressing");
            e.printStackTrace();
        }
        return resizedBitmap;
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void resetDemoUser() {
        // delete all interests associated w/ demo user
        ParseQuery<UserInterest> interestQuery = ParseQuery.getQuery("UserInterest");
        interestQuery.whereEqualTo("user", currUser);
        interestQuery.findInBackground(new FindCallback<UserInterest>() {
            @Override
            public void done(List<UserInterest> objects, ParseException e) {
                if (e == null) {
                    for (UserInterest interest : objects) {
                        interest.deleteInBackground();
                    }
                    Toast.makeText(context, "User's interests deleted successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    e.printStackTrace();
                }
            }
        });
        // delete demo user
        currUser.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(context, "User deleted successfully!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        currUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                Intent goToLogin = new Intent(context, LoginActivity.class);
                goToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // set current user on installation to null
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put("currentUserId", "");
                installation.saveInBackground();
                startActivity(goToLogin);
            }
        });
    }
}
