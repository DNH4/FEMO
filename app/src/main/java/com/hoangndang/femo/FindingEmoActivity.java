package com.hoangndang.femo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.Random;

public class FindingEmoActivity extends AppCompatActivity {
    public static Integer SHOW_TIME_DURATION = 2000;

    private Button startButton;
    private ImageButton vibration_enable;
    private ImageButton sound_enable;
    private LinearLayout viewA;
    private FloatingActionButton mFindingEMO;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private int viewAHeight;
    private int viewAWidth;
    private Path path;
    private int mButtonSize;


    private int mCurrentColor;
    private boolean mFlagVibration;
    private boolean mFlagSound;
    private boolean mHidingEMOflag;

    private boolean isDrawn = false;

    private int mDefaultPosX;
    private int mDefaultPosY;
    private int currentPositionX;
    private int currentPositionY;
    private Random mRandom;
    private GameSound gameSound;
    private Handler mHandler;
    private Vibrator vibrator;


    @Override
    protected void onResume() {
        super.onResume();
        gameSound = new GameSound(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //*****Window transition animation******
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

            getWindow().setExitTransition(new Explode().setDuration(750));

        }
        //*****FIN Window transition animation******
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finding_emo);
        //*********** Initialize ********************
        viewA = (LinearLayout) findViewById(R.id.viewA);
        mCurrentColor = getResources().getColor(R.color.colorPrimary);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        mFlagVibration = sharedPref.getBoolean(getString(R.string.flag_vibration), true);//default is true
        mFlagSound = sharedPref.getBoolean(getString(R.string.flag_sound), true);//default is true
        mFindingEMO = (FloatingActionButton) findViewById(R.id.EMO);

        updateBackground();
        //*********** FIN Initialize ********************

        //Setting welcome screen animation
        viewA.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(!isDrawn){
                    mHidingEMOflag = true;

                    mRandom = new Random();

                    isDrawn=true;
                    viewAHeight = viewA.getHeight();
                    viewAWidth = viewA.getWidth();

                    // converting button size to pixel
                    DisplayMetrics metrics = getResources().getDisplayMetrics();
                    mButtonSize = (int) (metrics.density * GameFragment.BUTTON_SIZE + 0.5f);

                    //start the welcome animation
                    mDefaultPosX = viewAWidth / 2-mButtonSize/2;
                    mDefaultPosY = viewAHeight / 3;

                    currentPositionX = mDefaultPosX;
                    currentPositionY = mDefaultPosY;

                    FindingEMOAnimation();
                }

            }
        });


        //*** Set up button to start the game *****
        startButton = (Button) findViewById(R.id.button_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FindingEmoActivity.this,GameActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(i, ActivityOptions.makeSceneTransitionAnimation(FindingEmoActivity.this).toBundle());
                }else{
                    startActivity(i);
                }
            }
        });
        //*** FIN Set up button to start the game *****

        //*** Set up up Pref. buttons ***//
        vibration_enable = (ImageButton) findViewById(R.id.image_button_vibration_enable);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if(!vibrator.hasVibrator()){ //if device doesn't have vibrator
            vibration_enable.setImageDrawable(getDrawable(R.drawable.vibration_disable));
            vibration_enable.setEnabled(false);
            mFlagVibration = false;
            editor.putBoolean(getString(R.string.flag_vibration), mFlagVibration);//update flag
            editor.apply();
        }else if(!mFlagVibration){
            vibration_enable.setImageDrawable(getDrawable(R.drawable.vibration_disable));
        }

        vibration_enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFlagVibration){
                    vibration_enable.setImageDrawable(getDrawable(R.drawable.vibration_disable));
                    mFlagVibration = false;

                }else {
                    vibration_enable.setImageDrawable(getDrawable(R.drawable.vibration_enable));
                    mFlagVibration = true;
                    //vibrate once
                    //Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(200);
                }
                boolean check1 = sharedPref.getBoolean(getString(R.string.flag_vibration), true);
                editor.putBoolean(getString(R.string.flag_vibration),mFlagVibration);//update flag
                editor.apply();
                boolean check2 = sharedPref.getBoolean(getString(R.string.flag_vibration), true);
                updateBackground();//update background
            }
        });

        sound_enable = (ImageButton) findViewById(R.id.image_button_sound_enable);
        if(!mFlagSound){
            sound_enable.setImageDrawable(getDrawable(R.drawable.sound_disable));
        }
        sound_enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFlagSound){
                    sound_enable.setImageDrawable(getDrawable(R.drawable.sound_disable));
                    mFlagSound = false;
                    gameSound.mute(mFlagSound);

                }else {
                    sound_enable.setImageDrawable(getDrawable(R.drawable.sound_enable));

                    mFlagSound = true;
                    gameSound.mute(mFlagSound);
                }
                gameSound.play(getResources().getString(R.string.sound_find_emo), 0.7f,0);
                //mSoundEnableFlag=!mSoundEnableFlag;
                editor.putBoolean(getString(R.string.flag_sound),mFlagSound);//update flag
                editor.apply();
                updateBackground();//update background
            }
        });

        //set up floating button touch
        mFindingEMO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHidingEMOflag = !mHidingEMOflag;
                if (mHidingEMOflag) {
                    //Handler handler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hidingEMO();
                        }
                    }, SHOW_TIME_DURATION);
                } else {
                    AppCompatDialog creditDialog = new CreditDialog(FindingEmoActivity.this);
                    creditDialog.setTitle("Credit");
                    creditDialog.show();
                }
            }
        });

        //*** FIN Set up up Pref. buttons ***//

    }


    public void updateBackground(){

        final int newColor = updateColor();



        final ObjectAnimator objectAnimator = ObjectAnimator
                .ofArgb(viewA, "backgroundColor", mCurrentColor, newColor)
                .setDuration(700);
        objectAnimator.setAutoCancel(true);//Auto canceled when any other ObjectAnimator with the same target and properties is started
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentColor = newColor;
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentColor = (int)objectAnimator.getAnimatedValue();
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        objectAnimator.start();
    }

    public int updateColor(){
        int newColor;
        if(mFlagVibration && mFlagSound){
            newColor = getResources().getColor(R.color.colorPrimary);
        }else if (mFlagVibration || mFlagSound) {
            newColor = getResources().getColor(R.color.colorPrimaryChange1);
        }else{
            newColor = getResources().getColor(R.color.colorPrimaryChange2);
        }

        return newColor;
    }

    public void FindingEMOAnimation(){


        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mFindingEMO.getLayoutParams();

        params.leftMargin = currentPositionX;
        params.topMargin = currentPositionY;

        mFindingEMO.setLayoutParams(params);
        // Button is at current position
        // Hide button after 500ms and move it randomly around
        //hidingEMO();
        //Handler handler = new Handler();
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hidingEMO();
            }
        }, SHOW_TIME_DURATION);

    }

    public void hidingEMO(){
        mFindingEMO.hide();


        // get Random position to move: b/w width
        //final Random random = new Random();
        int randomPos = mRandom.nextInt(2 * viewAWidth / 5) + (viewAWidth / 5 + mButtonSize / 2); //Margin is 1/5 + 1/5 = 2/5 width then move back Margin

        //Getting the path
        path = new Path();
        path.moveTo(currentPositionX, currentPositionY); //move to original position
        path.lineTo(randomPos, currentPositionY);

        final ObjectAnimator moveEMO = ObjectAnimator
                .ofFloat(mFindingEMO, View.X, View.Y, path); // API 21
                //.ofFloat(mFindingEMO, "x", (float)currentPositionX, (float)currentPositionY); // for API 16
        moveEMO.setDuration(2000);
        moveEMO.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //mFindingEMO.hide();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentPositionX = (int) mFindingEMO.getX();
                currentPositionY = (int) mFindingEMO.getY();
                //mFindingEMO.show();
                if (mHidingEMOflag) {
                    //Handler handler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hidingEMO();
                        }
                    }, SHOW_TIME_DURATION);
                } else { // mdo nothing FEMO will stop on track
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        //moveEMO.setRepeatCount(ValueAnimator.INFINITE);
        moveEMO.start();

        //show in b/w to see the moving
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //change color
                mFindingEMO.setBackgroundTintList(ColorStateList.valueOf
                        (RandomUtilities.randomSimilarColor
                                (getResources().getColor(R.color.colorAccent))));
                mFindingEMO.show();
                gameSound.play(getString(R.string.sound_intro),0.9f,0);
            }
        }, 700);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameSound.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameSound.release();
        //reset tutorial to true.
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.flag_tutorial), true);//update flag
        editor.apply();
    }
}
