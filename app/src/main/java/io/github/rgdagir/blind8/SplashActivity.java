package io.github.rgdagir.blind8;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView heartLogo = findViewById(R.id.heartLogo);
        pulseAnimation(heartLogo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,       MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1300);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    public void pulseAnimation(View heartLogo) {
        ObjectAnimator objAnim= ObjectAnimator.ofPropertyValuesHolder(heartLogo, PropertyValuesHolder.ofFloat("scaleX", 1.5f), PropertyValuesHolder.ofFloat("scaleY", 1.5f));
        objAnim.setDuration(300);
        objAnim.setRepeatMode(ObjectAnimator.REVERSE);
        objAnim.setRepeatCount(3);
        objAnim.start();
    }
}
