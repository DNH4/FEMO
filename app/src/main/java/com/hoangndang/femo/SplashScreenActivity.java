package com.hoangndang.femo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by hdang on 3/30/2016.
 */
public class SplashScreenActivity extends AppCompatActivity {
    Handler handler;
    Runnable runnable;
    RelativeLayout mSplashScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {
                startHomeScreen();
            }
        };

        handler.postDelayed(runnable,2000);

        mSplashScreen = (RelativeLayout) findViewById(R.id.splash_screen);
        mSplashScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                startHomeScreen();
            }
        });

    }

    public void startHomeScreen(){
        mSplashScreen.setClickable(false);//disable click
        Intent intent = new Intent(this,FindingEmoActivity.class);
        startActivity(intent);
    }
}
