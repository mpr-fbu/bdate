package io.github.rgdagir.mpr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.parse.ParseUser;

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
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
