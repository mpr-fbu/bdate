package io.github.rgdagir.blind8;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import io.github.rgdagir.blind8.models.Conversation;

public class HolderActivity extends AppCompatActivity
        implements ProfileFragment.OnFragmentInteractionListener {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Conversation conversation;
    private ImageView arrowBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holder);

        conversation = (Conversation) getIntent().getSerializableExtra("conversation");
        ProfileFragment profileFragment = ProfileFragment.newInstance(conversation);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, profileFragment).commit();

        arrowBack = findViewById(R.id.goBackArrow);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        conversation = (Conversation) getIntent().getSerializableExtra("conversation");
        ProfileFragment profileFragment = ProfileFragment.newInstance(conversation);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, profileFragment).commit();
    }

    @Override
    public void goToEditProfile() {
        // do nothing since this is someone else's profile
    }
}
