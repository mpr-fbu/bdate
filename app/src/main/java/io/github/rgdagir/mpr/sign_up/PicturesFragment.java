package io.github.rgdagir.mpr.sign_up;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.rgdagir.mpr.R;
import io.github.rgdagir.mpr.utils.Utils;

public class PicturesFragment extends Fragment {

    private PicturesFragment.OnFragmentInteractionListener mListener;
    private Context context;
    private TextView title;
    private TextView explanation;
    private ImageView profilePic;
    private ImageView galleryPicOne;
    private ImageView galleryPicTwo;
    private ImageView galleryPicThree;
    private Button back;
    private Button finish;
    public final static int PICK_PHOTO_CODE = 1046;
    private List<ParseFile> images;
    private ParseFile imageFile;
    private Uri photoUri;



    public PicturesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pictures, container, false);
        images = new ArrayList<>();
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
        // Placeholder, to be inserted when clicking is introduced
        //void onFragmentInteraction(Uri uri);
        void onBackPressed();
        void createNewUser();
        void addPicturesToUser(List<ParseFile> files);
    }

    private void setupFragmentVariables(View view) {
        title = view.findViewById(R.id.title);
        explanation = view.findViewById(R.id.explanation);
        profilePic = view.findViewById(R.id.profilePic);
        galleryPicOne = view.findViewById(R.id.galleryPicOne);
        galleryPicTwo = view.findViewById(R.id.galleryPicTwo);
        galleryPicThree = view.findViewById(R.id.galleryPicThree);
        back = view.findViewById(R.id.back);
        finish = view.findViewById(R.id.finish);
    }

    private void setupButtonListeners() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onBackPressed();
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.addPicturesToUser(images);
                mListener.createNewUser();
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(0);
            }
        });
        galleryPicOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(1);
            }
        });
        galleryPicTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(2);
            }
        });
        galleryPicThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(3);
            }
        });
    }

    public void onPickPhoto(int position) {
        // Create intent for picking a photo from the gallery
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (i.resolveActivity(context.getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(i, PICK_PHOTO_CODE + position);
        }
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (data != null) {
            photoUri = data.getData();
            // Do something with the photo based on Uri
            Bitmap selectedImage = null;
            byte[] img = null;
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri);
                img = Utils.getbytearray(selectedImage);
                imageFile = new ParseFile(img);
                imageFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (requestCode == PICK_PHOTO_CODE){ // top left corner - profile pic
                            loadPhoto(context, photoUri, profilePic);
                            images.add(imageFile);
                        } else if (requestCode == PICK_PHOTO_CODE + 1) { // top right corner - first gallery pic
                            loadPhoto(context, photoUri, galleryPicOne);
                            images.add(imageFile);
                        } else if (requestCode == PICK_PHOTO_CODE + 2) { // bottom right corner - second gallery pic
                            loadPhoto(context, photoUri, galleryPicTwo);
                            images.add(imageFile);
                        } else if (requestCode == PICK_PHOTO_CODE + 3) { // bottom left corner - third gallery pic
                            loadPhoto(context, photoUri, galleryPicThree);
                            images.add(imageFile);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadPhoto(final Context context, Uri fileUri, final ImageView container){
        // Load the selected image into a preview
        Glide.with(context).load(fileUri)
                .asBitmap().centerCrop().dontAnimate()
                .placeholder(R.drawable.ic_action_name)
                .error(R.drawable.ic_action_name)
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

}
