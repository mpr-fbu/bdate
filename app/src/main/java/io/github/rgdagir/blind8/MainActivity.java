package io.github.rgdagir.blind8;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import io.github.rgdagir.blind8.utils.CustomPushReceiver;


public class MainActivity extends AppCompatActivity
        implements ChatsListFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        SearchFragment.OnFragmentInteractionListener, EditProfileFragment.OnFragmentInteractionListener {

    BottomNavigationView bottomNavigationView;
    ViewPager viewPager;
    MenuItem prevMenuItem;
    FrameLayout flContainer;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private ViewPagerAdapter vpAdapter = new ViewPagerAdapter(getSupportFragmentManager());


    // set up broadcast receiver for push notifications
    private BroadcastReceiver mBroadcastReceiver = new CustomPushReceiver();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        flContainer = findViewById(R.id.flContainer);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager = findViewById(R.id.viewPager);
        resizeMenuIcons(bottomNavigationView);

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(CustomPushReceiver.intentAction));

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                viewPager.setVisibility(View.VISIBLE);
                flContainer.setVisibility(View.INVISIBLE);
                switch (item.getItemId()) {
                    case R.id.action_chats:
                        viewPager.setCurrentItem(0);
                        return true;
                    case R.id.action_search:
                        viewPager.setCurrentItem(1);
                        return true;
                    case R.id.action_profile:
                        viewPager.setCurrentItem(2);
                        return true;
                }
                return false;
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ChatsListFragment chatsListFragment = new ChatsListFragment();
        SearchFragment searchFragment = new SearchFragment();
        ProfileFragment profileFragment = new ProfileFragment();
        vpAdapter.addFragment(chatsListFragment);
        vpAdapter.addFragment(searchFragment);
        vpAdapter.addFragment(profileFragment);
        viewPager.setAdapter(vpAdapter);
        viewPager.setCurrentItem(0);
    }

    private void resizeMenuIcons(BottomNavigationView bottomNavigationView) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(android.support.design.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, displayMetrics);
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(CustomPushReceiver.intentAction));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        goToEditProfile();
        goBackToProfile();
    }

    public static void switchFragment(FragmentTransaction fragmentTransaction, Fragment fragment) {
        fragmentTransaction.replace(R.id.flContainer, fragment).commit();
    }

    public void goToEditProfile() {
        viewPager.setVisibility(View.INVISIBLE);
        flContainer.setVisibility(View.VISIBLE);
        switchFragment(fragmentManager.beginTransaction(), new EditProfileFragment());
    }

    public void goBackToProfile() {
        viewPager.setVisibility(View.VISIBLE);
        viewPager.getAdapter().notifyDataSetChanged();
        flContainer.setVisibility(View.INVISIBLE);
        viewPager.setCurrentItem(2);
    }
}
