package io.github.rgdagir.mpr;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ChatsListFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener {

    ChatsListFragment initialFragment = new ChatsListFragment();
    private static final int LOCATION_PERMISSION_REQUEST = 1;

    // set up broadcast receiver for push notifications
    private BroadcastReceiver mBroadcastReceiver = new CustomPushReceiver();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLocationPermissions();

        final FragmentManager fragmentManager = getSupportFragmentManager();


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

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getLocationPermissions(){
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If the statement above is true, then no permission was granted
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                // if the statement above is false, it means that the user has previously denied the request for that permission
                // TODO - give user an explanation of how location will be used before the actual request is made
            }
            // Request permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        }
    }

    // Callback function for location request
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_SHORT).show();
            } else {
                boolean showRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
                if (showRationale) {
                    // TODO - handle this situation when the user has denied permission
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
