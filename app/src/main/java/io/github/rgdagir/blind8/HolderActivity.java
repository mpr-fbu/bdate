package io.github.rgdagir.blind8;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

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
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.shadowMediumGrey));

        conversation = (Conversation) getIntent().getSerializableExtra("conversation");
        ProfileFragment profileFragment = ProfileFragment.newInstance(conversation);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, profileFragment).commit();

        arrowBack = findViewById(R.id.goBackArrow);
        Glide.with(getApplicationContext()).load(R.drawable.ic_back_arrow).asBitmap().centerCrop().into(new BitmapImageViewTarget(arrowBack) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                arrowBack.setImageDrawable(circularBitmapDrawable);
            }
        });
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
