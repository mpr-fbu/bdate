package io.github.rgdagir.mpr;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditProfileFragment extends Fragment {
    private Context context;
    private EditText editName;
    private EditText editEmail;
    private EditText editWebpage;
    private EditText editBio;
    private EditText editBirthDate;
    private FloatingActionButton changeprofilePic;
    private Spinner myGenderSpinner;
    private Spinner interestedInSpinner;
    private SeekBar rangeSeekBar;
    private ImageView profilePic;
    private ParseUser currUser;
    private TextView displayProgress;
    private int rangeMatch;
    private Button submitEdits;
    private HashMap changes;
    private EditProfileFragment.OnFragmentInteractionListener mListener;
    private Button dateBtn;


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
        setupSpinners(v);
        setupRangeBar();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void setupViews(View v){
        // associating views from xml file with the Java class
        editName = v.findViewById(R.id.editName);
        editEmail = v.findViewById(R.id.editEmail);
        editWebpage = v.findViewById(R.id.editWebpage);
        editBio = v.findViewById(R.id.editBio);
        editBirthDate = v.findViewById(R.id.editBirthDate);
        changeprofilePic = v.findViewById(R.id.changeProfilePicBtn);
        profilePic = v.findViewById(R.id.profilePic);
        myGenderSpinner = v.findViewById(R.id.myGender);
        interestedInSpinner = v.findViewById(R.id.interestedInGender);
        rangeSeekBar = v.findViewById(R.id.rangeSeekBar);
        displayProgress = v.findViewById(R.id.displayProgress);
        submitEdits = v.findViewById(R.id.submitEdits);
        dateBtn = v.findViewById(R.id.dateBtn);

        // adding listeners to buttons
        changeprofilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        submitEdits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUpdatedUser();
                mListener.goBackToProfile();
            }
        });

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment fragment2 = new DatePickerFragment();
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer,fragment2,"tag");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

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

        editWebpage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                changes.put("webpage", s.toString());
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
    }

    public void fetchCurrentUserAndLoadPage(){
        // populate screen
        editName.setText(currUser.getString("firstName"));
        editEmail.setText(currUser.getString("email").toString());
        editWebpage.setText(currUser.getString("webpage").toString());
        editBio.setText(currUser.getString("bio").toString());
        Date inputDate = currUser.getDate("dob");
        DateFormat dataFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        String strDate = dataFormat.format(inputDate);
        DateFormat inputFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date date = null;
        try {
            date = inputFormatter.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat outputFormatter = new SimpleDateFormat("MM/dd/yyyy");
        String output = outputFormatter.format(date);
        editBirthDate.setText(output);
        Glide.with(context).load(currUser.getParseFile("profilePic").getUrl())
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
    }

    public void setupSpinners(View v){
        String userGender = currUser.getString("gender");
        String userInterest = currUser.getString("interestedIn");
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
    }

    public void setupRangeBar(){
        rangeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                displayProgress.setText(Integer.toString(progress));
                rangeMatch = progress;
                //changes.put("matchRange", Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void saveUpdatedUser(){
        Iterator it = changes.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry)it.next();
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

}
