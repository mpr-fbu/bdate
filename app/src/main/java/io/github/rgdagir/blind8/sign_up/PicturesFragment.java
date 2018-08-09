package io.github.rgdagir.blind8.sign_up;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.parse.ParseImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import io.github.rgdagir.blind8.PickGalleryAdapter;
import io.github.rgdagir.blind8.R;
import io.github.rgdagir.blind8.utils.BitmapScaler;
import io.github.rgdagir.blind8.utils.Utils;

public class PicturesFragment extends Fragment {

    private PicturesFragment.OnFragmentInteractionListener mListener;
    private Context context;
    private TextView savingIndicator;
    private ParseImageView profilePic;
    private FloatingActionButton changeProfilePic;
    public final static int PICK_PHOTO_CODE = 1046;
    ArrayList<String> displayImages;
    PickGalleryAdapter galleryAdapter;
    RecyclerView rvGalleryPicker;
    private Button finish;
    final String APP_TAG = "Blind8";

    public PicturesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pictures, container, false);
        context = getActivity();
        setupFragmentVariables(view);
        setupButtonListeners();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PicturesFragment.OnFragmentInteractionListener) {
            mListener = (PicturesFragment.OnFragmentInteractionListener) context;
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
    }

    public interface OnFragmentInteractionListener {
        void createNewUser();
        void addPicturesToUser(String itemName, byte[] image);
    }

    private void setupFragmentVariables(View view) {
        savingIndicator = getActivity().findViewById(R.id.tvSaving);
        savingIndicator.setVisibility(View.INVISIBLE);
        profilePic = view.findViewById(R.id.profilePic);
        changeProfilePic = view.findViewById(R.id.changeProfilePicBtn);
        rvGalleryPicker = view.findViewById(R.id.rvGalleryPics);
        displayImages = setupCoverImages();
        galleryAdapter = new PickGalleryAdapter(displayImages, this);
        rvGalleryPicker.setAdapter(galleryAdapter);
        rvGalleryPicker.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        finish = view.findViewById(R.id.finish);
    }

    private void setupButtonListeners() {
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.createNewUser();
            }
        });

        changeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });
    }

    public ArrayList<String> setupCoverImages(){
        ArrayList<String> coverImages = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            coverImages.add("placeholder");
        }
        return coverImages;
    }

    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (i.resolveActivity(context.getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(i, PICK_PHOTO_CODE);
        }
    }

    public void loadPhoto(final Context context, Uri fileUri, final ImageView container){
        // Load the selected image into a preview
        Glide.with(context).load(fileUri)
                .asBitmap().centerCrop().dontAnimate()
                .placeholder(R.drawable.ic_action_name)
                .error(R.drawable.ic_launcher_background)
                .into(new BitmapImageViewTarget(profilePic) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        container.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    // after user picks image to upload from their gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            savingIndicator.setVisibility(View.VISIBLE);
            Uri photoUri = data.getData();
            // Do something with the photo based on Uri
            final Bitmap selectedImage;
            byte[] img;
            try {
                Bitmap rawSelectedImage = BitmapScaler.getCorrectlyOrientedImage(context, photoUri);
                if (requestCode == PICK_PHOTO_CODE){ // profile pic
                    selectedImage = resizeImage(rawSelectedImage, "profilePic");
                    img = Utils.getbytearray(selectedImage);
                    loadPhoto(context, photoUri, profilePic);
                    mListener.addPicturesToUser("profilePic", img);
                } else if (requestCode == PICK_PHOTO_CODE + 1) { // first gallery pic
                    selectedImage = resizeImage(rawSelectedImage, "coverPhoto0");
                    img = Utils.getbytearray(selectedImage);
                    addToAdapter(0, photoUri.toString());
                    mListener.addPicturesToUser("coverPhoto0", img);
                } else if (requestCode == PICK_PHOTO_CODE + 2) { // second gallery pic
                    selectedImage = resizeImage(rawSelectedImage, "coverPhoto1");
                    img = Utils.getbytearray(selectedImage);
                    addToAdapter(1, photoUri.toString());
                    mListener.addPicturesToUser("coverPhoto1", img);
                } else if (requestCode == PICK_PHOTO_CODE + 3) { // third gallery pic
                    selectedImage = resizeImage(rawSelectedImage, "coverPhoto2");
                    img = Utils.getbytearray(selectedImage);
                    addToAdapter(2, photoUri.toString());
                    mListener.addPicturesToUser("coverPhoto2", img);
                } else if (requestCode == PICK_PHOTO_CODE + 4) { // third gallery pic
                    selectedImage = resizeImage(rawSelectedImage, "coverPhoto3");
                    img = Utils.getbytearray(selectedImage);
                    addToAdapter(3, photoUri.toString());
                    mListener.addPicturesToUser("coverPhoto3", img);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addToAdapter(int position, String fileUri) {
        displayImages.remove(position);
        displayImages.add(position, fileUri);
        galleryAdapter.notifyDataSetChanged();
        rvGalleryPicker.scrollToPosition(0);
    }

    // RESIZE BITMAP
    private Bitmap resizeImage(Bitmap rawImage, String imagePrefix) {
        Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawImage, 800);
        // Configure byte output stream
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // Compress the image further
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
        final File resizedFile = getPhotoFileUri(imagePrefix + "_resized.jpg");
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
