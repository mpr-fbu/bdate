package io.github.rgdagir.mpr.sign_up;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.rgdagir.mpr.R;

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
                mListener.createNewUser();
            }
        });

        profilePic.setOnClickListener(selectPhoto);
        galleryPicOne.setOnClickListener(selectPhoto);
        galleryPicTwo.setOnClickListener(selectPhoto);
        galleryPicThree.setOnClickListener(selectPhoto);
    }

    private View.OnClickListener selectPhoto = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onPickPhoto(v);
        }
    };

    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (i.resolveActivity(context.getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(i, PICK_PHOTO_CODE);
        }
    }
}
