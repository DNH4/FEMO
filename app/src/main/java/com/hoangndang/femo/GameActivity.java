package com.hoangndang.femo;

import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.widget.RelativeLayout;

/**
 * Created by hdang on 3/3/2016.
 */
public class GameActivity extends SingleFragmentActivity {

    public static final long VIBRATE_DURATION = 200; // setting default vibrator setting is 300 ms
    public static final String TAG = "GameActivity";


    private RelativeLayout mBackground;
    private Vibrator mVibrator;

    boolean testToggle=true;


    @Override
    protected Fragment createFragment() {
        return GameFragment.newInstance();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //*****Window transition animation***************************************************************
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
            getWindow().setAllowEnterTransitionOverlap(false); //This to make sure that the other transition will not be faded
        }
        //*****FIN Window transition animation***************************************************************

        super.onCreate(savedInstanceState);
    }

}
