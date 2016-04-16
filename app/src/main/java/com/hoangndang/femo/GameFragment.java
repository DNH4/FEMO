package com.hoangndang.femo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;

/**
 * Created by hdang on 3/3/2016.
 */
public class GameFragment extends Fragment {
    public static final long VIBRATE_DURATION = 200; // setting default vibrator setting is 200 ms
    public static final String TAG = "GameFragment";
    public static final float BUTTON_SIZE = 56f;
    public static final String EXTRA_SCORE = "score";
    public static final String EXTRA_EMO_CAUGHT = "EMO_caught";
    public static final int GAME_OVER_REQUEST = 1;
    public static final int SEARCH_COUNT_LIFE = 21;
    public static final int LIFE_GAIN = 3;


    private RelativeLayout mBackground;
    private Vibrator mVibrator;
    private int mEMOPosX;
    private int mEMOPosY;
    private int mTouchPosX;
    private int mTouchPosY;
    private int mButtonSize;
    private int mTolerance;
    private int mCurrentColor;
    private int mHeight;
    private int mWidth;
    private boolean mFlagVibration;
    private boolean mFlagSound;
    private boolean mFlagStartActivity;
    private boolean isAds;

    private int mSearchCount;
    private TextView tvLife;
    private TextView tvScore;
    private TextView tvLifeGain;
    private int mScore;

    private FloatingActionButton currentEMO;
    private FloatingActionButton previousEMO;

    private ObjectAnimator screenFlashing;

    private GameSound mGameSound;

    private boolean flagTouchEnable;


    private ArrayList<FEMO> mFEMOList;

    boolean toggleAddEmo = true;

    private SharedPreferences sharedPref;

    private InterstitialAd mInterstitialAd;

    public static GameFragment newInstance() {
        GameFragment fragment = new GameFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentColor = getResources().getColor(R.color.colorPrimary);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mWidth = size.x;
        mHeight = size.y;
        //show tutorial
/*        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.flag_tutorial), true);//update flag
        editor.apply();*/

        // !@#$ Need to fix to show only if close app
        //Intent i = new Intent(getActivity(),TutorialActivity.class);
        //startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GAME_OVER_REQUEST){
            //reset the game
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new GameFragment())
                    .commit();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGameSound.release();
    }

    @Override
    public void onPause() {
        super.onPause();
        mFlagSound=false;
        mGameSound.mute(mFlagSound);
        mFlagVibration=false;
        mFlagStartActivity = false;
        //cancel vibrator callback
    }

    @Override
    public void onResume() {
        super.onResume();
        mFlagVibration = sharedPref.getBoolean(getString(R.string.flag_vibration),false);//default is true
        mFlagSound = sharedPref.getBoolean(getString(R.string.flag_sound),false);
        mGameSound.mute(mFlagSound);
        mFlagStartActivity = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_game, container, false);

        //***** Initialize ***************************************************************************
        mSearchCount = SEARCH_COUNT_LIFE; //limit player to have 20 search count
        mScore = 0;
        flagTouchEnable = true;

        //Getting Preference
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mFlagVibration = sharedPref.getBoolean(getString(R.string.flag_vibration), true);//default is true
        mFlagSound = sharedPref.getBoolean(getString(R.string.flag_sound),true);

        //Link sound
        mGameSound = new GameSound(getActivity());

        mGameSound.mute(mFlagSound);

        //Initialize Ads
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));// !@#$ change to test_interstitial_ad_unit_id for testing
        AdUtilities.requestNewInterstitial(mInterstitialAd);
        //**** Fin Initialize ******

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                AdUtilities.requestNewInterstitial(mInterstitialAd);
                flagTouchEnable = true;//reenable touch screen
            }
        });


        screenFlashing = new ObjectAnimator();

        mFEMOList = new ArrayList<>();

        // converting button size to pixel
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mButtonSize = (int) (metrics.density * BUTTON_SIZE + 0.5f);
        mTolerance = (int) (metrics.density * 0f +0.5f);

        //set Vibrator
        mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        //Display Search count;
        tvLife = (TextView) v.findViewById(R.id.tv_life);
        tvScore = (TextView) v.findViewById(R.id.tv_score);
        tvLifeGain = (TextView) v.findViewById(R.id.tv_life_gain);
        updateUI();


        //set Background
        mBackground = (RelativeLayout)v.findViewById(R.id.layout); // Attach background
        mBackground.setBackgroundColor(mCurrentColor);
        mBackground.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // While user is touching the screen
                mTouchPosX =(int) event.getX();
                mTouchPosY =(int) event.getY();

                if (flagTouchEnable) {
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            //Log.i(TAG, "ACTION_DOWN !");
                            if(toggleAddEmo){
                                addEMO(); // Create new FEMO
                                toggleAddEmo =false;
                            }
                            // Scan position to find FEMO
                            findingEMO();
                            updateUI(); //update UI after

                            //Game over if search count is 0
                            if(mSearchCount==0){

                                //play losing animation
                                currentEMO.show();
                                ObjectAnimator didntCatch = ObjectAnimator.ofFloat(currentEMO, "rotation", 0, 540f)
                                        .setDuration(1000);
                                didntCatch.setInterpolator(new OvershootInterpolator());
                                didntCatch.setRepeatMode(ValueAnimator.REVERSE);
                                didntCatch.setRepeatCount(2); //repeat 2 times to allow the sound to be played
                                didntCatch.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        // play game over sound
                                        flagTouchEnable = false;
                                        mGameSound.play(getResources().getString(R.string.sound_game_over),1.0f,0);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        if(mFlagStartActivity){//make sure that didn't call when back button is used to when animation is ending
                                            Intent i = new Intent(getActivity(), GameOverActivity.class);
                                            //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            i.putExtra(EXTRA_SCORE, mScore);
                                            i.putExtra(EXTRA_EMO_CAUGHT, mFEMOList);
                                            startActivityForResult(i, GAME_OVER_REQUEST);
                                            flagTouchEnable = true;
                                        }
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {
                                    }
                                });
                                didntCatch.start();
                            }

                            return true;
                        case MotionEvent.ACTION_UP:
                            //Log.i(TAG, "ACTION_UP !");
                            //handler.removeCallbacks(mLongPressed);
                            //mVibrator.cancel(); // Stop vibrating when hand is not touching
                            return true;
                        case MotionEvent.ACTION_CANCEL:
                            //handler.removeCallbacks(mLongPressed);
                            //mVibrator.cancel(); // Stop vibrating when hand is not touching
                            //Log.i(TAG,"ACTION_CANCEL !");
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            return true;
                        default:
                            return false;
                    }
                } else {
                    return false;
                }
            }
        });
        //***** FIN set Vibrating action ********************************************************************

        //*******************************************************************************************
        return v;
    }

    public void updateUI(){

        tvLife.setText(String.valueOf(mSearchCount));
        tvScore.setTextColor(mCurrentColor);
        tvScore.setText(String.valueOf(mScore));
    }


    //***************************************************
    // This method will initiate beats as per interval
    private void findingEMO(){
        boolean check;//!@#$ test
        check = isFound(mTouchPosX,mTouchPosY); // check if click is found
        //Check if found FEMO
        Log.i(TAG,"Touch X =  " + mTouchPosX + "Touch Y = " + mTouchPosY);
        Log.i(TAG, "Pos X =  " + mEMOPosX + "Pos Y = "+ mEMOPosY);
        Log.i(TAG,"isFound()= "+check);


        //If Found the button
        if(check){
            //update score and life
            mSearchCount += LIFE_GAIN;
            mScore += 100;
            //**** Add animation for score increasing ********
            String lifeGain = "+" + LIFE_GAIN;
            tvLifeGain.setText(lifeGain);

            ObjectAnimator animationLifeGain = ObjectAnimator
                    //.ofArgb(tvLifeGain,"textColor",getResources().getColor(R.color.colorPrimary),getResources().getColor(R.color.colorTransparent)) // API 21
                    .ofInt(tvLifeGain, "textColor", getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorTransparent))
                    .setDuration(1000);
            animationLifeGain.setEvaluator(new ArgbEvaluator());
            animationLifeGain.start();


            //*** Fin Add animation ********

            if(screenFlashing.isRunning()){ //cancel screen flashing if running
                screenFlashing.cancel();
            }
            flagTouchEnable = false;
            //play sound
            mGameSound.play(getResources().getString(R.string.sound_found_emo),1.0f,0);
            //start animation
            foundEmoAnimation();
            //Toggle to add new button
            toggleAddEmo =true;


        } else {
            mSearchCount--;
            //getting the distance b/w device and FEMO and touch
            int distToEMO=findDistance(mEMOPosX, mEMOPosY,mTouchPosX,mTouchPosY);
            int maxDist = findDistance(0, 0, mHeight, mWidth);

            int mInterval = (int)((float)(distToEMO)/(float)maxDist*2000+VIBRATE_DURATION);

            //long[] mVibration = {0,VIBRATE_DURATION,distToEMO}; // first 0 mean start immediately, vibrate for VIBRATE_DURATION, sleep for interval
            //TEST//mVibrator.vibrate(mVibration,0);// -1 mean don't repeat, 0 mean repeat until cancel
            screenFlash(mInterval);
        }
    }
    //***************************************************

    // This method will add a floating button which is FEMO to the screen in hidden form
    public void addEMO(){

        //****** Get Random position to add FEMO ***************

        //*********** Adding FEMO *************
        FEMO newFEMO = new FEMO(mHeight,mWidth,mButtonSize,mCurrentColor);
        //check if FEMO isAd
        isAds = newFEMO.isAd();
        // Make sure that the first FEMO is not Ads
        if(isAds && (mFEMOList.size()<1)){
            addEMO(); // Add a different FEMO
            return; //cancel this instance of addEMO()
        }
        //*********** END Adding FEMO *********

        //FloatingActionButton fab = newFEMO.getFab();
        FloatingActionButton fab = new FloatingActionButton(getActivity());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);


        params.leftMargin = newFEMO.getXpos();
        params.topMargin = newFEMO.getYpos();

        //Update FEMO position
        mEMOPosX = newFEMO.getXpos();
        mEMOPosY = newFEMO.getYpos();

        fab.setLayoutParams(params);
        fab.setVisibility(View.GONE);
        mBackground.addView(fab);

        //fab.setImageDrawable(getActivity().getDrawable(newFEMO.getFEMOFace()));
        fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), newFEMO.getFEMOFace()));
        //fab.setImageTintList(ColorStateList.valueOf(getActivity().getResources().getColor(R.color.colorTextIcon))); //API 21
        fab.setColorFilter(getActivity().getResources().getColor(R.color.colorTextIcon)); //!@#$
        fab.setBackgroundTintList(ColorStateList.valueOf(newFEMO.getColor()));
        //fab.setImageTintList(ColorStateList.valueOf(mCurrentColor));


        //ADD FEMO
        mFEMOList.add(newFEMO);

        // updating
        previousEMO = currentEMO;
        currentEMO =  fab;
    }

    private boolean isFound(int xPos, int yPos){
        //int tolerance = 100;
        int trueRange = mButtonSize;
        Log.i(TAG,"trueRange  " + trueRange);
        Log.i(TAG,"clickX+ " + (mEMOPosX +trueRange) + "clickX- " + (mEMOPosX -trueRange));
        Log.i(TAG,"clickY+ " + (mEMOPosY +trueRange) + "clickY- " + (mEMOPosY -trueRange));

        if((((mEMOPosX +trueRange) > xPos) && (xPos > (mEMOPosX -trueRange))) &&
                (((mEMOPosY +trueRange) > yPos) && (yPos > (mEMOPosY -trueRange)))){//found FEMO
            currentEMO.setVisibility(View.VISIBLE);
            return true;
        }else {
            return false;
        }
    }

    private int findDistance(int posX1,int posY1,int posX2,int posY2){
        return (int)Math.sqrt(Math.pow(posX1 - posX2, 2) + Math.pow(posY1- posY2, 2));
    }

    private void foundEmoAnimation(){

        if(previousEMO !=null){
            previousEMO.hide();
        }

        //Move the button up
        //** create Path **** API 21
/*        Path path = createPath();
            *//*path.moveTo(FEMO.get(FEMO.size()-1).getX(),FEMO.get(FEMO.size()-1).getY());
            path.lineTo(500, 500);//test*//*
        ObjectAnimator moveEMO = ObjectAnimator
                .ofFloat(currentEMO, View.X, View.Y, path);*/
        // API 21

        //Lower API:
        float newX = mBackground.getWidth() / 2 - mButtonSize / 2;
        float newY = mButtonSize / 6;
        ObjectAnimator moveEMOX = ObjectAnimator
                .ofFloat(currentEMO,"x",(float)mEMOPosX,newX);
        moveEMOX.setDuration(700)
                .setInterpolator(new AccelerateDecelerateInterpolator());
        ObjectAnimator moveEMOY = ObjectAnimator
                .ofFloat(currentEMO,"y",(float)mEMOPosY,newY);
        moveEMOY.setDuration(700)
                .setInterpolator(new AccelerateDecelerateInterpolator());

        //Transition the screen
        int tempCurrentColor = mCurrentColor;
        mCurrentColor = RandomUtilities.randomSimilarColor(mCurrentColor);
        final ObjectAnimator changeBackground = ObjectAnimator
                .ofInt(mBackground, "backgroundColor", tempCurrentColor,mCurrentColor )
                .setDuration(1000);
        changeBackground.setEvaluator(new ArgbEvaluator());
        //changeBackground.start();

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet
                .play(moveEMOX)
                .with(moveEMOY)
                .before(changeBackground);
        animatorSet.start();

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                flagTouchEnable = true;
                if(isAds){//if FEMO is for ads --> show ads
                    mInterstitialAd.show();
                    //AdUtilities.requestNewInterstitial(mInterstitialAd);//download a new ad
                    flagTouchEnable = false; // disable touching screen
                    isAds = false; // reset ads flag
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    private void screenFlash(int interval){

        //Def. saturation is 76, B is 100, increment Hua by 20
        float[] currentColorHSV = new float[3];
        Color.colorToHSV(mCurrentColor,currentColorHSV);
        //currentColorHSV[1] = 0.40f; //setting saturation to be lighter 40%
        currentColorHSV[2] = 0.80f; //lowering brightness to 80%
        int flashColor = Color.HSVToColor(currentColorHSV);

        if(screenFlashing.isRunning()){
            screenFlashing.cancel();
        }

        screenFlashing = ObjectAnimator
                .ofInt(mBackground, "backgroundColor", flashColor, mCurrentColor)
                .setDuration(interval);
                //.setDuration(VIBRATE_DURATION);

        screenFlashing.setEvaluator(new ArgbEvaluator());
        screenFlashing.setRepeatCount(1);
        //screenFlashing.setRepeatCount(ValueAnimator.INFINITE);



        screenFlashing.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(mFlagVibration) {
                    mVibrator.vibrate(VIBRATE_DURATION);
                }
                //if(mFlagSound){
                    mGameSound.play(getResources().getString(R.string.sound_find_emo),0.7f,0);
                //}
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //screenFlashing.setStartDelay(500);
                //screenFlashing.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                if(mFlagVibration) {
                    mVibrator.vibrate(VIBRATE_DURATION);
                }
                //if(mFlagSound){
                    mGameSound.play(getResources().getString(R.string.sound_find_emo),0.65f,0);
                //}
            }
        });

        screenFlashing.start();
    }
    /**
     * creatPath(int position)
     * create Path for FEMO to move to
     *
     * position: the order of where the FEMO will be
     * */
    private Path createPath(){

        Path path = new Path();
        //Log.i(TAG,"currentEMO: X-" + currentEMO.getX()+" Y-" + currentEMO.getY());
        path.moveTo(mEMOPosX, mEMOPosY); //move to original position
        path.lineTo(mBackground.getWidth() / 2 - mButtonSize / 2, mButtonSize / 6);
        //path.lineTo(0,0); //TESTING

        return path;

    }



}
