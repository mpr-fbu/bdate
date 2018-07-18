package io.github.rgdagir.mpr;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements ChatsFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener {

    //FragmentTransaction fragmentTransaction;

    ChatsFragment fragment1 = new ChatsFragment();
    SearchFragment fragment2 = new SearchFragment();
    ProfileFragment fragment3 = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //set chats fragment as initial
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, fragment1).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch(item.getItemId()) {
                    case R.id.action_chats:
                        FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
                        fragmentTransaction1.replace(R.id.flContainer, fragment1).commit();
                        return true;
                    case R.id.action_search:
                        FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
                        fragmentTransaction2.replace(R.id.flContainer, fragment2).commit();
                        return true;
                    case R.id.action_profile:
                        FragmentTransaction fragmentTransaction3 = fragmentManager.beginTransaction();
                        fragmentTransaction3.replace(R.id.flContainer, fragment3).commit();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
