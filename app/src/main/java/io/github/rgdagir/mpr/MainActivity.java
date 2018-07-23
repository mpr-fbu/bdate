package io.github.rgdagir.mpr;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements ChatsListFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener {

//    ParseUser currentUser = ParseUser.getCurrentUser();
//    currentUser.logOut();
    ChatsListFragment initialFragment = new ChatsListFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //set chats fragment as initial
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
    }

    public static void switchFragment(FragmentTransaction fragmentTransaction, Fragment fragment) {
        fragmentTransaction.replace(R.id.flContainer, fragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
