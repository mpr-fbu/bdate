package io.github.rgdagir.blind8;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import io.github.rgdagir.blind8.models.Conversation;

public class HolderActivity extends AppCompatActivity
        implements ProfileFragment.OnFragmentInteractionListener {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Conversation conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holder);

        conversation = (Conversation) getIntent().getSerializableExtra("conversation");
        ProfileFragment profileFragment = ProfileFragment.newInstance(conversation);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, profileFragment).commit();
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
