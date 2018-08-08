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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
    ArrayList<String> images;
    PickGalleryAdapter galleryAdapter;
    RecyclerView rvGalleryPicker;

    private EditText editName;
    private EditText editEmail;
    private EditText editBio;
    private EditText editOccupation;
    private EditText editEducation;

    private Spinner myGenderSpinner;
    private Spinner interestedInSpinner;
    private SeekBar rangeDistanceSeekBar;
    private TextView displayProgress;
    private HashMap changes;

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
        View v = inflater.inflate(R.layout.new_fragment_edit_profile, container, false);
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

        myGenderSpinner = v.findViewById(R.id.myGender);
        interestedInSpinner = v.findViewById(R.id.interestedInGender);
        rangeDistanceSeekBar = v.findViewById(R.id.rangeDistanceSeekBar);
        displayProgress = v.findViewById(R.id.distanceProgress);

        setupButtonListeners();
        setupTextContainerListeners();
        setupGallery(v);
        setupSpinners(v);
        setupRangeBar();
        setupCrystalSeekBar(v);
    }

    private void setupButtonListeners() {
        // adding listeners to buttons
        submitChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Done button", "pressed!");
                saveUpdatedUser();
                mListener.goBackToProfile();
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
        tvMin.setText(String.valueOf(currUser.getNumber("minAge")));
        tvMax.setText(String.valueOf(currUser.getNumber("maxAge")));

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
    }

    public void fetchCurrentUserAndLoadPage(){
        // populate screen
        editName.setText(currUser.getString("firstName"));
        editEmail.setText(currUser.getString("email"));
        editBio.setText(currUser.getString("bio"));
        editEducation.setText(currUser.getString("education"));
        editOccupation.setText(currUser.getString("occupation"));
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
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.genders, android.R.layout.simple_spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        myGenderSpinner.setAdapter(adapter);
        myGenderSpinner.setSelection(adapter.getPosition(userGender), true);
        interestedInSpinner = v.findViewById(R.id.interestedInGender);
        // Apply the adapter to the spinner
        interestedInSpinner.setAdapter(adapter);
        interestedInSpinner.setSelection(adapter.getPosition(userInterest), true);
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
        displayProgress.setText(String.valueOf(currUser.getNumber("matchRange")));
        rangeDistanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                displayProgress.setText(Integer.toString(progress));
                changes.put("matchRange", Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
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
}
