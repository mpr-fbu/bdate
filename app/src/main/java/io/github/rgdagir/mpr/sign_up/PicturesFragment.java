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

import java.io.IOException;

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
    private Button finish;
    public final static int PICK_PHOTO_CODE = 1046;


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
        // Placeholder, to be inserted when clicking is introduced
        //void onFragmentInteraction(Uri uri);
        void onBackPressed();
        void createNewUser();
    }

    private void setupFragmentVariables(View view) {
        title = view.findViewById(R.id.title);
        explanation = view.findViewById(R.id.explanation);
        profilePic = view.findViewById(R.id.profilePic);
        galleryPicOne = view.findViewById(R.id.galleryPicOne);
        galleryPicTwo = view.findViewById(R.id.galleryPicTwo);
        galleryPicThree = view.findViewById(R.id.galleryPicThree);
        finish = view.findViewById(R.id.finish);
    }

    private void setupButtonListeners() {
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri photoUri = data.getData();
            // Do something with the photo based on Uri
            Bitmap selectedImage = null;
            byte[] img = null;
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri);
                img = Utils.getbytearray(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (requestCode == PICK_PHOTO_CODE){ // top left corner - profile pic
                loadPhoto(context, photoUri, profilePic);
            } else if (requestCode == PICK_PHOTO_CODE + 1) { // top right corner - first gallery pic
                loadPhoto(context, photoUri, galleryPicOne);
            } else if (requestCode == PICK_PHOTO_CODE + 2) { // bottom right corner - second gallery pic
                loadPhoto(context, photoUri, galleryPicTwo);
            } else if (requestCode == PICK_PHOTO_CODE + 3) { // bottom left corner - third gallery pic
                loadPhoto(context, photoUri, galleryPicThree);
            }

            // TODO - decide how we'll setup users in this new design and save the info below
//            ParseFile imageFile = new ParseFile(currUser.getObjectId() + "profilepic.jpg", img);
//            currUser.put("profilePic", imageFile);
//            currUser.saveInBackground();
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
