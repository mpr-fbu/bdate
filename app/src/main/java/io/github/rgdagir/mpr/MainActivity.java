package io.github.rgdagir.mpr;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements ChatsListFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener {

    ChatsListFragment initialFragment = new ChatsListFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();


    // set up broadcast receiver for push notifications
    private BroadcastReceiver mBroadcastReceiver = new CustomPushReceiver();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // set chats fragment as initial
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, initialFragment).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch(item.getItemId()) {
                    case R.id.action_chats:
                        switchFragment(fragmentManager.beginTransaction(), initialFragment);
                        return true;
                    case R.id.action_search:
                        switchFragment(fragmentManager.beginTransaction(), new SearchFragment());
                        return true;
                    case R.id.action_profile:
                        switchFragment(fragmentManager.beginTransaction(), new ProfileFragment());
                        return true;
                }
                return false;
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(CustomPushReceiver.intentAction));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(CustomPushReceiver.intentAction));
    }

    public static void switchFragment(FragmentTransaction fragmentTransaction, Fragment fragment) {
        fragmentTransaction.replace(R.id.flContainer, fragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        goToEditProfile();
    }

    public void goToEditProfile() {
        switchFragment(fragmentManager.beginTransaction(), new EditProfileFragment());
    }
}
